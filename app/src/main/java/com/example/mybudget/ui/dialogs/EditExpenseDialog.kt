package com.example.mybudget.ui.dialogs

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mybudget.data.model.Expense
import com.example.mybudget.data.model.ExpenseCategory
import com.example.mybudget.data.model.ExpenseFrequency
import com.example.mybudget.data.model.ExpensePriority
import com.example.mybudget.ui.components.DropdownMenuBox
import com.example.mybudget.ui.theme.MyBudgetTheme
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditExpenseDialog(
    expense: Expense,
    onDismiss: () -> Unit,
    onSave: (Expense) -> Unit,
    modifier: Modifier = Modifier,
) {
    var name by remember { mutableStateOf(expense.name) }
    var amount by remember { mutableStateOf(expense.amount.toString()) }
    var selectedPriority by remember { mutableStateOf(expense.priority) }
    var selectedFrequency by remember { mutableStateOf(expense.frequency) }
    var selectedCategory by remember { mutableStateOf(expense.category) }
    var customFrequencyDays by remember {
        mutableStateOf(
            expense.customFrequencyInDays?.toString() ?: "",
        )
    }
    var purchaseDate by remember { mutableStateOf(expense.purchaseDate) }
    var brand by remember { mutableStateOf(expense.brand) }
    var provider by remember { mutableStateOf(expense.provider) }
    var linkToPurchase by remember { mutableStateOf(expense.linkToPurchase) }
    var note by remember { mutableStateOf(expense.note ?: "") }

    var showPurchaseDatePicker by remember { mutableStateOf(false) }

    val purchaseDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = purchaseDate.atStartOfDay(ZoneId.systemDefault()).toInstant()
            .toEpochMilli(),
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val parsedAmount = amount.toDoubleOrNull()
                    val customDays = customFrequencyDays.toIntOrNull()
                    if (name.isNotBlank() && parsedAmount != null) {
                        onSave(
                            expense.copy(
                                name = name.trim(),
                                amount = parsedAmount,
                                priority = selectedPriority,
                                frequency = selectedFrequency,
                                category = selectedCategory,
                                customFrequencyInDays = if (selectedFrequency ==
                                    ExpenseFrequency.CUSTOM
                                ) {
                                    customDays
                                } else {
                                    null
                                },
                                purchaseDate = purchaseDate,
                                brand = brand.trim(),
                                provider = provider.trim(),
                                linkToPurchase = linkToPurchase.trim(),
                                note = note.trim().ifEmpty { null },
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
        title = {
            Text(
                "Edit Expense",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
        },
        text = {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                )

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount") },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number,
                    ),
                )

                DropdownMenuBox(
                    label = "Priority",
                    options = ExpensePriority.entries,
                    selected = selectedPriority,
                    onSelected = { selectedPriority = it },
                    modifier = Modifier.fillMaxWidth(),
                )

                DropdownMenuBox(
                    label = "Frequency",
                    options = ExpenseFrequency.entries,
                    selected = selectedFrequency,
                    onSelected = { selectedFrequency = it },
                    modifier = Modifier.fillMaxWidth(),
                )

                if (selectedFrequency == ExpenseFrequency.CUSTOM) {
                    OutlinedTextField(
                        value = customFrequencyDays,
                        onValueChange = { customFrequencyDays = it },
                        label = { Text("Custom Frequency (days)") },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number,
                        ),
                    )
                }

                DropdownMenuBox(
                    label = "Category",
                    options = ExpenseCategory.entries,
                    selected = selectedCategory,
                    onSelected = { selectedCategory = it },
                    modifier = Modifier.fillMaxWidth(),
                )

                OutlinedTextField(
                    value = brand,
                    onValueChange = { brand = it },
                    label = { Text("Brand") },
                )

                OutlinedTextField(
                    value = provider,
                    onValueChange = { provider = it },
                    label = { Text("Provider") },
                )

                OutlinedTextField(
                    value = linkToPurchase,
                    onValueChange = { linkToPurchase = it },
                    label = { Text("Link to Purchase") },
                )

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Note") },
                    maxLines = 3,
                )

                // Date Picker
                OutlinedButton(
                    onClick = { showPurchaseDatePicker = true },
                ) {
                    Text("Purchase Date: ${purchaseDate.format(DateTimeFormatter.ISO_DATE)}")
                }
                if (showPurchaseDatePicker) {
                    DatePickerDialog(
                        onDismissRequest = { showPurchaseDatePicker = false },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    purchaseDatePickerState.selectedDateMillis?.let {
                                        purchaseDate =
                                            Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault())
                                                .toLocalDate()
                                    }
                                    showPurchaseDatePicker = false
                                },
                            ) { Text("OK") }
                        },
                        dismissButton = {
                            TextButton(onClick = { showPurchaseDatePicker = false }) {
                                Text("Cancel")
                            }
                        },
                    ) {
                        DatePicker(state = purchaseDatePickerState)
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
    )
}

@Preview(showBackground = true)
@Composable
fun EditExpensePreviewDialog() {
    EditExpenseDialog(
        expense = Expense(
            name = "Rent",
            amount = 1200.0,
            priority = ExpensePriority.REQUIRED,
            frequency = ExpenseFrequency.MONTHLY,
            category = ExpenseCategory.HOME,
        ),
        onDismiss = {},
        onSave = {},
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun EditExpenseDialogPreviewDark() {
    MyBudgetTheme {
        EditExpenseDialog(
            expense = Expense(
                name = "Rent",
                amount = 1200.0,
                priority = ExpensePriority.REQUIRED,
                frequency = ExpenseFrequency.MONTHLY,
                category = ExpenseCategory.HOME,
            ),
            onDismiss = {},
            onSave = {},
        )
    }
}
