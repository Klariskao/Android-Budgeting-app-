package com.example.mybudget.ui.screens

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mybudget.data.local.MockExpenseDao
import com.example.mybudget.data.local.MockIncomeDao
import com.example.mybudget.data.model.Budget
import com.example.mybudget.data.model.ExpenseFrequency
import com.example.mybudget.data.model.ExpensePriority
import com.example.mybudget.data.model.IncomeFrequency
import com.example.mybudget.repository.BudgetRepositoryImpl
import com.example.mybudget.ui.BudgetViewModel
import com.example.mybudget.ui.dialogs.DeleteConfirmationDialog
import com.example.mybudget.ui.dialogs.EditExpenseDialog
import com.example.mybudget.ui.dialogs.EditIncomeDialog
import com.example.mybudget.ui.helpers.formatCurrency
import com.example.mybudget.ui.model.BudgetDialogState
import com.example.mybudget.ui.model.BudgetEvent
import com.example.mybudget.ui.model.ExpensesSortOption
import com.example.mybudget.ui.navigation.Screen
import com.example.mybudget.ui.theme.Green40
import com.example.mybudget.ui.theme.Green80
import com.example.mybudget.ui.theme.MyBudgetTheme
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

@Composable
fun BudgetScreen(viewModel: BudgetViewModel, navController: NavController) {
    val budget by viewModel.budget.collectAsState()
    val dialogState = viewModel.dialogState
    var sortOption by remember { mutableStateOf(ExpensesSortOption.NONE) }
    var selectedExpensePriorities by remember { mutableStateOf(setOf<ExpensePriority>()) }

    dialogState?.let { state ->
        when (state) {
            is BudgetDialogState.ConfirmDeleteIncome -> {
                DeleteConfirmationDialog(
                    message = state.income.name,
                    onConfirm = {
                        viewModel.onEvent(BudgetEvent.RemoveIncome(state.income))
                        viewModel.onEvent(BudgetEvent.CloseDialog)
                    },
                    onDismiss = { viewModel.onEvent(BudgetEvent.CloseDialog) },
                )
            }

            is BudgetDialogState.ConfirmDeleteExpense -> {
                DeleteConfirmationDialog(
                    message = state.expense.name,
                    onConfirm = {
                        viewModel.onEvent(BudgetEvent.RemoveExpense(state.expense))
                        viewModel.onEvent(BudgetEvent.CloseDialog)
                    },
                    onDismiss = { viewModel.onEvent(BudgetEvent.CloseDialog) },
                )
            }

            is BudgetDialogState.EditIncome -> {
                EditIncomeDialog(
                    income = state.income,
                    onSave = {
                        viewModel.onEvent(BudgetEvent.UpdateIncome(it))
                        viewModel.onEvent(BudgetEvent.CloseDialog)
                    },
                    onDismiss = { viewModel.onEvent(BudgetEvent.CloseDialog) },
                )
            }

            is BudgetDialogState.EditExpense -> {
                EditExpenseDialog(
                    expense = state.expense,
                    onSave = {
                        viewModel.onEvent(BudgetEvent.UpdateExpense(it))
                        viewModel.onEvent(BudgetEvent.CloseDialog)
                    },
                    onDismiss = { viewModel.onEvent(BudgetEvent.CloseDialog) },
                )
            }
        }
    }

    Scaffold { padding ->
        LazyColumn(
            modifier =
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                BudgetSummaryCard(budget)
            }

            item {
                IncomeHeader(
                    onAddIncome = { navController.navigate("income") },
                )
            }

            if (budget.incomes.isEmpty()) {
                item {
                    Text("No incomes added yet.", style = MaterialTheme.typography.bodyMedium)
                }
            } else {
                items(
                    items = budget.incomes,
                    key = { "income-${it.id}" },
                ) { income ->
                    SwipeableIncomeExpenseItem(
                        title = income.name,
                        amount = income.amount,
                        subtitle = income.frequency.name,
                        icon = Icons.Filled.AttachMoney,
                        color = Color(0xFF388E3C),
                        onDelete = {
                            viewModel.onEvent(
                                BudgetEvent.ConfirmRemoveIncome(income),
                            )
                        },
                        onEdit = {
                            viewModel.onEvent(
                                BudgetEvent.EditIncome(income),
                            )
                        },
                        onClick = {},
                    )
                }
            }

            item {
                Spacer(Modifier.height(12.dp))
                ExpenseHeader(
                    onAddExpense = { navController.navigate("expense") },
                )
            }

            if (budget.expenses.isEmpty()) {
                item {
                    Text("No expenses added yet.", style = MaterialTheme.typography.bodyMedium)
                }
            } else {
                val sortedExpenses = viewModel.sortExpenses(budget.expenses, sortOption)
                val filteredExpenses =
                    sortedExpenses.filter {
                        selectedExpensePriorities.isEmpty() ||
                            selectedExpensePriorities.contains(it.priority)
                    }

                item {
                    SortFilterBar(
                        selectedSort = sortOption,
                        selectedExpensePriorities = selectedExpensePriorities,
                        onSortChange = { sortOption = it },
                        onFilterChange = { type ->
                            selectedExpensePriorities =
                                if (type in selectedExpensePriorities) {
                                    selectedExpensePriorities - type
                                } else {
                                    selectedExpensePriorities + type
                                }
                        },
                    )

                    Spacer(Modifier.height(12.dp))

                    ExpensePieChart(
                        data = budget.expenses.map { it.category.name to it.amount.toFloat() },
                    )
                }
                items(
                    items = filteredExpenses,
                    key = { "expense-${it.id}" },
                ) { expense ->
                    SwipeableIncomeExpenseItem(
                        title = expense.name,
                        amount = expense.amount,
                        subtitle = "${expense.frequency.name}, ${expense.priority.name}",
                        icon = Icons.Filled.MoneyOff,
                        color = Color(0xFFD32F2F),
                        onDelete = {
                            viewModel.onEvent(
                                BudgetEvent.ConfirmRemoveExpense(expense),
                            )
                        },
                        onEdit = {
                            viewModel.onEvent(
                                BudgetEvent.EditExpense(expense),
                            )
                        },
                        onClick = {
                            navController.navigate(Screen.ExpenseDetail.createRoute(expense.id))
                        },
                    )
                }
            }
        }
    }
}

