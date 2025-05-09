package com.example.mybudget.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.mybudget.data.local.MockExpenseDao
import com.example.mybudget.data.model.ExpenseFrequency
import com.example.mybudget.data.model.ExpenseType
import com.example.mybudget.repository.BudgetRepositoryImpl
import com.example.mybudget.ui.AddExpenseViewModel
import com.example.mybudget.ui.components.BudgetItemCard
import com.example.mybudget.ui.model.AddExpenseEvent

@Composable
fun AddExpenseScreen(viewModel: AddExpenseViewModel) {
    val context = LocalContext.current

    val expenses = viewModel.budget.value.expenses
    var name by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(ExpenseType.REQUIRED) }
    var frequency by remember { mutableStateOf(ExpenseFrequency.MONTHLY) }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is AddExpenseEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }

                is AddExpenseEvent.AddExpense -> {
                    // Handled by the VM
                }
            }
        }
    }

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text("Add Expense", style = MaterialTheme.typography.headlineSmall)

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Amount") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        DropdownSelector(
            options = ExpenseType.entries.toTypedArray(),
            selectedOption = type,
            onOptionSelected = { type = it },
            label = "Type"
        )

        DropdownSelector(
            options = ExpenseFrequency.entries.toTypedArray(),
            selectedOption = frequency,
            onOptionSelected = { frequency = it },
            label = "Frequency"
        )

        Spacer(Modifier.height(8.dp))

        Button(onClick = {
            viewModel.onEvent(AddExpenseEvent.AddExpense(name, amount, type, frequency))
            name = ""
            amount = ""
        }, modifier = Modifier.align(Alignment.End)) {
            Text("Add Expense")
        }

        if (expenses.isNotEmpty()) {
            Spacer(Modifier.height(24.dp))

            Text("Added Expenses", style = MaterialTheme.typography.titleMedium)

            LazyColumn {
                items(expenses) {
                    BudgetItemCard(it.name, it.amount, "${it.type.name}, ${it.frequency.name}")
                }
            }
        }
    }
}

@Composable
fun <T> DropdownSelector(
    options: Array<T>,
    selectedOption: T,
    onOptionSelected: (T) -> Unit,
    label: String = ""
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Text(text = "$label: ${selectedOption.toString()}")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.toString()) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddExpenseScreenPreview() {
    MaterialTheme {
        AddExpenseScreen(
            viewModel = AddExpenseViewModel(BudgetRepositoryImpl(MockExpenseDao()))
        )
    }
}
