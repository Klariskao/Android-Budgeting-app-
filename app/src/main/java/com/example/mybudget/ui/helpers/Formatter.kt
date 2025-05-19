package com.example.mybudget.ui.helpers

import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

fun formatCurrency(
    amount: Double,
    currencyCode: String,
    locale: Locale = Locale.getDefault(),
): String {
    val formatter = NumberFormat.getCurrencyInstance(locale)
    formatter.currency = Currency.getInstance(currencyCode)
    return formatter.format(amount)
}
