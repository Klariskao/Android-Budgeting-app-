package com.example.mybudget.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mybudget.data.local.MockExpenseDao
import com.example.mybudget.data.local.MockIncomeDao
import com.example.mybudget.data.model.Budget
import com.example.mybudget.data.model.ExpenseFrequency
import com.example.mybudget.data.model.IncomeFrequency
import com.example.mybudget.repository.BudgetRepositoryImpl
import com.example.mybudget.ui.BudgetViewModel
import com.example.mybudget.ui.dialogs.DeleteConfirmationDialog
import com.example.mybudget.ui.dialogs.EditExpenseDialog
import com.example.mybudget.ui.dialogs.EditIncomeDialog
import com.example.mybudget.ui.model.BudgetDialogState
import com.example.mybudget.ui.model.BudgetEvent

@Composable
fun BudgetScreen(
    viewModel: BudgetViewModel,
    navController: NavController
) {
    val budget by viewModel.budget.collectAsState()
    val availableFunds = viewModel.calculateAvailableFunds()
    val dialogState = viewModel.dialogState

    dialogState?.let { state ->
        when (state) {
            is BudgetDialogState.ConfirmDeleteIncome -> {
                DeleteConfirmationDialog(
                    message = state.income.name,
                    onConfirm = {
                        viewModel.onEvent(BudgetEvent.RemoveIncome(state.income))
                        viewModel.onEvent(BudgetEvent.CloseDialog)
                    },
                    onDismiss = { viewModel.onEvent(BudgetEvent.CloseDialog) }
                )
            }

            is BudgetDialogState.ConfirmDeleteExpense -> {
                DeleteConfirmationDialog(
                    message = state.expense.name,
                    onConfirm = {
                        viewModel.onEvent(BudgetEvent.RemoveExpense(state.expense))
                        viewModel.onEvent(BudgetEvent.CloseDialog)
                    },
                    onDismiss = { viewModel.onEvent(BudgetEvent.CloseDialog) }
                )
            }

            is BudgetDialogState.EditIncome -> {
                EditIncomeDialog(
                    income = state.income,
                    onSave = {
                        viewModel.onEvent(BudgetEvent.UpdateIncome(it))
                        viewModel.onEvent(BudgetEvent.CloseDialog)
                    },
                    onDismiss = { viewModel.onEvent(BudgetEvent.CloseDialog) }
                )
            }

            is BudgetDialogState.EditExpense -> {
                EditExpenseDialog(
                    expense = state.expense,
                    onSave = {
                        viewModel.onEvent(BudgetEvent.UpdateExpense(it))
                        viewModel.onEvent(BudgetEvent.CloseDialog)
                    },
                    onDismiss = { viewModel.onEvent(BudgetEvent.CloseDialog) }
                )
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.End,
                modifier = Modifier.padding(end = 16.dp, bottom = 16.dp)
            ) {
                ExtendedFloatingActionButton(
                    text = { Text("Add Income") },
                    icon = { Icon(Icons.Filled.AttachMoney, null) },
                    onClick = { navController.navigate("income") },
                    containerColor = Color(0xFF388E3C),
                    contentColor = Color.White
                )
                ExtendedFloatingActionButton(
                    text = { Text("Add Expense") },
                    icon = { Icon(Icons.Filled.MoneyOff, null) },
                    onClick = { navController.navigate("expense") },
                    containerColor = Color(0xFFD32F2F),
                    contentColor = Color.White
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                BudgetSummaryCard(budget)
            }

            item {
                Text("Incomes", style = MaterialTheme.typography.titleMedium)
            }

            if (budget.incomes.isEmpty()) {
                item {
                    Text("No incomes added yet.", style = MaterialTheme.typography.bodyMedium)
                }
            } else {
                items(
                    items = budget.incomes,
                    key = { "income-${it.id}" }
                ) { income ->
                    SwipeableIncomeExpenseItem(
                        title = income.name,
                        amount = income.amount,
                        subtitle = income.frequency.name,
                        icon = Icons.Filled.AttachMoney,
                        color = Color(0xFF388E3C),
                        onDelete = {
                            viewModel.onEvent(
                                BudgetEvent.ConfirmRemoveIncome(income)
                            )
                        },
                        onEdit = {
                            viewModel.onEvent(
                                BudgetEvent.EditIncome(income)
                            )
                        }
                    )
                }
            }

            item {
                Spacer(Modifier.height(12.dp))
                Text("Expenses", style = MaterialTheme.typography.titleMedium)
            }

            if (budget.expenses.isEmpty()) {
                item {
                    Text("No expenses added yet.", style = MaterialTheme.typography.bodyMedium)
                }
            } else {
                items(
                    items = budget.expenses,
                    key = { "expense-${it.id}" }
                ) { expense ->
                    SwipeableIncomeExpenseItem(
                        title = expense.name,
                        amount = expense.amount,
                        subtitle = "${expense.frequency.name}, ${expense.priority.name}",
                        icon = Icons.Filled.MoneyOff,
                        color = Color(0xFFD32F2F),
                        onDelete = {
                            viewModel.onEvent(
                                BudgetEvent.ConfirmRemoveExpense(expense)
                            )
                        },
                        onEdit = {
                            viewModel.onEvent(
                                BudgetEvent.EditExpense(expense)
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun BudgetSummaryCard(
    budget: Budget,
    modifier: Modifier = Modifier
) {
    val monthlyIncome = budget.incomes
        .filter { it.frequency == IncomeFrequency.MONTHLY }
        .sumOf { it.amount }

    val yearlyIncome = budget.incomes
        .filter { it.frequency == IncomeFrequency.YEARLY }
        .sumOf { it.amount }

    val monthlyExpenses = budget.expenses
        .filter { it.frequency == ExpenseFrequency.MONTHLY }
        .sumOf { it.amount }

    val yearlyExpenses = budget.expenses
        .filter { it.frequency == ExpenseFrequency.YEARLY }
        .sumOf { it.amount }

    val totalIncome = budget.incomes.sumOf { it.amount }
    val totalExpenses = budget.expenses.sumOf { it.amount }
    val availableFunds = totalIncome - totalExpenses

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Budget Summary", style = MaterialTheme.typography.titleMedium)

            Spacer(Modifier.height(12.dp))

            SummaryRow("Total Income", totalIncome)
            SummaryRow("Total Expenses", totalExpenses)
            SummaryRow("Available Funds", availableFunds)

            Spacer(Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { (totalExpenses / totalIncome).toFloat().coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(8.dp)),
            )

            Spacer(Modifier.height(12.dp))

            Text(
                "Income Breakdown",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold
            )
            SummaryRow("Monthly Income", monthlyIncome)
            SummaryRow("Yearly Income", yearlyIncome)

            Spacer(Modifier.height(8.dp))

            Text(
                "Expense Breakdown",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold
            )
            SummaryRow("Monthly Expenses", monthlyExpenses)
            SummaryRow("Yearly Expenses", yearlyExpenses)
        }
    }
}

@Composable
fun SummaryRow(label: String, amount: Double) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label)
        Text("$%.2f".format(amount), fontWeight = FontWeight.Medium)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeableIncomeExpenseItem(
    title: String,
    subtitle: String,
    amount: Double,
    icon: ImageVector,
    color: Color,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    var itemHeight by remember { mutableIntStateOf(0) }
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            when (value) {
                SwipeToDismissBoxValue.StartToEnd -> {
                    onEdit()
                    false // Don't auto-dismiss on edit
                }
                SwipeToDismissBoxValue.EndToStart -> {
                    onDelete()
                    false // Don't auto-dismiss on delete
                }
                SwipeToDismissBoxValue.Settled -> false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val direction = dismissState.dismissDirection
            val isStartToEnd = direction == SwipeToDismissBoxValue.StartToEnd

            val backgroundColor = when (direction) {
                SwipeToDismissBoxValue.StartToEnd -> Color(0xFFB9F6CA) // light green
                SwipeToDismissBoxValue.EndToStart -> Color(0xFFFFCDD2) // light red
                else -> Color.Transparent
            }

            val icon = when (direction) {
                SwipeToDismissBoxValue.StartToEnd -> Icons.Default.Edit
                SwipeToDismissBoxValue.EndToStart -> Icons.Default.Delete
                else -> null
            }

            val label = when (direction) {
                SwipeToDismissBoxValue.StartToEnd -> "Edit"
                SwipeToDismissBoxValue.EndToStart -> "Delete"
                else -> ""
            }

            if (icon != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(with(LocalDensity.current) { itemHeight.toDp() })
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(backgroundColor),
                    contentAlignment = if (isStartToEnd) Alignment.CenterStart else Alignment.CenterEnd
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                    ) {
                        Icon(icon, contentDescription = label, tint = Color.Black)
                        Text(label, color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        content = {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .onGloballyPositioned { coordinates ->
                        itemHeight = coordinates.size.height
                    },
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                ListItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color),
                    leadingContent = {
                        Icon(
                            icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    headlineContent = { Text(title, fontWeight = FontWeight.SemiBold) },
                    supportingContent = { Text(subtitle) },
                    trailingContent = {
                        Text(
                            "$%.2f".format(amount),
                            fontWeight = FontWeight.Medium,
                            color = Color.DarkGray
                        )
                    }
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun BudgetScreenPreview() {
    MaterialTheme {
        BudgetScreen(
            viewModel = BudgetViewModel(
                BudgetRepositoryImpl(
                    MockExpenseDao(),
                    MockIncomeDao()
                )
            ),
            navController = rememberNavController()
        )
    }
}
