package com.example.mybudget.data.util

import android.content.Context
import com.example.mybudget.data.model.Expense
import com.example.mybudget.data.model.Income
import java.io.File

object CsvExporter {

    fun exportToInternalStorage(
        context: Context,
        incomes: List<Income>,
        expenses: List<Expense>,
        fileName: String = "budget_export.csv",
    ): File? = try {
        val file = File(context.filesDir, fileName)
        val writer = file.bufferedWriter()

        // CSV Header
        val header = listOf(
            "Type", "Name", "Amount", "Frequency", "Custom Frequency (days)",
            "Start Date", "Repetitions", "End Date",
            "Priority", "Category", "Brand", "Provider", "Link", "Note",
        )
        writer.write(header.joinToString(","))
        writer.newLine()

        // Write incomes
        incomes.forEach { income ->
            val row = listOf(
                "Income",
                income.name,
                income.amount.toString(),
                income.frequency.label,
                income.customFrequencyInDays?.toString() ?: "",
                income.firstPaymentDate.toString(),
                "", // Repetitions not applicable
                "", // End date not applicable
                "", // Priority not applicable
                "", // Category not applicable
                "", // Brand not applicable
                "", // Provider not applicable
                "", // Link not applicable
                "", // Note not applicable
            )
            writer.write(row.joinToString(","))
            writer.newLine()
        }

        // Write expenses
        expenses.forEach { expense ->
            val row = listOf(
                "Expense",
                expense.name,
                expense.amount.toString(),
                expense.frequency.label,
                expense.customFrequencyInDays?.toString() ?: "",
                expense.purchaseDate.toString(),
                expense.repetitions?.toString() ?: "",
                expense.endDate?.toString() ?: "",
                expense.priority.label,
                expense.category.label,
                expense.brand,
                expense.provider,
                expense.linkToPurchase,
                expense.note ?: "",
            )
            writer.write(row.joinToString(","))
            writer.newLine()
        }

        writer.close()
        file
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
