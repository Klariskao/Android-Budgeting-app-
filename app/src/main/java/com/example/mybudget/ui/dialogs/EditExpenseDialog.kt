package com.example.mybudget.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mybudget.data.model.Expense
import com.example.mybudget.data.model.ExpenseFrequency
import com.example.mybudget.data.model.ExpenseType
import com.example.mybudget.ui.components.DropdownMenuBox

@Composable
fun EditExpenseDialog(
    expense: Expense,
    onDismiss: () -> Unit,
    onSave: (Expense) -> Unit
) {
    var name by remember { mutableStateOf(expense.name) }
    var amount by remember { mutableStateOf(expense.amount.toString()) }
    var type by remember { mutableStateOf(expense.type) }
    var frequency by remember { mutableStateOf(expense.frequency) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                val parsedAmount = amount.toDoubleOrNull()
                if (name.isNotBlank() && parsedAmount != null) {
                    onSave(
                        expense.copy(
                            name = name.trim(),
                            amount = parsedAmount,
                            type = type,
                            frequency = frequency
                        )
                    )
                }
                onDismiss()
            }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = { Text("Edit Expense") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                DropdownMenuBox(
                    label = "Type",
                    options = ExpenseType.entries,
                    selected = type,
                    onSelected = { type = it }
                )
                DropdownMenuBox(
                    label = "Frequency",
                    options = ExpenseFrequency.entries,
                    selected = frequency,
                    onSelected = { frequency = it }
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewEditExpenseDialog() {
    EditExpenseDialog(
        expense = Expense(
            name = "Rent",
            amount = 1200.0,
            type = ExpenseType.LUXURY,
            frequency = ExpenseFrequency.MONTHLY
        ),
        onDismiss = {},
        onSave = {}
    )
}

