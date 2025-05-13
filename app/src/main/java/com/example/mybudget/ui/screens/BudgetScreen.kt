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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
                BudgetSummaryCard(budget, availableFunds)
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
fun BudgetSummaryCard(budget: Budget, availableFunds: Double) {
    val totalIncome = budget.incomes.sumOf { it.amount }
    val totalExpense = budget.expenses.sumOf { it.amount }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9)),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("This Month", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(12.dp))
            SummaryRow("Total Income", totalIncome, Color(0xFF388E3C))
            SummaryRow("Total Expenses", totalExpense, Color(0xFFD32F2F))
            HorizontalDivider(Modifier.padding(vertical = 8.dp))
            SummaryRow(
                "Available Funds",
                availableFunds,
                if (availableFunds >= 0) Color(0xFF00796B) else Color.Red
            )
        }
    }
}

@Composable
fun SummaryRow(label: String, amount: Double, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Text(
            "$%.2f".format(amount),
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            color = color
        )
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
