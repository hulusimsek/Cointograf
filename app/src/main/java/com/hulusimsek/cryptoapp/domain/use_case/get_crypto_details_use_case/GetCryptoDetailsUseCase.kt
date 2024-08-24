package com.hulusimsek.cryptoapp.domain.use_case.get_crypto_details_use_case

import coil.network.HttpException
import com.hulusimsek.cryptoapp.data.remote.dto.CryptoItem
import com.hulusimsek.cryptoapp.domain.repository.CryptoRepository
import com.hulusimsek.cryptoapp.util.Constants.getBtcSymbols
import com.hulusimsek.cryptoapp.util.Constants.removeTrailingZeros
import com.hulusimsek.cryptoapp.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

class GetCryptoDetailsUseCase @Inject constructor(
    private val repository: CryptoRepository
) {
    fun executeGetCryptos(id: String): Flow<Resource<CryptoItem>> = flow {
        try {
            emit(Resource.Loading()) // Yükleme durumu yayılır.

            // API'den veri al
            val crypto = repository.getCrypto(id)

            // Eğer veri null değilse ve işlem başarılıysa
            if (crypto != null) {
                val priceChangePercent = crypto.priceChangePercent.toFloatOrNull() ?: 0f
                val formattedPriceChangePercent = String.format("%.2f", priceChangePercent)

                // Formatlanmış değer ile kripto veri modeli güncellenebilir.
                val updatedCrypto = crypto.copy(priceChangePercent = formattedPriceChangePercent)

                emit(Resource.Success(updatedCrypto)) // Başarılı durumda güncellenmiş veri yayılır.
            } else {
                emit(Resource.Error(message = "No coin found!")) // Veri bulunamazsa hata yayılır.
            }
        } catch (e: IOException) {
            emit(Resource.Error(message = "No internet connection")) // İnternet bağlantı hatası.
        } catch (e: HttpException) {
            emit(Resource.Error(message = e.localizedMessage ?: "Error")) // HTTP hatası.
        }
    }
}