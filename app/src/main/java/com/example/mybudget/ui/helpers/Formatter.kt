package com.example.mybudget.ui.helpers

import java.text.NumberFormat
import java.util.Locale

fun formatCurrency(amount: Double, locale: Locale = Locale.getDefault()): String {
    val currencyFormatter = NumberFormat.getCurrencyInstance(locale)
    return currencyFormatter.format(amount)
}
