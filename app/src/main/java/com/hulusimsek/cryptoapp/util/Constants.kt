package com.hulusimsek.cryptoapp.util

import android.content.Context
import com.hulusimsek.cryptoapp.R
import java.math.BigDecimal
import java.text.DecimalFormat

object Constants {
    const val BASE_URL = "https://data-api.binance.vision"

    fun removeTrailingZeros(value: String): String {
        val bigDecimal = BigDecimal(value).stripTrailingZeros()

        val formatter = DecimalFormat("#,###.################")
        val formattedPriceChangePercent = formatter.format(bigDecimal)

        return formattedPriceChangePercent.toString()
    }


    fun getBtcSymbols(context: Context): List<String> {
        return listOf(
            context.getString(R.string.allMarkets),
            "BTC",
            "USDT",
            "TUSD",
            "PAX",
            "USDC",
            "USDS",
            "BUSD",
            "NGN",
            "RUB",
            "TRY",
            "EUR",
            "ZAR",
            "BKRW",
            "IDRT",
            "USDT",
            "GBP",
            "UAH",
            "BIDR",
            "AUD",
            "DAI",
            "BRL",
            "VAI",
            "USDP",
            "UST",
            "PLN",
            "RON",
            "ARS",
            "FDUSD",
            "AEUR",
            "JPY",
            "MXN"
        )
    }

}