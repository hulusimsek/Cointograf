package com.hulusimsek.cryptoapp.presentation.formetters

import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DateValueFormatter : ValueFormatter() {
    private val dateFormat = SimpleDateFormat("dd MMM yy", Locale.getDefault())

    override fun getFormattedValue(value: Float): String {
        val date = Date(value.toLong())
        return dateFormat.format(date)
    }
}