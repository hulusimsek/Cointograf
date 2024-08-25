package com.hulusimsek.cryptoapp.domain.use_case.get_cryptos

import android.content.Context
import coil.network.HttpException
import com.hulusimsek.cryptoapp.data.remote.dto.Crypto
import com.hulusimsek.cryptoapp.data.remote.dto.CryptoItem
import com.hulusimsek.cryptoapp.domain.repository.CryptoRepository
import com.hulusimsek.cryptoapp.util.Constants.getBtcSymbols
import com.hulusimsek.cryptoapp.util.Constants.removeTrailingZeros
import com.hulusimsek.cryptoapp.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOError
import java.io.IOException
import javax.inject.Inject

class GetCryptosUseCase @Inject constructor(
    private val repository: CryptoRepository,
    private val context: Context
) {
    fun executeGetCryptos(): Flow<Resource<List<CryptoItem>>> {
        return flow {
            try {
                emit(Resource.Loading())

                // API'den gelen veriyi al
                val cryptoList = repository.getCryptoList24hr()

                // Eğer liste boş değilse dönüşüm işlemini yap
                if (cryptoList.isNotEmpty()) {
                    val cryptoItems = cryptoList.map { item ->
                        // Varsayılan olarak surname ve name ayarla
                        var name = item.symbol
                        var surname = ""

                        // Surname olarak ayırmak istediğimiz listeyi kullanarak ayrıştırma yapıyoruz
                        getBtcSymbols(context = context).findLast { item.symbol.endsWith(it) }
                            ?.let { potentialSurname ->
                                surname = potentialSurname
                                name = item.symbol.removeSuffix(potentialSurname)
                            }

                        val priceChangePercent = item.priceChangePercent.toFloatOrNull() ?: 0f
                        val formattedPriceChangePercent = String.format("%.2f", priceChangePercent)



                        CryptoItem(
                            surname = surname,
                            name = name,
                            symbol = item.symbol,
                            lastPrice = removeTrailingZeros(item.lastPrice),
                            askPrice = item.askPrice,
                            askQty = item.askQty,
                            bidPrice = item.bidPrice,
                            bidQty = item.bidQty,
                            closeTime = item.closeTime,
                            count = item.count,
                            firstId = item.firstId,
                            highPrice = item.highPrice,
                            lastId = item.lastId,
                            lastQty = item.lastQty,
                            lowPrice = item.lowPrice,
                            openPrice = item.openPrice,
                            openTime = item.openTime,
                            prevClosePrice = item.prevClosePrice,
                            priceChange = item.priceChange,
                            priceChangePercent = formattedPriceChangePercent,
                            quoteVolume = item.quoteVolume,
                            volume = item.volume,
                            weightedAvgPrice = item.weightedAvgPrice
                        )
                    }

                    emit(Resource.Success(cryptoItems))
                } else {
                    emit(Resource.Error(message = "No coin found!"))
                }
            } catch (e: IOException) {
                emit(Resource.Error(message = "No internet connection"))
            } catch (e: HttpException) {
                emit(Resource.Error(message = e.localizedMessage ?: "Error"))
            }
        }
    }
}