package com.example.mybudget.ui.screens

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
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
import com.example.mybudget.data.model.IncomeFrequency
import com.example.mybudget.repository.BudgetRepositoryImpl
import com.example.mybudget.ui.AddIncomeViewModel
import com.example.mybudget.ui.components.BudgetItemCard
import com.example.mybudget.ui.model.AddIncomeEvent
import com.example.mybudget.ui.theme.MyBudgetTheme
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddIncomeScreen(viewModel: AddIncomeViewModel, navController: NavController) {
    val context = LocalContext.current
    val budget by viewModel.budget.collectAsState()
    val incomes = budget.incomes
    var name by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var frequency by remember { mutableStateOf(IncomeFrequency.MONTHLY) }
    var firstPaymentDate by remember { mutableStateOf(LocalDate.now()) }
    var customFrequencyInDays by remember { mutableStateOf("") }
    val showCustomFrequency = frequency == IncomeFrequency.CUSTOM

    val datePickerState =
        rememberDatePickerState(
            initialSelectedDateMillis = firstPaymentDate.toEpochDay() * 86400000,
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

    LaunchedEffect(true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is AddIncomeEvent.ShowToast -> Toast.makeText(
                    context,
                    event.message,
                    Toast.LENGTH_SHORT,
                ).show()

                is AddIncomeEvent.IncomeAdded -> {
                    navController.popBackStack() // go back to BudgetScreen
                }

                is AddIncomeEvent.AddIncome -> {
                    // Handled by the VM
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
    ) {
        Text("Add Income", style = MaterialTheme.typography.headlineSmall)

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
            options = IncomeFrequency.entries,
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

        Spacer(Modifier.height(8.dp))

        OutlinedButton(
            onClick = { datePickerShown.value = true },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                "First Payment Date: ${firstPaymentDate.format(
                    DateTimeFormatter.ofPattern("MMM dd, yyyy"),
                )}",
            )
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.onEvent(
                    AddIncomeEvent.AddIncome(
                        name = name,
                        amount = amount,
                        frequency = frequency,
                        firstPaymentDate = firstPaymentDate,
                        customFrequencyInDays = customFrequencyInDays.toIntOrNull(),
                    ),
                )
            },
            modifier = Modifier.align(Alignment.End),
        ) {
            Text("Add Income")
        }

        if (incomes.isNotEmpty()) {
            Spacer(Modifier.height(24.dp))

            Text("Added Incomes", style = MaterialTheme.typography.titleMedium)

            LazyColumn {
                items(incomes) {
                    BudgetItemCard(it.name, it.amount, "Type: ${it.frequency.name}")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddIncomeScreenPreview() {
    MaterialTheme {
        AddIncomeScreen(
            viewModel = AddIncomeViewModel(BudgetRepositoryImpl(MockExpenseDao(), MockIncomeDao())),
            navController = rememberNavController(),
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun AddIncomeScreenPreviewDark() {
    MyBudgetTheme(isDarkTheme = true) {
        AddIncomeScreen(
            viewModel = AddIncomeViewModel(BudgetRepositoryImpl(MockExpenseDao(), MockIncomeDao())),
            navController = rememberNavController(),
        )
    }
}
