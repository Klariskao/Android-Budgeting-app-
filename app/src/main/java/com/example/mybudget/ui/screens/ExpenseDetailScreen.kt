package com.example.mybudget.ui.screens

import android.content.res.Configuration
import android.util.Log
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
import androidx.navigation.compose.rememberNavController
import com.example.mybudget.data.local.MockExpenseDao
import com.example.mybudget.data.local.MockIncomeDao
import com.example.mybudget.data.model.ExpenseFrequency
import com.example.mybudget.repository.BudgetRepositoryImpl
import com.example.mybudget.ui.ExpenseDetailViewModel
import com.example.mybudget.ui.dialogs.DeleteConfirmationDialog
import com.example.mybudget.ui.dialogs.EditExpenseDialog
import com.example.mybudget.ui.helpers.calculateNextPurchaseDate
import com.example.mybudget.ui.helpers.formatCurrency
import com.example.mybudget.ui.model.ExpenseDetailEvent
import com.example.mybudget.ui.theme.MyBudgetTheme
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ExpenseDetailScreen(viewModel: ExpenseDetailViewModel, navController: NavController) {
    val expense by viewModel.expenseFlow.collectAsState(initial = null)

    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.navigateBack.collectLatest {
            navController.popBackStack()
        }
    }

    expense?.let { safeExpense ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Text(
                text = safeExpense.name,
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
                    InfoRow(label = "Amount", value = formatCurrency(safeExpense.amount))
                    InfoRow(label = "Category", value = safeExpense.category.label)
                    InfoRow(label = "Priority", value = safeExpense.priority.label)
                    InfoRow(label = "Frequency", value = safeExpense.frequency.label)
                    if (safeExpense.frequency == ExpenseFrequency.CUSTOM &&
                        safeExpense.customFrequencyInDays != null
                    ) {
                        InfoRow(
                            label = "Custom Frequency",
                            value = "${safeExpense.customFrequencyInDays} days",
                        )
                    }
                    InfoRow(label = "Purchase Date", value = safeExpense.purchaseDate.toString())
                    val nextPurchaseDate = calculateNextPurchaseDate(
                        safeExpense.purchaseDate,
                        safeExpense.frequency,
                        safeExpense.customFrequencyInDays,
                        safeExpense.repetitions,
                        safeExpense.endDate,
                    )
                    if (nextPurchaseDate != null) {
                        InfoRow(
                            label = "Next Purchase",
                            value = nextPurchaseDate.toString()
                        )
                    }
                        if (safeExpense.brand.isNotBlank()) {
                        InfoRow(label = "Brand", value = safeExpense.brand)
                    }
                    if (safeExpense.provider.isNotBlank()) {
                        InfoRow(label = "Provider", value = safeExpense.provider)
                    }
                    if (safeExpense.linkToPurchase.isNotBlank()) {
                        InfoRow(label = "Purchase Link", value = safeExpense.linkToPurchase)
                    }
                    if (!safeExpense.note.isNullOrBlank()) {
                        InfoRow(label = "Note", value = safeExpense.note ?: "")
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
                expense = safeExpense,
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
                name = safeExpense.name,
                onConfirm = {
                    viewModel.onEvent(ExpenseDetailEvent.RemoveExpense(safeExpense))
                    showDeleteDialog = false
                },
                onDismiss = { showDeleteDialog = false },
            )
        }
    } ?: CircularProgressIndicator()
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

@Preview(showBackground = true)
@Composable
fun ExpenseDetailScreenPreview() {
    MaterialTheme {
        ExpenseDetailScreen(
            viewModel =
            ExpenseDetailViewModel(
                savedStateHandle = SavedStateHandle(mapOf("expenseId" to 1L)),
                repository = BudgetRepositoryImpl(
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
fun ExpenseDetailScreenPreviewDark() {
    MyBudgetTheme {
        ExpenseDetailScreen(
            viewModel =
            ExpenseDetailViewModel(
                savedStateHandle = SavedStateHandle(mapOf("expenseId" to 1L)),
                repository = BudgetRepositoryImpl(
                    MockExpenseDao(),
                    MockIncomeDao(),
                ),
            ),
            navController = rememberNavController(),
        )
    }
}
