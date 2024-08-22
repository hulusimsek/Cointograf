package com.hulusimsek.cryptoapp.view

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
import com.hulusimsek.cryptoapp.model.CryptoItem
import com.hulusimsek.cryptoapp.ui.theme.BlueMunsell
import com.hulusimsek.cryptoapp.viewmodel.HomeViewModel

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
    LazyColumn(contentPadding = PaddingValues(5.dp)) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Seçim yapma ve simge göstergesi
                Row(
                    modifier = Modifier
                        .clickable { showDialog.value = true }
                        .weight(1f)
                        .padding(horizontal = 8.dp), // Yalnızca yatay padding ekleyin
                    verticalAlignment = Alignment.CenterVertically, // Dikey hizalama merkez
                    horizontalArrangement = Arrangement.Start // Yatay hizalama merkez
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
                            .padding(start = 24.dp) // Dikey hizalamayı sağla
                    )
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = stringResource(R.string.allMarkets),
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(16.dp)
                            .align(Alignment.CenterVertically) // Dikey hizalamayı sağla
                    )
                }

                // Fiyat ve 24s Değişim başlıkları
                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(2f)
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








        if (searchQuery.isEmpty()) {
            items(filterCryptoList) { crypto ->
                CryptoRow(navController = navController, crypto = crypto)
            }
        } else {
            items(cryptoList) { crypto ->
                CryptoRow(navController = navController, crypto = crypto)
            }
        }

    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (isLoading) {
            CircularProgressIndicator(color = BlueMunsell)
        }
        if (errorMessage.isNotEmpty()) {
            RetryView(error = errorMessage) {
                viewModel.refresh()
            }
        }
    }
}