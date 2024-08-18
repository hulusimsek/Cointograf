package com.hulusimsek.cryptoapp.service

import com.hulusimsek.cryptoapp.model.Crypto
import com.hulusimsek.cryptoapp.model.CryptoItem
import com.hulusimsek.cryptoapp.model.CryptoList
import com.hulusimsek.cryptoapp.model.CryptoListItem
import retrofit2.http.GET
import retrofit2.http.Query

interface CryptoAPI {
    @GET("/api/v3/ticker/price")
    suspend fun getCryptoList(): CryptoList

    @GET("api/v3/ticker/24hr")
    suspend fun getCryptoList24hr(): Crypto

    @GET("api/v3/ticker/24hr")
    suspend fun getCrypto(@Query("symbol") symbol : String) : CryptoItem
}