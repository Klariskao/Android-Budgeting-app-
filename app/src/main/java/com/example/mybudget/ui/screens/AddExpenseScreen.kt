package com.example.mybudget.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mybudget.AddExpenseViewModel
import com.example.mybudget.data.local.MockExpenseDao
import com.example.mybudget.data.model.ExpenseFrequency
import com.example.mybudget.data.model.ExpenseType
import com.example.mybudget.repository.BudgetRepositoryImpl
import com.example.mybudget.ui.model.AddExpenseEvent

@Composable
fun AddExpenseScreen(viewModel: AddExpenseViewModel) {
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var expenseType by remember { mutableStateOf(ExpenseType.REQUIRED) }
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
        TextField(value = name, onValueChange = { name = it }, label = { Text("Expense Name") })
        TextField(value = amount, onValueChange = { amount = it }, label = { Text("Amount") })

        DropdownSelector(
            options = ExpenseType.entries.toTypedArray(),
            selectedOption = expenseType,
            onOptionSelected = { expenseType = it }
        )

        DropdownSelector(
            options = ExpenseFrequency.entries.toTypedArray(),
            selectedOption = frequency,
            onOptionSelected = { frequency = it }
        )

        Button(
            onClick = {
                viewModel.onEvent(
                    AddExpenseEvent.AddExpense(
                        name = name,
                        amount = amount,
                        type = expenseType,
                        frequency = frequency
                    )
                )
            }
        ) {
            Text("Add Expense")
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
