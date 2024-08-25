package com.hulusimsek.cryptoapp.presentation.home_screen.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.hulusimsek.cryptoapp.R
import com.hulusimsek.cryptoapp.data.remote.dto.CryptoItem
import com.hulusimsek.cryptoapp.presentation.home_screen.CryptosEvent
import com.hulusimsek.cryptoapp.presentation.theme.BlueMunsell
import com.hulusimsek.cryptoapp.presentation.home_screen.HomeViewModel
import com.hulusimsek.cryptoapp.presentation.util.RetryView

@Composable
fun HomeLazyColumn(
    navController: NavController,
    cryptoList: List<CryptoItem>,
    filterCryptoList: List<CryptoItem>,
    searchQuery: String,
    isLoading: Boolean,
    errorMessage: String,
    selectedSymbol: String?,
    showDialog: MutableState<Boolean>,
    viewModel: HomeViewModel
) {
    // Box, LazyColumn ve diğer içeriklerin yerleştirileceği ana bileşen
    Box(modifier = Modifier.fillMaxSize()) {
        // LazyColumn'ı ekranın üst kısmına yerleştir
        LazyColumn(contentPadding = PaddingValues(5.dp)) {
            if (!isLoading) {
                item {
                    // Başlık ve simge gösterimi
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Row(
                            modifier = Modifier
                                .clickable { showDialog.value = true }
                                .padding(horizontal = 8.dp), // Yalnızca yatay padding ekleyin
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Text(
                                text = selectedSymbol ?: "Seçiniz",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                ),
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .padding(start = 24.dp)
                            )
                            Icon(
                                imageVector = Icons.Filled.ArrowDropDown,
                                contentDescription = stringResource(R.string.allMarkets),
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .size(16.dp)
                                    .align(Alignment.CenterVertically)
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = stringResource(R.string.price),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                ),
                                modifier = Modifier.padding(end = 16.dp),
                                textAlign = TextAlign.End
                            )
                            Text(
                                text = stringResource(R.string.price_change_24h),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                ),
                                modifier = Modifier.padding(end = 48.dp),
                                textAlign = TextAlign.End
                            )
                        }
                    }
                }

                // Liste elemanlarını render et
                items(if (searchQuery.isEmpty()) filterCryptoList else cryptoList, key = {
                    it.symbol
                }) { crypto ->
                    CryptoRow(navController = navController, crypto = crypto)
                }
            }
        }

        // Loading ve hata durumlarını ortada göster
        if (isLoading || errorMessage.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp), // Biraz boşluk bırak
                contentAlignment = Alignment.Center
            ) {
                when {
                    isLoading -> CircularProgressIndicator(color = BlueMunsell)
                    errorMessage.isNotEmpty() -> RetryView(error = errorMessage) {
                        viewModel.onEvent(CryptosEvent.Refresh)

                    }
                }
            }
        }
    }
}
