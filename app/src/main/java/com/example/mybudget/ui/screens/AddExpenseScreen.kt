package com.example.mybudget.ui.screens

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mybudget.data.local.MockExpenseDao
import com.example.mybudget.data.local.MockIncomeDao
import com.example.mybudget.data.model.ExpenseCategory
import com.example.mybudget.data.model.ExpenseFrequency
import com.example.mybudget.data.model.ExpensePriority
import com.example.mybudget.repository.BudgetRepositoryImpl
import com.example.mybudget.ui.AddExpenseViewModel
import com.example.mybudget.ui.components.BudgetItemCard
import com.example.mybudget.ui.helpers.calculateNextPurchaseDate
import com.example.mybudget.ui.model.AddExpenseEvent
import com.example.mybudget.ui.theme.MyBudgetTheme
import java.time.LocalDate

@Composable
fun AddExpenseScreen(viewModel: AddExpenseViewModel, navController: NavController) {
    val context = LocalContext.current

    val budget by viewModel.budget.collectAsState()
    val expenses = budget.expenses
    var name by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(ExpensePriority.REQUIRED) }
    var frequency by remember { mutableStateOf(ExpenseFrequency.MONTHLY) }
    var category by remember { mutableStateOf(ExpenseCategory.OTHER) }
    var brand by remember { mutableStateOf("") }
    var provider by remember { mutableStateOf("") }
    var linkToPurchase by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var customFrequencyInDays by remember { mutableStateOf("") }
    var purchaseDate by remember { mutableStateOf(LocalDate.now()) }

    // New state to control if repetitions input is shown
    var showRepetitions by remember { mutableStateOf(false) }
    var repetitions by remember { mutableStateOf("") }

    // New state to control if endDate input is shown
    var showEndDate by remember { mutableStateOf(false) }
    var endDate by remember { mutableStateOf<LocalDate>(LocalDate.now()) }

    val showCustomFrequency = frequency == ExpenseFrequency.CUSTOM

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is AddExpenseEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }

                is AddExpenseEvent.ExpenseAdded -> {
                    navController.popBackStack()
                }

                is AddExpenseEvent.AddExpense -> {
                    // Handled by the VM
                }
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
    ) {
        item {
            Text(
                text = "Add Expense",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.headlineSmall,
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth(),
            )

            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
            )

            DropdownSelector(
                options = ExpensePriority.entries,
                selectedOption = priority,
                onOptionSelected = { priority = it },
                label = "Priority",
            )

            DropdownSelector(
                options = ExpenseFrequency.entries,
                selectedOption = frequency,
                onOptionSelected = { frequency = it },
                label = "Frequency",
            )

            if (showCustomFrequency) {
                OutlinedTextField(
                    value = customFrequencyInDays,
                    onValueChange = { customFrequencyInDays = it },
                    label = { Text("Custom Frequency (days)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            DropdownSelector(
                options = ExpenseCategory.entries,
                selectedOption = category,
                onOptionSelected = { category = it },
                label = "Category",
            )

            OutlinedTextField(
                value = brand,
                onValueChange = { brand = it },
                label = { Text("Brand") },
                modifier = Modifier.fillMaxWidth(),
            )

            OutlinedTextField(
                value = provider,
                onValueChange = { provider = it },
                label = { Text("Provider") },
                modifier = Modifier.fillMaxWidth(),
            )

            OutlinedTextField(
                value = linkToPurchase,
                onValueChange = { linkToPurchase = it },
                label = { Text("Purchase Link") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
            )

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Note") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3,
            )

            DatePickerField(
                label = "First Purchase Date",
                selectedDate = purchaseDate,
                onDateSelected = { purchaseDate = it },
            )

            Spacer(Modifier.height(8.dp))

            // Checkbox to toggle repetitions input
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = showRepetitions,
                    onCheckedChange = {
                        showRepetitions = it
                        showEndDate = false
                    },
                )
                Spacer(Modifier.width(8.dp))
                Text(text = "Add number of repetitions", color = MaterialTheme.colorScheme.primary)
            }

            if (showRepetitions) {
                OutlinedTextField(
                    value = repetitions,
                    onValueChange = { repetitions = it },
                    label = { Text("Number of Repetitions") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            Spacer(Modifier.height(8.dp))

            // Checkbox to toggle end date input
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = showEndDate,
                    onCheckedChange = {
                        showEndDate = it
                        showRepetitions = false
                    },
                )
                Spacer(Modifier.width(8.dp))
                Text(text = "Add end date", color = MaterialTheme.colorScheme.primary)
            }

            if (showEndDate) {
                DatePickerField(
                    label = "End Date",
                    selectedDate = endDate,
                    onDateSelected = { endDate = it },
                )
            }

            Spacer(Modifier.height(8.dp))

            // Show calculated nextPurchaseDate
            val nextPurchaseDate = calculateNextPurchaseDate(
                purchaseDate,
                frequency,
                customFrequencyInDays.toIntOrNull(),
                repetitions.takeIf { showRepetitions }?.toIntOrNull(),
                if (showEndDate) endDate else null,
            )
            Text(
                text = "Next Purchase Date: ${nextPurchaseDate?.toString() ?: "N/A"}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 8.dp),
            )

            Button(
                onClick = {
                    viewModel.onEvent(
                        AddExpenseEvent.AddExpense(
                            name = name,
                            amount = amount,
                            priority = priority,
                            frequency = frequency,
                            category = category,
                            customFrequencyInDays = customFrequencyInDays.toIntOrNull(),
                            purchaseDate = purchaseDate,
                            brand = brand,
                            provider = provider,
                            linkToPurchase = linkToPurchase,
                            note = note,
                            repetitions = repetitions.takeIf { showRepetitions }?.toIntOrNull(),
                            endDate = if (showEndDate) endDate else null,
                        ),
                    )
                    // Reset form
                    name = ""
                    amount = ""
                    customFrequencyInDays = ""
                    brand = ""
                    provider = ""
                    linkToPurchase = ""
                    note = ""
                    purchaseDate = LocalDate.now()
                    repetitions = ""
                    endDate = LocalDate.now()
                    showRepetitions = false
                    showEndDate = false
                },
            ) {
                Text("Add Expense")
            }
        }
        if (expenses.isNotEmpty()) {
            item {
                Spacer(Modifier.height(24.dp))

                Text("Added Expenses", style = MaterialTheme.typography.titleMedium)
            }
            items(expenses) {
                BudgetItemCard(
                    it.name,
                    it.amount,
                    "${it.priority.name}, ${it.frequency.name}, ${it.category.name}",
                )
            }
        }
    }
}

