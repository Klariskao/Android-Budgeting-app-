package com.example.mybudget.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.mybudget.data.model.IncomeType
import com.example.mybudget.repository.BudgetRepositoryImpl
import com.example.mybudget.ui.AddIncomeViewModel
import com.example.mybudget.ui.components.BudgetItemCard
import com.example.mybudget.ui.model.AddIncomeEvent

@Composable
fun AddIncomeScreen(
    viewModel: AddIncomeViewModel,
    navController: NavController
) {
    val budget by viewModel.budget.collectAsState()
    val incomes = budget.incomes
    var name by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(IncomeType.MONTHLY) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is AddIncomeEvent.ShowToast -> Toast.makeText(
                    context,
                    event.message,
                    Toast.LENGTH_SHORT
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

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Add Income", style = MaterialTheme.typography.headlineSmall)

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
            options = IncomeType.entries.toTypedArray(),
            selectedOption = type,
            onOptionSelected = { type = it },
            label = "Type"
        )

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = {
                viewModel.onEvent(AddIncomeEvent.AddIncome(name, amount, type))
                name = ""
                amount = ""
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Add Income")
        }

        if (incomes.isNotEmpty()) {
            Spacer(Modifier.height(24.dp))

            Text("Added Incomes", style = MaterialTheme.typography.titleMedium)

            LazyColumn {
                items(incomes) {
                    BudgetItemCard(it.name, it.amount, "Type: ${it.type.name}")
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
            navController = rememberNavController()
        )
    }
}
