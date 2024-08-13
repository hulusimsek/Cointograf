package com.hulusimsek.cryptoapp.repository

import com.hulusimsek.cryptoapp.model.CryptoList
import com.hulusimsek.cryptoapp.service.CryptoAPI
import com.hulusimsek.cryptoapp.util.Resource
import javax.inject.Inject

class CryptoRepository @Inject constructor(
    private val api: CryptoAPI
) : CryptoRepositoryInterface {

    override suspend fun getCryptoList() : Resource<CryptoList> {
        val response = try {
            api.getCryptoList()
        } catch (e: Exception) {
            return Resource.Error("Error")
        }
        return Resource.Success(response)

    }

    override suspend fun getCrypto(id: String): Resource<CryptoList> {
        val response = try {
            api.getCrypto(id)
        } catch (e: Exception) {
            return Resource.Error("Error")
        }
        return Resource.Success(response)
    }
}