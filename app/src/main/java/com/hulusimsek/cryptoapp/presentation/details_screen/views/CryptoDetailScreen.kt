import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.hulusimsek.cryptoapp.data.remote.dto.CryptoItem
import com.hulusimsek.cryptoapp.presentation.details_screen.CryptoDetailViewModel
import com.hulusimsek.cryptoapp.presentation.details_screen.CryptoDetailsEvent
import com.hulusimsek.cryptoapp.presentation.home_screen.CryptosEvent

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
        color = MaterialTheme.colorScheme.background // Burada arka plan rengini ayarlayın
    ) {
        PullToRefreshPage(
            isRefreshing = state.isLoading,
            onRefresh = { viewModel.onEvent(CryptoDetailsEvent.Refresh) }
        ) { contentModifier ->
            LazyColumn(
                modifier = contentModifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp) // Optional: Add padding around the content
            ) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp), // Optional: Add padding to the Box
                        contentAlignment = Alignment.Center // Center both horizontally and vertically
                    ) {
                        // Use a Column to align vertically centered content
                        Column(
                            modifier = Modifier
                                .wrapContentSize() // Wrap content to avoid stretching
                                .align(Alignment.Center) // Center within the Box both horizontally and vertically
                        ) {
                            when {
                                state.isLoading -> CircularProgressIndicator()
                                !state.error.isNullOrEmpty() -> Text(
                                    text = state.error ?: "Error",
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(16.dp)
                                )
                                state.coin != null -> CryptoDetailContent(cryptoItem = state.coin!!)
                                else -> Text("No data available")
                            }
                        }
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