@Composable
fun BudgetSummaryCard(budget: Budget, modifier: Modifier = Modifier) {
    val monthlyIncome =
        budget.incomes
            .filter { it.frequency == IncomeFrequency.MONTHLY }
            .sumOf { it.amount }

    val yearlyIncome =
        budget.incomes
            .filter { it.frequency == IncomeFrequency.YEARLY }
            .sumOf { it.amount }

    val monthlyExpenses =
        budget.expenses
            .filter { it.frequency == ExpenseFrequency.MONTHLY }
            .sumOf { it.amount }

    val yearlyExpenses =
        budget.expenses
            .filter { it.frequency == ExpenseFrequency.YEARLY }
            .sumOf { it.amount }

    val totalIncome = budget.incomes.sumOf { it.amount }
    val totalExpenses = budget.expenses.sumOf { it.amount }
    val availableFunds = totalIncome - totalExpenses

    Card(
        modifier =
        modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Budget Summary", style = MaterialTheme.typography.titleMedium)

            Spacer(Modifier.height(12.dp))

            SummaryRow("Total Income", totalIncome)
            SummaryRow("Total Expenses", totalExpenses)
            SummaryRow("Available Funds", availableFunds)

            Spacer(Modifier.height(12.dp))

            val progress = (totalExpenses / totalIncome).toFloat().coerceIn(0f, 1f)
            LinearProgressIndicator(
                progress = { progress },
                modifier =
                Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(8.dp)),
                color =
                when {
                    progress < 0.5f -> customGreen
                    progress == 1f -> MaterialTheme.colorScheme.error
                    else -> ProgressIndicatorDefaults.linearColor
                },
            )

            Spacer(Modifier.height(12.dp))

            Text(
                "Income Breakdown",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
            )
            SummaryRow("Monthly Income", monthlyIncome)
            SummaryRow("Yearly Income", yearlyIncome)

            Spacer(Modifier.height(8.dp))

            Text(
                "Expense Breakdown",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
            )
            SummaryRow("Monthly Expenses", monthlyExpenses)
            SummaryRow("Yearly Expenses", yearlyExpenses)
        }
    }
}

