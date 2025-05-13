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
import com.example.mybudget.data.model.Income
import com.example.mybudget.data.model.IncomeType
import com.example.mybudget.ui.components.DropdownMenuBox

@Composable
fun EditIncomeDialog(
    income: Income,
    onDismiss: () -> Unit,
    onSave: (Income) -> Unit
) {
    var name by remember { mutableStateOf(income.name) }
    var amount by remember { mutableStateOf(income.amount.toString()) }
    var type by remember { mutableStateOf(income.type) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                val parsedAmount = amount.toDoubleOrNull()
                if (name.isNotBlank() && parsedAmount != null) {
                    onSave(
                        income.copy(
                            name = name.trim(),
                            amount = parsedAmount,
                            type = type
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
        title = { Text("Edit Income") },
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
                    options = IncomeType.entries,
                    selected = type,
                    onSelected = { type = it }
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewEditIncomeDialog() {
    EditIncomeDialog(
        income = Income(
            name = "Salary",
            amount = 4000.0,
            type = IncomeType.MONTHLY
        ),
        onDismiss = {},
        onSave = {}
    )
}

