package com.example.mybudget.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mybudget.data.model.Expense
import com.example.mybudget.data.model.ExpenseCategory
import com.example.mybudget.data.model.ExpenseFrequency
import com.example.mybudget.data.model.ExpensePriority
import com.example.mybudget.ui.components.DropdownMenuBox
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditExpenseDialog(
    expense: Expense,
    onDismiss: () -> Unit,
    onSave: (Expense) -> Unit
) {
    var name by remember { mutableStateOf(expense.name) }
    var amount by remember { mutableStateOf(expense.amount.toString()) }
    var selectedPriority by remember { mutableStateOf(expense.priority) }
    var selectedFrequency by remember { mutableStateOf(expense.frequency) }
    var selectedCategory by remember { mutableStateOf(expense.category) }
    var customFrequencyDays by remember {
        mutableStateOf(
            expense.customFrequencyInDays?.toString() ?: ""
        )
    }
    var purchaseDate by remember { mutableStateOf(expense.purchaseDate) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = purchaseDate.atStartOfDay(ZoneId.systemDefault()).toInstant()
            .toEpochMilli()
    )
    var showDatePicker by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val parsedAmount = amount.toDoubleOrNull() ?: return@TextButton
                    val customDays = customFrequencyDays.toIntOrNull()
                    if (name.isNotBlank() && parsedAmount != null) {
                        onSave(
                            expense.copy(
                                name = name,
                                amount = parsedAmount,
                                priority = selectedPriority,
                                frequency = selectedFrequency,
                                category = selectedCategory,
                                customFrequencyInDays = if (selectedFrequency == ExpenseFrequency.CUSTOM) customDays else null,
                                purchaseDate = purchaseDate
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
                    label = { Text("Name") }
                )

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                )

                DropdownMenuBox(
                    label = "Priority",
                    options = ExpensePriority.entries,
                    selected = selectedPriority,
                    onSelected = { selectedPriority = it }
                )

                DropdownMenuBox(
                    label = "Frequency",
                    options = ExpenseFrequency.entries,
                    selected = selectedFrequency,
                    onSelected = { selectedFrequency = it }
                )

                if (selectedFrequency == ExpenseFrequency.CUSTOM) {
                    OutlinedTextField(
                        value = customFrequencyDays,
                        onValueChange = { customFrequencyDays = it },
                        label = { Text("Custom Frequency (days)") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                    )
                }

                DropdownMenuBox(
                    label = "Category",
                    options = ExpenseCategory.entries,
                    selected = selectedCategory,
                    onSelected = { selectedCategory = it }
                )

                // Date Picker
                OutlinedButton(
                    onClick = { showDatePicker = true }
                ) {
                    Text("Purchase Date: ${purchaseDate.format(DateTimeFormatter.ISO_DATE)}")
                }

                if (showDatePicker) {
                    DatePickerDialog(
                        onDismissRequest = { showDatePicker = false },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    datePickerState.selectedDateMillis?.let {
                                        purchaseDate =
                                            Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault())
                                                .toLocalDate()
                                    }
                                    showDatePicker = false
                                }
                            ) { Text("OK") }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = { showDatePicker = false }
                            ) { Text("Cancel") }
                        }
                    ) {
                        DatePicker(state = datePickerState)
                    }
                }
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
            priority = ExpensePriority.REQUIRED,
            frequency = ExpenseFrequency.MONTHLY,
            category = ExpenseCategory.HOME
        ),
        onDismiss = {},
        onSave = {}
    )
}

