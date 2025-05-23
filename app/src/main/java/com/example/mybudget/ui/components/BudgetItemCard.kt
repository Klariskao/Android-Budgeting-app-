package com.example.mybudget.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mybudget.data.local.SettingsDataStoreImpl
import com.example.mybudget.ui.helpers.formatCurrency
import org.koin.compose.getKoin

@Composable
fun BudgetItemCard(title: String, amount: Double, subtitle: String) {
    val settingsDataStore: SettingsDataStoreImpl = getKoin().get()
    val currency by settingsDataStore.currencyFlow.collectAsState(initial = "USD")
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            Text(subtitle, style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(4.dp))
            Text(formatCurrency(amount, currency), style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BudgetItemCardPreview() {
    MaterialTheme {
        BudgetItemCard(
            title = "Budget card",
            amount = 1234.5,
            subtitle = "Weekly",
        )
    }
}
