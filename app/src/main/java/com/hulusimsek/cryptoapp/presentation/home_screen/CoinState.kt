package com.hulusimsek.cryptoapp.presentation.home_screen

import com.hulusimsek.cryptoapp.data.remote.dto.CryptoItem

data class CoinState(
    val isLoading: Boolean = false, // Veriler yüklenirken gösterilecek olan loading durumu
    val coins: List<CryptoItem> = emptyList(), // Yüklenecek olan kripto para listesini tutar
    val error: String = "" // Hata mesajını tutar, hata oluştuğunda dolu olur
)