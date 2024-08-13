package com.hulusimsek.cryptoapp.repository

import com.hulusimsek.cryptoapp.model.CryptoList
import com.hulusimsek.cryptoapp.util.Resource

interface CryptoRepositoryInterface {
    suspend fun getCryptoList() : Resource<CryptoList>

    suspend fun getCrypto(id: String): Resource<CryptoList>

}