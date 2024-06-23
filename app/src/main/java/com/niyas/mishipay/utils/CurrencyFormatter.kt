package com.niyas.mishipay.utils

import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale

object CurrencyFormatter {
    fun formatToINR(amount: Int): String {
        val inrFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN")) as DecimalFormat
        inrFormat.maximumFractionDigits = 0
        return inrFormat.format(amount)
    }
}