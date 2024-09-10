import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.hulusimsek.cryptoapp.R
import com.hulusimsek.cryptoapp.data.remote.dto.CryptoItem
import com.hulusimsek.cryptoapp.domain.model.KlineModel
import com.hulusimsek.cryptoapp.presentation.details_screen.CryptoDetailViewModel
import com.hulusimsek.cryptoapp.presentation.details_screen.CryptoDetailsEvent
import com.hulusimsek.cryptoapp.presentation.details_screen.views.CandleStickChartView
import com.hulusimsek.cryptoapp.presentation.home_screen.CryptosEvent
import com.hulusimsek.cryptoapp.presentation.theme.BlueMunsell
import com.hulusimsek.cryptoapp.presentation.theme.dusenKirmizi
import com.hulusimsek.cryptoapp.presentation.theme.yukselenYesil
import com.hulusimsek.cryptoapp.presentation.util.RetryView
import kotlin.math.absoluteValue

@Composable
fun CryptoDetailScreen(
    id: String,
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    val viewModel: CryptoDetailViewModel = hiltViewModel()
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.toastMessage) {
        state.toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            // Mesajı gösterdikten sonra temizle
            viewModel.onEvent(CryptoDetailsEvent.ClearToastMessage)
        }
    }

    LaunchedEffect(id) {
        viewModel.onEvent(CryptoDetailsEvent.LoadCryptoDetails(id))
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background // Arka plan rengini ayarlar
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // İçeriği PullToRefreshPage içinde tutuyoruz
            PullToRefreshPage(
                isRefreshing = state.isLoading,
                onRefresh = { viewModel.onEvent(CryptoDetailsEvent.Refresh) }
            ) { contentModifier ->
                Column {


                    LazyColumn(
                        modifier = contentModifier
                            .fillMaxWidth(),
                        contentPadding = PaddingValues(16.dp) // İçeriğe padding ekler
                    ) {
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                elevation = CardDefaults.cardElevation(4.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    // İsim ve Fiyat aynı yatay hizada
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        // İsim ve Soyisim aynı hizada
                                        Column(
                                            verticalArrangement = Arrangement.Center,
                                            horizontalAlignment = Alignment.Start
                                        ) {
                                            Text(
                                                text = state.coin?.name ?: "",
                                                style = MaterialTheme.typography.headlineLarge,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = "/${state.coin?.surname ?: ""}",
                                                style = MaterialTheme.typography.headlineSmall,
                                                color = Color.Gray,
                                                modifier = Modifier.padding(start = 4.dp)
                                            )
                                        }

                                        // Fiyat Bilgisi ve Yüzdelik Değişim
                                        Column(
                                            horizontalAlignment = Alignment.End
                                        ) {
                                            Text(
                                                text = state.coin?.lastPrice ?: "",
                                                style = MaterialTheme.typography.headlineMedium,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )

                                            // Yüzdelik Değişim Kutusu
                                            Box(
                                                modifier = Modifier
                                                    .background(
                                                        color = if (state.coin?.priceChangePercent?.toDoubleOrNull() ?: 0.0 >= 0.0) yukselenYesil else dusenKirmizi,
                                                        shape = RoundedCornerShape(4.dp)
                                                    )
                                                    .width(75.dp)
                                                    .padding(8.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = "${state.coin?.priceChangePercent}%",
                                                    color = Color.White,
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Diğer Bilgiler
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Sol Kısım: High Price ve Low Price
                                        Column {
                                            Text(
                                                text = stringResource(R.string.high_price),
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    color = Color.Gray
                                                )
                                            )
                                            Text(
                                                text = state.coin?.highPrice ?: "N/A",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = stringResource(R.string.low_price),
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    color = Color.Gray
                                                )
                                            )
                                            Text(
                                                text = state.coin?.lowPrice ?: "N/A",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }

                                        // Sağ Kısım: Volume ve Quote Volume
                                        Column {
                                            Text(
                                                text = stringResource(R.string.volume),
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    color = Color.Gray
                                                )
                                            )
                                            Text(
                                                text = state.coin?.volume ?: "N/A",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = stringResource(R.string.quote_volume),
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    color = Color.Gray
                                                )
                                            )
                                            Text(
                                                text = state.coin?.quoteVolume ?: "N/A",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        // Diğer içeriği buraya ekleyebilirsiniz
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(400.dp)
                    ) {
                        if (state.klines != null) {
                            CandleStickChartView(
                                klines = state.klines!!,
                                modifier = Modifier
                                    .fillMaxSize()
                            )
                        } else {
                            // Yükleniyor veya veri yoksa bir gösterim
                            Text(state.klinesError ?: "Error!!", modifier = Modifier.padding(16.dp))
                        }
                    }
                }
            }

            // Grafik, LazyColumn'ın dışında, kartın hemen altında


            // Yüklenme ve hata gösterimi
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                if (state.isLoading) {
                    CircularProgressIndicator(color = BlueMunsell)
                }
                if (state.error?.isNullOrEmpty() == false) {
                    RetryView(error = state.error!!) {
                        viewModel.onEvent(CryptoDetailsEvent.Refresh)
                    }
                }
            }
        }
    }
}







@Composable
fun CryptoDetailContent(cryptoItem: CryptoItem) {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            Text(
                text = cryptoItem.symbol,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            DetailRow(label = "Last Price", value = cryptoItem.lastPrice)
            DetailRow(label = "Ask Price", value = cryptoItem.askPrice)
            DetailRow(label = "Bid Price", value = cryptoItem.bidPrice)
            DetailRow(label = "Open Price", value = cryptoItem.openPrice)
            DetailRow(label = "High Price", value = cryptoItem.highPrice)
            DetailRow(label = "Low Price", value = cryptoItem.lowPrice)
            DetailRow(label = "Volume", value = cryptoItem.volume)
            DetailRow(label = "Quote Volume", value = cryptoItem.quoteVolume)
            DetailRow(label = "Price Change", value = cryptoItem.priceChange)
            DetailRow(label = "Price Change Percent", value = cryptoItem.priceChangePercent)
            DetailRow(label = "Weighted Avg Price", value = cryptoItem.weightedAvgPrice)
            DetailRow(label = "Open Time", value = cryptoItem.openTime.toString())
            DetailRow(label = "Close Time", value = cryptoItem.closeTime.toString())
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.secondary)
        )
    }
}
