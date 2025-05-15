package com.example.mybudget.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mybudget.data.model.Expense
import com.example.mybudget.data.model.ExpenseCategory
import com.example.mybudget.data.model.ExpenseFrequency
import com.example.mybudget.data.model.ExpensePriority
import com.example.mybudget.ui.helpers.formatCurrency
import com.example.mybudget.ui.theme.MyBudgetTheme
import java.time.LocalDate

@Composable
fun ExpenseDetailScreen(expense: Expense) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Text(
            text = expense.name,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                InfoRow(label = "Amount", value = formatCurrency(expense.amount))
                InfoRow(label = "Category", value = expense.category.label)
                InfoRow(label = "Priority", value = expense.priority.label)
                InfoRow(label = "Frequency", value = expense.frequency.label)
                if (expense.frequency == ExpenseFrequency.CUSTOM &&
                    expense.customFrequencyInDays != null
                ) {
                    InfoRow(
                        label = "Custom Frequency",
                        value = "${expense.customFrequencyInDays} days",
                    )
                }
                InfoRow(label = "Purchase Date", value = expense.purchaseDate.toString())
                InfoRow(label = "Next Purchase", value = expense.nextPurchaseDate.toString())
                if (expense.brand.isNotBlank()) {
                    InfoRow(label = "Brand", value = expense.brand)
                }
                if (expense.provider.isNotBlank()) {
                    InfoRow(label = "Provider", value = expense.provider)
                }
                if (expense.linkToPurchase.isNotBlank()) {
                    InfoRow(label = "Purchase Link", value = expense.linkToPurchase)
                }
                if (!expense.note.isNullOrBlank()) {
                    InfoRow(label = "Note", value = expense.note)
                }
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Divider(
            modifier = Modifier.padding(top = 4.dp),
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ExpenseDetailScreenPreview() {
    MaterialTheme {
        ExpenseDetailScreen(
            Expense(
                name = "Netflix Subscription",
                amount = 15.99,
                priority = ExpensePriority.GOOD_TO_HAVE,
                frequency = ExpenseFrequency.MONTHLY,
                category = ExpenseCategory.ENTERTAINMENT,
                customFrequencyInDays = null,
                purchaseDate = LocalDate.of(2025, 5, 1),
                brand = "Netflix",
                provider = "Netflix Inc.",
                linkToPurchase = "https://www.netflix.com",
                nextPurchaseDate = LocalDate.of(2025, 6, 1),
                note = "Monthly subscription for streaming service",
            ),
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ExpenseDetailScreenPreviewDark() {
    MyBudgetTheme {
        ExpenseDetailScreen(
            Expense(
                name = "Netflix Subscription",
                amount = 15.99,
                priority = ExpensePriority.GOOD_TO_HAVE,
                frequency = ExpenseFrequency.MONTHLY,
                category = ExpenseCategory.ENTERTAINMENT,
                customFrequencyInDays = null,
                purchaseDate = LocalDate.of(2025, 5, 1),
                brand = "Netflix",
                provider = "Netflix Inc.",
                linkToPurchase = "https://www.netflix.com",
                nextPurchaseDate = LocalDate.of(2025, 6, 1),
                note = "Monthly subscription for streaming service",
            ),
        )
    }
}
