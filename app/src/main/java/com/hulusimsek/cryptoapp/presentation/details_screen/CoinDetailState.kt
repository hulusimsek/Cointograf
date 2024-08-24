package com.hulusimsek.cryptoapp.presentation.details_screen

import com.hulusimsek.cryptoapp.data.remote.dto.CryptoItem

data class CoinDetailState(
    val coin: CryptoItem? = null,             // Detayları gösterilecek kripto para
    val isLoading: Boolean = false,            // Yüklenme durumu
    val error: String? = null,                 // Hata mesajı
    val toastMessage: String? = null,          // Kullanıcıya gösterilecek toast mesajı
    val currentCrypto: String? = null          // Şu anda seçili kripto para sembolü
)