@Composable
fun <T> DropdownSelector(
    options: List<T>,
    selectedOption: T,
    onOptionSelected: (T) -> Unit,
    label: String = "",
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Text(text = "$label: $selectedOption")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.toString()) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
fun DatePickerField(label: String, selectedDate: LocalDate, onDateSelected: (LocalDate) -> Unit) {
    val context = LocalContext.current
    val datePickerDialog = remember {
        android.app.DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                onDateSelected(LocalDate.of(year, month + 1, dayOfMonth))
            },
            selectedDate.year,
            selectedDate.monthValue - 1,
            selectedDate.dayOfMonth,
        )
    }

    OutlinedTextField(
        value = selectedDate.toString(),
        onValueChange = {},
        readOnly = true,
        label = { Text(label) },
        trailingIcon = {
            IconButton(
                onClick = { datePickerDialog.show() },
            ) {
                Icon(Icons.Default.DateRange, contentDescription = "Select date")
            }
        },
        modifier = Modifier.fillMaxWidth(),
    )
}

@Preview(showBackground = true)
@Composable
fun AddExpenseScreenPreview() {
    MaterialTheme {
        AddExpenseScreen(
            viewModel = AddExpenseViewModel(
                BudgetRepositoryImpl(
                    MockExpenseDao(),
                    MockIncomeDao(),
                ),
            ),
            navController = rememberNavController(),
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun AddExpenseScreenPreviewDark() {
    MyBudgetTheme(isDarkTheme = true) {
        AddExpenseScreen(
            viewModel = AddExpenseViewModel(
                BudgetRepositoryImpl(
                    MockExpenseDao(),
                    MockIncomeDao(),
                ),
            ),
            navController = rememberNavController(),
        )
    }
}
