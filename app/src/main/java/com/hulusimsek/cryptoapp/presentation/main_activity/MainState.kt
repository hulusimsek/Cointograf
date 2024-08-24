package com.hulusimsek.cryptoapp.presentation.main_activity

data class MainState(
    val pageCount: Int = 4, // Varsayılan sayfa sayısı
    val currentPage: Int = 0 // Varsayılan mevcut sayfa
)