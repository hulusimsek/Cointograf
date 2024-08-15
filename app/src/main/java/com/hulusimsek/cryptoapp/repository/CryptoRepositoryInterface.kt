package com.hulusimsek.cryptoapp.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.hulusimsek.cryptoapp.entity.SearchQuery
import com.hulusimsek.cryptoapp.model.CryptoItem
import com.hulusimsek.cryptoapp.model.CryptoList
import com.hulusimsek.cryptoapp.util.Resource
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

interface CryptoRepositoryInterface {
    suspend fun insertSearchQuery(searchQuery: SearchQuery)

    suspend fun deleteSearchQuery(searchQuery: SearchQuery)

    suspend fun deleteSearchQueryByQuery(query: String)

    suspend fun getSearchQuery(): List<SearchQuery>

    suspend fun getCryptoList() : Resource<CryptoList>

    suspend fun getCrypto(id: String): Resource<CryptoItem>

}