package com.hulusimsek.cryptoapp.repository

import androidx.lifecycle.LiveData
import com.hulusimsek.cryptoapp.entity.SearchQuery
import com.hulusimsek.cryptoapp.model.Crypto
import com.hulusimsek.cryptoapp.model.CryptoItem
import com.hulusimsek.cryptoapp.model.CryptoList
import com.hulusimsek.cryptoapp.roomDB.CryptoDao
import com.hulusimsek.cryptoapp.service.CryptoAPI
import com.hulusimsek.cryptoapp.util.Resource
import javax.inject.Inject

class CryptoRepository @Inject constructor(
    private val api: CryptoAPI,
    private val cryptoDao: CryptoDao
) : CryptoRepositoryInterface {
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

    override suspend fun getCryptoList() : Resource<CryptoList> {
        val response = try {
            api.getCryptoList()
        } catch (e: Exception) {
            return Resource.Error("Error")
        }
        return Resource.Success(response)

    }

    override suspend fun getCrypto(id: String): Resource<CryptoItem> {
        val response = try {
            api.getCrypto(id)
        } catch (e: Exception) {
            return Resource.Error("Error")
        }
        return Resource.Success(response)
    }

    override suspend fun getCryptoList24hr(): Resource<Crypto> {
        val response = try {
            api.getCryptoList24hr()
        } catch (e: Exception) {
            return Resource.Error("Error")
        }
        return Resource.Success(response)    }


}