@Composable
fun SummaryRow(label: String, amount: Double) {
    Row(
        modifier =
        Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label)
        Text(formatCurrency(amount), fontWeight = FontWeight.Medium)
    }
}

@Composable
fun IncomeHeader(onAddIncome: () -> Unit) {
    Row(
        modifier =
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("Incomes", style = MaterialTheme.typography.titleLarge)
        TextButton(
            onClick = onAddIncome,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
            colors =
            ButtonDefaults.outlinedButtonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.primary,
            ),
        ) {
            Icon(Icons.Default.AttachMoney, contentDescription = "Add Income")
            Spacer(modifier = Modifier.width(4.dp))
            Text("Add")
        }
    }
}

@Composable
fun ExpenseHeader(onAddExpense: () -> Unit) {
    Row(
        modifier =
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("Expenses", style = MaterialTheme.typography.titleLarge)
        TextButton(
            onClick = onAddExpense,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
            colors =
            ButtonDefaults.outlinedButtonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.primary,
            ),
        ) {
            Icon(Icons.Default.MoneyOff, contentDescription = "Add Expense")
            Spacer(modifier = Modifier.width(4.dp))
            Text("Add")
        }
    }
}

@Composable
fun SortFilterBar(
    selectedSort: ExpensesSortOption,
    selectedExpensePriorities: Set<ExpensePriority>,
    onSortChange: (ExpensesSortOption) -> Unit,
    onFilterChange: (ExpensePriority) -> Unit,
) {
    Column(
        modifier =
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
    ) {
        // Row 1: Sort
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text("Sort by:", style = MaterialTheme.typography.labelLarge)

            SortDropdown(
                selectedSort = selectedSort,
                onSortChange = onSortChange,
            )
        }

        // Row 2: Filter
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text("Filter by:", style = MaterialTheme.typography.labelLarge)
            ExpenseTypeFilterBar(
                selectedTypes = selectedExpensePriorities,
                onPriorityToggle = { onFilterChange(it) },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortDropdown(selectedSort: ExpensesSortOption, onSortChange: (ExpensesSortOption) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val textStyle =
        MaterialTheme.typography.bodySmall.copy(
            textAlign = TextAlign.Center,
        )
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        OutlinedTextField(
            value = selectedSort.label,
            onValueChange = {},
            readOnly = true,
            textStyle = textStyle,
            modifier =
            Modifier
                .menuAnchor()
                .height(48.dp)
                .widthIn(max = 160.dp),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            shape = RoundedCornerShape(12.dp),
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            ExpensesSortOption.entries.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.name) },
                    onClick = {
                        onSortChange(option)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
fun ExpenseTypeFilterBar(
    selectedTypes: Set<ExpensePriority>,
    onPriorityToggle: (ExpensePriority) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier =
        modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(ExpensePriority.entries) { priority ->
            FilterChip(
                selected = selectedTypes.contains(priority),
                onClick = { onPriorityToggle(priority) },
                label = { Text(priority.name.lowercase().replaceFirstChar { it.uppercase() }) },
                modifier = Modifier.padding(vertical = 4.dp),
            )
        }
    }
}

@Composable
fun ExpensePieChart(data: List<Pair<String, Float>>, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    val pieColors =
        listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.secondary,
            MaterialTheme.colorScheme.tertiary,
            MaterialTheme.colorScheme.error,
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.outline,
        ).map { it.toArgb() }
    val labelColor = MaterialTheme.colorScheme.onBackground.toArgb()
    val holeColor = MaterialTheme.colorScheme.background.toArgb()

    AndroidView(
        modifier =
        modifier
            .height(300.dp)
            .fillMaxWidth(),
        factory = {
            PieChart(context).apply {
                description.isEnabled = false
                isDrawHoleEnabled = true
                setUsePercentValues(true)
                setEntryLabelTextSize(12f)
                setEntryLabelColor(labelColor)
                centerText = "Expenses"
                setCenterTextSize(18f)
                legend.isEnabled = true
                legend.textColor = labelColor
                setEntryLabelColor(labelColor)
                setHoleColor(holeColor)
            }
        },
        update = { chart ->
            val entries =
                data.map { (label, value) ->
                    PieEntry(value, label)
                }

            val dataSet =
                PieDataSet(entries, "").apply {
                    colors = pieColors
                    sliceSpace = 3f
                    selectionShift = 5f
                }

            chart.data =
                PieData(dataSet).apply {
                    setDrawValues(true)
                    setValueTextSize(12f)
                    setValueTextColor(labelColor)
                }

            chart.invalidate()
        },
    )
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
    onEdit: () -> Unit,
    onClick: () -> Unit,
) {
    var itemHeight by remember { mutableIntStateOf(0) }
    val dismissState =
        rememberSwipeToDismissBoxState(
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
            },
        )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val direction = dismissState.dismissDirection
            val isStartToEnd = direction == SwipeToDismissBoxValue.StartToEnd

            val backgroundColor =
                when (direction) {
                    SwipeToDismissBoxValue.StartToEnd -> Color(0xFFB9F6CA) // light green
                    SwipeToDismissBoxValue.EndToStart -> Color(0xFFFFCDD2) // light red
                    else -> Color.Transparent
                }

            val icon =
                when (direction) {
                    SwipeToDismissBoxValue.StartToEnd -> Icons.Default.Edit
                    SwipeToDismissBoxValue.EndToStart -> Icons.Default.Delete
                    else -> null
                }

            val label =
                when (direction) {
                    SwipeToDismissBoxValue.StartToEnd -> "Edit"
                    SwipeToDismissBoxValue.EndToStart -> "Delete"
                    else -> ""
                }

            if (icon != null) {
                Box(
                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(with(LocalDensity.current) { itemHeight.toDp() })
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(backgroundColor),
                    contentAlignment = if (isStartToEnd) Alignment.CenterStart else Alignment.CenterEnd,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier =
                        Modifier
                            .padding(horizontal = 20.dp),
                    ) {
                        Icon(icon, contentDescription = label, tint = Color.Black)
                        Text(label, color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        content = {
            Card(
                modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .onGloballyPositioned { coordinates ->
                        itemHeight = coordinates.size.height
                    },
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(2.dp),
            ) {
                ListItem(
                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(color)
                        .clickable { onClick() },
                    leadingContent = {
                        Icon(
                            icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    },
                    headlineContent = { Text(title, fontWeight = FontWeight.SemiBold) },
                    supportingContent = { Text(subtitle) },
                    trailingContent = {
                        Text(
                            formatCurrency(amount),
                            fontWeight = FontWeight.Medium,
                            color = Color.DarkGray,
                        )
                    },
                )
            }
        },
    )
}

val customGreen: Color
    @Composable
    get() = if (isSystemInDarkTheme()) Green80 else Green40

@Preview(showBackground = true)
@Composable
fun BudgetScreenPreview() {
    MaterialTheme {
        BudgetScreen(
            viewModel =
            BudgetViewModel(
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
fun BudgetScreenPreviewDark() {
    MyBudgetTheme {
        BudgetScreen(
            viewModel =
            BudgetViewModel(
                BudgetRepositoryImpl(
                    MockExpenseDao(),
                    MockIncomeDao(),
                ),
            ),
            navController = rememberNavController(),
        )
    }
}
