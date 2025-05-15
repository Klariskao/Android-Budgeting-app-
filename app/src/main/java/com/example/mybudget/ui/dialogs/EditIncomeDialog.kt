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
import com.example.mybudget.data.model.Income
import com.example.mybudget.data.model.IncomeFrequency
import com.example.mybudget.ui.components.DropdownMenuBox
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditIncomeDialog(income: Income, onDismiss: () -> Unit, onSave: (Income) -> Unit) {
    var name by remember { mutableStateOf(income.name) }
    var amount by remember { mutableStateOf(income.amount.toString()) }
    var type by remember { mutableStateOf(income.frequency) }
    var firstPaymentDate by remember { mutableStateOf(income.firstPaymentDate) }
    var customFrequencyInDays by remember {
        mutableStateOf(
            income.customFrequencyInDays?.toString() ?: "",
        )
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = firstPaymentDate.toEpochDay() * 86_400_000,
    )
    val datePickerShown = remember { mutableStateOf(false) }

    if (datePickerShown.value) {
        DatePickerDialog(
            onDismissRequest = { datePickerShown.value = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            firstPaymentDate = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                        }
                        datePickerShown.value = false
                    },
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { datePickerShown.value = false }) {
                    Text("Cancel")
                }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val parsedAmount = amount.toDoubleOrNull()
                    val customDays = customFrequencyInDays.toIntOrNull()
                    if (name.isNotBlank() && parsedAmount != null) {
                        onSave(
                            income.copy(
                                name = name.trim(),
                                amount = parsedAmount,
                                frequency = type,
                                firstPaymentDate = firstPaymentDate,
                                customFrequencyInDays = if (type ==
                                    IncomeFrequency.CUSTOM
                                ) {
                                    customDays
                                } else {
                                    null
                                },
                            ),
                        )
                    }
                    onDismiss()
                },
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
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    singleLine = true,
                )
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
                DropdownMenuBox(
                    label = "Frequency",
                    options = IncomeFrequency.entries,
                    selected = type,
                    onSelected = { type = it },
                )
                if (type == IncomeFrequency.CUSTOM) {
                    OutlinedTextField(
                        value = customFrequencyInDays,
                        onValueChange = { customFrequencyInDays = it },
                        label = { Text("Custom Frequency (days)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                    )
                }

                OutlinedButton(
                    onClick = { datePickerShown.value = true },
                ) {
                    Text(
                        "First Payment Date: ${
                            firstPaymentDate.format(
                                DateTimeFormatter.ofPattern(
                                    "MMM dd, yyyy",
                                ),
                            )
                        }",
                    )
                }
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewEditIncomeDialog() {
    EditIncomeDialog(
        income = Income(
            name = "Salary",
            amount = 4000.0,
            frequency = IncomeFrequency.MONTHLY,
        ),
        onDismiss = {},
        onSave = {},
    )
}
