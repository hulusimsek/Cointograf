package com.hulusimsek.cryptoapp.data.remote

import com.hulusimsek.cryptoapp.data.remote.dto.Crypto
import com.hulusimsek.cryptoapp.data.remote.dto.CryptoItem
import com.hulusimsek.cryptoapp.data.remote.dto.CryptoList
import com.hulusimsek.cryptoapp.data.remote.dto.Klines
import retrofit2.http.GET
import retrofit2.http.Query

interface CryptoAPI {
    @GET("/api/v3/ticker/price")
    suspend fun getCryptoList(): CryptoList

    @GET("api/v3/ticker/24hr")
    suspend fun getCryptoList24hr(): Crypto

    @GET("api/v3/ticker/24hr")
    suspend fun getCrypto(@Query("symbol") symbol : String) : CryptoItem

    @GET("/api/v3/klines")
    suspend fun getKlines(@Query("symbol") symbol : String, @Query("interval") interval : String) : Klines
}