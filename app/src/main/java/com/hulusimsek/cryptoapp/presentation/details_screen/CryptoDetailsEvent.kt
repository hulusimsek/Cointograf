package com.hulusimsek.cryptoapp.presentation.details_screen

sealed class CryptoDetailsEvent {
    data class LoadCryptoDetails(val symbol: String) : CryptoDetailsEvent()
    data class SelectTimeRange(val timeRange: String) : CryptoDetailsEvent()
    object Refresh : CryptoDetailsEvent()
    object ClearToastMessage : CryptoDetailsEvent()
}