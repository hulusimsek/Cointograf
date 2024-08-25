package com.hulusimsek.cryptoapp.domain.repository

import androidx.compose.foundation.lazy.layout.IntervalList
import com.hulusimsek.cryptoapp.data.database.entity.SearchQuery
import com.hulusimsek.cryptoapp.data.remote.dto.Crypto
import com.hulusimsek.cryptoapp.data.remote.dto.CryptoItem
import com.hulusimsek.cryptoapp.data.remote.dto.CryptoList
import com.hulusimsek.cryptoapp.data.remote.dto.Klines
import com.hulusimsek.cryptoapp.util.Resource

interface CryptoRepository {
    suspend fun insertSearchQuery(searchQuery: SearchQuery)

    suspend fun deleteSearchQuery(searchQuery: SearchQuery)

    suspend fun deleteSearchQueryByQuery(query: String)

    suspend fun getSearchQuery(): List<SearchQuery>

    suspend fun getCryptoList() : Resource<CryptoList>

    suspend fun getCrypto(id: String): CryptoItem

    suspend fun getCryptoList24hr(): Crypto

    suspend fun getKlines(id: String, interval: String): Klines


}