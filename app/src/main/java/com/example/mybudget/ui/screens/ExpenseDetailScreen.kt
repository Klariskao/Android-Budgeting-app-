package com.example.mybudget.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import com.example.mybudget.data.local.MockExpenseDao
import com.example.mybudget.data.local.MockIncomeDao
import com.example.mybudget.data.local.SettingsDataStoreImpl
import com.example.mybudget.data.model.Expense
import com.example.mybudget.data.model.ExpenseCategory
import com.example.mybudget.data.model.ExpenseFrequency
import com.example.mybudget.data.model.ExpensePriority
import com.example.mybudget.repository.BudgetRepositoryImpl
import com.example.mybudget.ui.ExpenseDetailViewModel
import com.example.mybudget.ui.dialogs.DeleteConfirmationDialog
import com.example.mybudget.ui.dialogs.EditExpenseDialog
import com.example.mybudget.ui.helpers.calculateNextPurchaseDate
import com.example.mybudget.ui.helpers.formatCurrency
import com.example.mybudget.ui.model.ExpenseDetailEvent
import com.example.mybudget.ui.theme.MyBudgetTheme
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.getKoin

@Composable
fun ExpenseDetailScreen(viewModel: ExpenseDetailViewModel, navController: NavController) {
    val settingsDataStore: SettingsDataStoreImpl = getKoin().get()
    val expense by viewModel.expenseFlow.collectAsState(initial = null)
    val currency by settingsDataStore.currencyFlow.collectAsState(initial = "USD")

    LaunchedEffect(Unit) {
        viewModel.navigateBack.collectLatest {
            navController.popBackStack()
        }
    }

    expense?.let { safeExpense ->
        ExpenseDetailScreenContent(
            expense = safeExpense,
            currency = currency,
            viewModel = viewModel,
        )
    } ?: CircularProgressIndicator()
}

@Composable
fun ExpenseDetailScreenContent(
    expense: Expense,
    currency: String,
    viewModel: ExpenseDetailViewModel,
) {
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Text(
            text = expense.name,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                InfoRow(label = "Amount", value = formatCurrency(expense.amount, currency))
                InfoRow(label = "Category", value = expense.category.label)
                InfoRow(label = "Priority", value = expense.priority.label)
                InfoRow(label = "Frequency", value = expense.frequency.label)
                if (expense.frequency == ExpenseFrequency.CUSTOM &&
                    expense.customFrequencyInDays != null
                ) {
                    InfoRow(
                        label = "Custom Frequency",
                        value = "${expense.customFrequencyInDays} days",
                    )
                }
                InfoRow(label = "Purchase Date", value = expense.purchaseDate.toString())
                val nextPurchaseDate = calculateNextPurchaseDate(
                    expense.purchaseDate,
                    expense.frequency,
                    expense.customFrequencyInDays,
                    expense.repetitions,
                    expense.endDate,
                )
                if (nextPurchaseDate != null) {
                    InfoRow(
                        label = "Next Purchase",
                        value = nextPurchaseDate.toString(),
                    )
                }
                if (expense.brand.isNotBlank()) {
                    InfoRow(label = "Brand", value = expense.brand)
                }
                if (expense.provider.isNotBlank()) {
                    InfoRow(label = "Provider", value = expense.provider)
                }
                if (expense.linkToPurchase.isNotBlank()) {
                    InfoRow(label = "Purchase Link", value = expense.linkToPurchase)
                }
                if (!expense.note.isNullOrBlank()) {
                    InfoRow(label = "Note", value = expense.note)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Edit / Delete Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            OutlinedButton(onClick = { showEditDialog = true }) {
                Text("Edit")
            }
            OutlinedButton(
                onClick = { showDeleteDialog = true },
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error,
                ),
            ) {
                Text("Delete")
            }
        }
    }
    // Edit Dialog
    if (showEditDialog) {
        EditExpenseDialog(
            expense = expense,
            onDismiss = { showEditDialog = false },
            onSave = {
                viewModel.onEvent(ExpenseDetailEvent.UpdateExpense(it))
                showEditDialog = false
            },
        )
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog) {
        DeleteConfirmationDialog(
            name = expense.name,
            onConfirm = {
                viewModel.onEvent(ExpenseDetailEvent.RemoveExpense(expense))
                showDeleteDialog = false
            },
            onDismiss = { showDeleteDialog = false },
        )
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Divider(
            modifier = Modifier.padding(top = 4.dp),
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
        )
    }
}

val mockExpense = Expense(
    name = "Rent",
    amount = 1000.0,
    priority = ExpensePriority.REQUIRED,
    frequency = ExpenseFrequency.MONTHLY,
    category = ExpenseCategory.HOME,
)

@Preview(showBackground = true)
@Composable
fun ExpenseDetailScreenPreview() {
    MaterialTheme {
        ExpenseDetailScreenContent(
            expense = mockExpense,
            currency = "USD",
            viewModel = ExpenseDetailViewModel(
                savedStateHandle = SavedStateHandle(mapOf("expenseId" to 1L)),
                repository = BudgetRepositoryImpl(
                    MockExpenseDao(),
                    MockIncomeDao(),
                ),
            ),
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ExpenseDetailScreenPreviewDark() {
    MyBudgetTheme(isDarkTheme = true) {
        ExpenseDetailScreenContent(
            expense = mockExpense,
            currency = "USD",
            viewModel = ExpenseDetailViewModel(
                savedStateHandle = SavedStateHandle(mapOf("expenseId" to 1L)),
                repository = BudgetRepositoryImpl(
                    MockExpenseDao(),
                    MockIncomeDao(),
                ),
            ),
        )
    }
}
