import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.hulusimsek.cryptoapp.model.CryptoItem
import com.hulusimsek.cryptoapp.viewmodel.CryptoDetailViewModel

@Composable
fun CryptoDetailScreen(
    id: String,
    price: String,
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: CryptoDetailViewModel = hiltViewModel()
) {
    val cryptoItem by remember { viewModel.cryptoItems }
    val isLoading by remember { viewModel.isLoading }
    val errorMessage by remember { viewModel.errorMessage }

    LaunchedEffect(Unit) {
        viewModel.loadCrpyoto(id)
    }

    Surface(color = MaterialTheme.colorScheme.secondary, modifier = modifier.fillMaxSize()) {

        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            when {
                isLoading -> CircularProgressIndicator()
                errorMessage.isNotEmpty() -> Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp)
                )

                cryptoItem != null -> CryptoDetailContent(cryptoItem!!)
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
