package com.hulusimsek.cryptoapp.domain.use_case.get_klines_use_case

import coil.network.HttpException
import com.hulusimsek.cryptoapp.domain.model.KlineModel
import com.hulusimsek.cryptoapp.domain.repository.CryptoRepository
import com.hulusimsek.cryptoapp.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

class GetKlinesUseCase @Inject constructor(private val repository: CryptoRepository) {
    fun executeGetKlines(symbol: String, interval: String): Flow<Resource<List<KlineModel>>> {
        return flow {
            try {
                emit(Resource.Loading())

                // API'den gelen veriyi al
                val klineList = repository.getKlines(symbol, interval)

                // Eğer liste boş değilse dönüşüm işlemini yap
                if (klineList.isNotEmpty()) {
                    emit(Resource.Success(klineList.map {
                        KlineModel(
                            openTime = (it[0] as Number).toLong(),
                            openPrice = it[1] as String,
                            highPrice = it[2] as String,
                            lowPrice = it[3] as String,
                            closePrice = it[4] as String,
                            volume = it[5] as String,
                            closeTime = (it[6] as Number).toLong(),
                            quoteAssetVolume = it[7] as String,
                            numberOfTrades = (it[8] as Number).toInt(),
                            takerBuyBaseAssetVolume = it[9] as String,
                            takerBuyQuoteAssetVolume = it[10] as String,
                            ignore = it[11] as String
                        )
                    }))
                } else {
                    emit(Resource.Error(message = "No chart found!"))
                }
            } catch (e: IOException) {
                emit(Resource.Error(message = "No internet connection"))
            } catch (e: HttpException) {
                emit(Resource.Error(message = e.localizedMessage ?: "Error"))
            }
        }
    }
}