package com.example.mybudget.ui.helpers

import com.example.mybudget.data.model.ExpenseFrequency
import java.time.LocalDate
import java.time.temporal.ChronoUnit

// Calculate the next purchase date based on frequency, repetitions, and endDate
fun calculateNextPurchaseDate(
    purchaseDate: LocalDate,
    frequency: ExpenseFrequency,
    customFrequencyInDays: Int?,
    repetitions: Int?,
    endDate: LocalDate?
): LocalDate? {
    // Calculate the next date based on frequency
    val candidateNextDate = when (frequency) {
        ExpenseFrequency.DAILY -> purchaseDate.plusDays(1)
        ExpenseFrequency.WEEKLY -> purchaseDate.plusWeeks(1)
        ExpenseFrequency.BIWEEKLY -> purchaseDate.plusWeeks(2)
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

            ExpenseFrequency.BIWEEKLY -> (
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
