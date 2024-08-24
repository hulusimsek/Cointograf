package com.hulusimsek.cryptoapp.presentation.home_screen

import com.hulusimsek.cryptoapp.data.database.entity.SearchQuery

sealed class CryptosEvent {
    data class SelectSymbol(val symbol: String) : CryptosEvent()
    data class SelectTab(val tabIndex: Int) : CryptosEvent()
    data class SearchCrypto(val query: String) : CryptosEvent()
    object Refresh : CryptosEvent()
    object LoadCryptos : CryptosEvent()
    object LoadSearchQuery : CryptosEvent()
    object FilterList : CryptosEvent()
    object ClearToastMessage : CryptosEvent()
    data class SaveSearchQuery(val query: String) : CryptosEvent()
    data class DeleteSearchQuery(val query: SearchQuery) : CryptosEvent()
}