package com.example.mybudget.ui.helpers

import com.example.mybudget.data.model.Expense
import com.example.mybudget.data.model.ExpenseFrequency
import com.example.mybudget.data.model.Income
import com.example.mybudget.data.model.IncomeFrequency
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.ChronoUnit

// Calculate the next purchase date based on frequency, repetitions, and endDate
fun calculateNextPurchaseDate(
    purchaseDate: LocalDate,
    frequency: ExpenseFrequency,
    customFrequencyInDays: Int?,
    repetitions: Int?,
    endDate: LocalDate?,
): LocalDate? {
    // Calculate the next date based on frequency
    val candidateNextDate = when (frequency) {
        ExpenseFrequency.DAILY -> purchaseDate.plusDays(1)
        ExpenseFrequency.WEEKLY -> purchaseDate.plusWeeks(1)
        ExpenseFrequency.BI_WEEKLY -> purchaseDate.plusWeeks(2)
        ExpenseFrequency.MONTHLY -> purchaseDate.plusMonths(1)
        ExpenseFrequency.YEARLY -> purchaseDate.plusYears(1)
        ExpenseFrequency.ONE_TIME -> return null
        ExpenseFrequency.CUSTOM -> customFrequencyInDays?.let {
            purchaseDate.plusDays(it.toLong())
        }
    } ?: return null

    // Check endDate limit
    if (endDate != null && candidateNextDate.isAfter(endDate)) {
        return null
    }

    // Check repetitions limit:
    // Assuming purchaseDate is the first purchase,
    // if repetitions is set, max allowed purchases = repetitions
    // So, for repetitions=1, next purchase is null (already bought once)
    if (repetitions != null) {
        // Count how many intervals have passed from purchaseDate to candidateNextDate
        val intervalsPassed = when (frequency) {
            ExpenseFrequency.DAILY -> ChronoUnit.DAYS.between(
                purchaseDate,
                candidateNextDate,
            ).toInt()

            ExpenseFrequency.WEEKLY -> ChronoUnit.WEEKS.between(
                purchaseDate,
                candidateNextDate,
            ).toInt()

            ExpenseFrequency.BI_WEEKLY -> (
                ChronoUnit.WEEKS.between(
                    purchaseDate,
                    candidateNextDate,
                ) / 2
                ).toInt()

            ExpenseFrequency.MONTHLY -> ChronoUnit.MONTHS.between(
                purchaseDate,
                candidateNextDate,
            ).toInt()

            ExpenseFrequency.YEARLY -> ChronoUnit.YEARS.between(
                purchaseDate,
                candidateNextDate,
            ).toInt()

            ExpenseFrequency.ONE_TIME -> 0
            ExpenseFrequency.CUSTOM -> customFrequencyInDays?.let {
                (ChronoUnit.DAYS.between(purchaseDate, candidateNextDate) / it).toInt()
            } ?: 0
        }

        if (intervalsPassed >= repetitions) {
            return null
        }
    }

    return candidateNextDate
}

// Helper extension to get monthly equivalent from IncomeFrequency
fun Income.toMonthlyAmount(): Double = when (frequency) {
    IncomeFrequency.WEEKLY -> amount * 52 / 12
    IncomeFrequency.BI_WEEKLY -> amount * 26 / 12
    IncomeFrequency.MONTHLY -> amount
    IncomeFrequency.YEARLY -> amount / 12
    IncomeFrequency.ONE_TIME -> 0.0 // One-time: do not count monthly recurring
    IncomeFrequency.CUSTOM -> amount / 12 // You can improve this if you track custom days
}

// Helper extension to get yearly equivalent from IncomeFrequency
fun Income.toYearlyAmount(customFrequencyInDays: Int?): Double = when (frequency) {
    IncomeFrequency.WEEKLY -> amount * 52
    IncomeFrequency.BI_WEEKLY -> amount * 26
    IncomeFrequency.MONTHLY -> amount * 12
    IncomeFrequency.YEARLY -> amount
    IncomeFrequency.ONE_TIME -> amount // One-time counts as yearly for total
    IncomeFrequency.CUSTOM -> amount * (365 / (customFrequencyInDays ?: 365)) // Approximate
}

// Same helpers for Expense
fun Expense.toMonthlyAmount(): Double = when (frequency) {
    ExpenseFrequency.DAILY -> amount * 30.44
    ExpenseFrequency.WEEKLY -> amount * 52 / 12
    ExpenseFrequency.BI_WEEKLY -> amount * 26 / 12
    ExpenseFrequency.MONTHLY -> amount
    ExpenseFrequency.YEARLY -> amount / 12
    ExpenseFrequency.ONE_TIME -> 0.0
    ExpenseFrequency.CUSTOM -> amount / 12
}

fun Expense.toYearlyAmount(customFrequencyInDays: Int?): Double = when (frequency) {
    ExpenseFrequency.DAILY -> amount * 365
    ExpenseFrequency.WEEKLY -> amount * 52
    ExpenseFrequency.BI_WEEKLY -> amount * 26
    ExpenseFrequency.MONTHLY -> amount * 12
    ExpenseFrequency.YEARLY -> amount
    ExpenseFrequency.ONE_TIME -> amount
    ExpenseFrequency.CUSTOM -> amount * (365 / (customFrequencyInDays ?: 365))
}

fun getExpenseOccurrencesInPeriod(expense: Expense, month: YearMonth?): List<LocalDate> {
    val dates = mutableListOf<LocalDate>()
    val start = expense.purchaseDate
    val end = expense.endDate ?: start.plusYears(1)

    val daysBetween = when (expense.frequency) {
        ExpenseFrequency.DAILY -> 1
        ExpenseFrequency.WEEKLY -> 7
        ExpenseFrequency.BI_WEEKLY -> 14
        ExpenseFrequency.MONTHLY -> 30
        ExpenseFrequency.YEARLY -> 365
        ExpenseFrequency.ONE_TIME -> null
        ExpenseFrequency.CUSTOM -> expense.customFrequencyInDays
    } ?: return listOf(start)

    var current = start
    var count = 0
    while (current <= end && (expense.repetitions == null || count < expense.repetitions)) {
        if (
            (month != null && YearMonth.from(current) == month) ||
            (
                month == null &&
                    current in YearMonth.now().atDay(1)..YearMonth.now().plusMonths(11)
                        .atEndOfMonth()
                )
        ) {
            dates.add(current)
        }
        current = current.plusDays(daysBetween.toLong())
        count++
    }
    return dates
}

fun getIncomeOccurrencesInPeriod(income: Income, month: YearMonth?): List<LocalDate> {
    val dates = mutableListOf<LocalDate>()
    val start = income.firstPaymentDate
    val end = start.plusYears(1)

    val daysBetween = when (income.frequency) {
        IncomeFrequency.WEEKLY -> 7
        IncomeFrequency.BI_WEEKLY -> 14
        IncomeFrequency.MONTHLY -> 30
        IncomeFrequency.YEARLY -> 365
        IncomeFrequency.ONE_TIME -> null
        IncomeFrequency.CUSTOM -> income.customFrequencyInDays
    } ?: return listOf(start)

    var current = start
    while (current <= end) {
        if (
            (month != null && YearMonth.from(current) == month) ||
            (
                month == null &&
                    current in YearMonth.now().atDay(1)..YearMonth.now().plusMonths(11)
                        .atEndOfMonth()
                )
        ) {
            dates.add(current)
        }
        current = current.plusDays(daysBetween.toLong())
    }
    return dates
}
