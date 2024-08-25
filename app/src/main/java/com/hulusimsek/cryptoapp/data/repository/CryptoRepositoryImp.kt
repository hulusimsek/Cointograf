package com.hulusimsek.cryptoapp.data.repository

import com.hulusimsek.cryptoapp.data.database.entity.SearchQuery
import com.hulusimsek.cryptoapp.data.remote.dto.Crypto
import com.hulusimsek.cryptoapp.data.remote.dto.CryptoItem
import com.hulusimsek.cryptoapp.data.remote.dto.CryptoList
import com.hulusimsek.cryptoapp.data.database.dao.CryptoDao
import com.hulusimsek.cryptoapp.data.remote.CryptoAPI
import com.hulusimsek.cryptoapp.data.remote.dto.Klines
import com.hulusimsek.cryptoapp.domain.repository.CryptoRepository
import com.hulusimsek.cryptoapp.util.Resource
import javax.inject.Inject

class CryptoRepositoryImp @Inject constructor(
    private val api: CryptoAPI,
    private val cryptoDao: CryptoDao
) : CryptoRepository {
    override suspend fun insertSearchQuery(searchQuery: SearchQuery) {
        cryptoDao.insertQuery(searchQuery)
    }

    override suspend fun deleteSearchQuery(searchQuery: SearchQuery) {
        cryptoDao.deleteQuery(searchQuery)
    }

    override suspend fun deleteSearchQueryByQuery(query: String) {
        cryptoDao.deleteSearchQueryByQuery(query)

    }

    override suspend fun getSearchQuery(): List<SearchQuery> {
        return cryptoDao.getAllQueries()
    }

    override suspend fun getCryptoList(): Resource<CryptoList> {
        val response = try {
            api.getCryptoList()
        } catch (e: Exception) {
            return Resource.Error("Error")
        }
        return Resource.Success(response)

    }

    override suspend fun getCrypto(id: String): CryptoItem {
        return api.getCrypto(id)
    }

    override suspend fun getCryptoList24hr(): Crypto {
        return api.getCryptoList24hr()
    }

    override suspend fun getKlines(id: String, interval: String): Klines {
        return api.getKlines(id, interval)
    }


}