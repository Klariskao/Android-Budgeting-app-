package com.example.mybudget.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mybudget.data.local.MockExpenseDao
import com.example.mybudget.data.local.MockIncomeDao
import com.example.mybudget.data.local.SettingsDataStore
import com.example.mybudget.data.model.Budget
import com.example.mybudget.data.model.Expense
import com.example.mybudget.data.model.ExpenseCategory
import com.example.mybudget.data.model.ExpenseFrequency
import com.example.mybudget.data.model.ExpensePriority
import com.example.mybudget.data.model.Income
import com.example.mybudget.data.model.IncomeFrequency
import com.example.mybudget.repository.BudgetRepositoryImpl
import com.example.mybudget.ui.BudgetViewModel
import com.example.mybudget.ui.dialogs.DeleteConfirmationDialog
import com.example.mybudget.ui.dialogs.EditExpenseDialog
import com.example.mybudget.ui.dialogs.EditIncomeDialog
import com.example.mybudget.ui.helpers.formatCurrency
import com.example.mybudget.ui.helpers.getExpenseOccurrencesInPeriod
import com.example.mybudget.ui.helpers.getIncomeOccurrencesInPeriod
import com.example.mybudget.ui.helpers.toMonthlyAmount
import com.example.mybudget.ui.helpers.toYearlyAmount
import com.example.mybudget.ui.model.BudgetDialogState
import com.example.mybudget.ui.model.BudgetEvent
import com.example.mybudget.ui.model.ExpensesSortOption
import com.example.mybudget.ui.model.ProgressSegment
import com.example.mybudget.ui.navigation.Screen
import com.example.mybudget.ui.theme.Green40
import com.example.mybudget.ui.theme.Green80
import com.example.mybudget.ui.theme.MyBudgetTheme
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import org.koin.compose.getKoin
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@Composable
fun BudgetScreen(viewModel: BudgetViewModel, navController: NavController) {
    val budget by viewModel.budget.collectAsState()
    val dialogState = viewModel.dialogState
    var sortOption by remember { mutableStateOf(ExpensesSortOption.NONE) }
    var selectedExpensePriorities by remember { mutableStateOf(setOf<ExpensePriority>()) }

    val graphColors = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.tertiary,
        MaterialTheme.colorScheme.error,
        MaterialTheme.colorScheme.surfaceVariant,
        MaterialTheme.colorScheme.outline,
    )

    dialogState?.let { state ->
        when (state) {
            is BudgetDialogState.ConfirmDeleteIncome -> DeleteConfirmationDialog(
                name = state.income.name,
                onConfirm = {
                    viewModel.onEvent(BudgetEvent.RemoveIncome(state.income))
                    viewModel.onEvent(BudgetEvent.CloseDialog)
                },
                onDismiss = { viewModel.onEvent(BudgetEvent.CloseDialog) },
            )

            is BudgetDialogState.ConfirmDeleteExpense -> DeleteConfirmationDialog(
                name = state.expense.name,
                onConfirm = {
                    viewModel.onEvent(BudgetEvent.RemoveExpense(state.expense))
                    viewModel.onEvent(BudgetEvent.CloseDialog)
                },
                onDismiss = { viewModel.onEvent(BudgetEvent.CloseDialog) },
            )

            is BudgetDialogState.EditIncome -> EditIncomeDialog(
                income = state.income,
                onSave = {
                    viewModel.onEvent(BudgetEvent.UpdateIncome(it))
                    viewModel.onEvent(BudgetEvent.CloseDialog)
                },
                onDismiss = { viewModel.onEvent(BudgetEvent.CloseDialog) },
            )

            is BudgetDialogState.EditExpense -> EditExpenseDialog(
                expense = state.expense,
                onSave = {
                    viewModel.onEvent(BudgetEvent.UpdateExpense(it))
                    viewModel.onEvent(BudgetEvent.CloseDialog)
                },
                onDismiss = { viewModel.onEvent(BudgetEvent.CloseDialog) },
            )
        }
    }

    Scaffold { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                BudgetSummaryCard(budget, navController)
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
                        subtitle = income.frequency.label,
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
                val totalSpent = filteredExpenses.sumOf { it.amount }
                val grouped = filteredExpenses.groupBy { it.category }

                val segments = grouped.entries.mapIndexed { index, (_, expenses) ->
                    val categoryTotal = expenses.sumOf { it.amount }
                    ProgressSegment(
                        fraction = (categoryTotal / totalSpent).toFloat(),
                        color = graphColors[index % graphColors.size],
                    )
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
                        data = filteredExpenses.map { it.category.label to it.amount.toFloat() },
                        pieColors = graphColors,
                    )
                }
                items(
                    items = filteredExpenses,
                    key = { "expense-${it.id}" },
                ) { expense ->
                    SwipeableIncomeExpenseItem(
                        title = expense.name,
                        amount = expense.amount,
                        subtitle = "${expense.frequency.label}, ${expense.priority.label}, ${expense.category.label}",
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

                item {
                    MultiSegmentLinearProgressBar(
                        segments = segments,
                        filteredExpenses = filteredExpenses,
                        modifier = Modifier.fillMaxWidth(),
                    )

                    BudgetPeriodOverview(incomes = budget.incomes, expenses = filteredExpenses)
                }
            }
        }
    }
}

@Composable
fun BudgetSummaryCard(budget: Budget, navController: NavController, modifier: Modifier = Modifier) {
    val monthlyIncome = budget.incomes.sumOf { it.toMonthlyAmount() }
    val yearlyIncome =
        budget.incomes.sumOf { it.toYearlyAmount(customFrequencyInDays = it.customFrequencyInDays) }

    val monthlyExpenses = budget.expenses.sumOf { it.toMonthlyAmount() }
    val yearlyExpenses =
        budget.expenses.sumOf {
            it.toYearlyAmount(customFrequencyInDays = it.customFrequencyInDays)
        }

    val availableYearlyFunds = yearlyIncome - yearlyExpenses
    val availableMonthlyFunds = monthlyIncome - monthlyExpenses

    val progress = if (yearlyIncome > 0) {
        (yearlyExpenses / yearlyIncome).toFloat().coerceIn(0f, 1f)
    } else {
        0f
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 8.dp, bottom = 16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Total Budget Summary", style = MaterialTheme.typography.titleMedium)
                ExportButton(navController = navController)
            }

            Spacer(Modifier.height(12.dp))

            Text(
                "Yearly Breakdown",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
            )

            SummaryRow("Total Income", yearlyIncome)
            SummaryRow("Total Expenses", yearlyExpenses)
            SummaryRow("Available Funds", availableYearlyFunds)

            Spacer(Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(8.dp)),
                color = when {
                    progress < 0.5f -> customGreen
                    progress == 1f -> MaterialTheme.colorScheme.error
                    else -> ProgressIndicatorDefaults.linearColor
                },
            )

            Spacer(Modifier.height(12.dp))

            Text(
                "Monthly Breakdown",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
            )

            SummaryRow("Average Income", monthlyIncome)
            SummaryRow("Average Expenses", monthlyExpenses)
            SummaryRow("Available Funds", availableMonthlyFunds)
        }
    }
}

@Composable
fun ExportButton(navController: NavController) {
    IconButton(
        onClick = { navController.navigate(Screen.Settings.route) },
    ) {
        Icon(Icons.Default.Settings, contentDescription = "Settings")
    }
}

@Composable
fun SummaryRow(label: String, amount: Double) {
    val settingsDataStore: SettingsDataStore = getKoin().get()
    val currency by settingsDataStore.currencyFlow.collectAsState(initial = "USD")
    Row(
        modifier =
        Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label)
        Text(formatCurrency(amount, currency), fontWeight = FontWeight.Medium)
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

@Composable
fun SortDropdown(selectedSort: ExpensesSortOption, onSortChange: (ExpensesSortOption) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.wrapContentWidth()) {
        OutlinedButton(
            onClick = { expanded = true },
        ) {
            Text(text = selectedSort.label)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            ExpensesSortOption.entries.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.label) },
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
                label = { Text(priority.label) },
                modifier = Modifier.padding(vertical = 4.dp),
            )
        }
    }
}

@Composable
fun ExpensePieChart(
    data: List<Pair<String, Float>>,
    pieColors: List<Color>,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
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
                legend.form = Legend.LegendForm.CIRCLE
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
                    colors = pieColors.map { it.toArgb() }
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
    val settingsDataStore: SettingsDataStore = getKoin().get()
    val currency by settingsDataStore.currencyFlow.collectAsState(initial = "USD")
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
        },
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
                            formatCurrency(amount, currency),
                            fontWeight = FontWeight.Medium,
                            color = Color.DarkGray,
                        )
                    },
                )
            }
        },
    )
}

@Composable
fun MultiSegmentLinearProgressBar(
    segments: List<ProgressSegment>,
    filteredExpenses: List<Expense>,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    height: Dp = 12.dp,
    cornerRadius: Dp = 8.dp,
) {
    Column(
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
    ) {
        Canvas(
            modifier = modifier
                .height(height)
                .clip(RoundedCornerShape(cornerRadius))
                .background(backgroundColor),
        ) {
            var startX = 0f
            val totalWidth = size.width

            segments.forEach { segment ->
                val width = segment.fraction.coerceIn(0f, 1f) * totalWidth
                drawRect(
                    color = segment.color,
                    topLeft = Offset(x = startX, y = 0f),
                    size = Size(width, size.height),
                )
                startX += width
            }
        }

        // Legend for the progress bar
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            segments.forEachIndexed { index, segment ->
                val category = filteredExpenses.groupBy { it.category }.keys.elementAt(index)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(segment.color, CircleShape),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = category.label,
                        fontSize = 10.sp,
                    )
                }
            }
        }
    }
}

@Composable
fun BudgetPeriodOverview(incomes: List<Income>, expenses: List<Expense>) {
    val currentMonth = YearMonth.now()
    val months = remember {
        (0..11).map { currentMonth.plusMonths(it.toLong()) }
    }
    var selectedMonth by remember { mutableStateOf<YearMonth?>(currentMonth) }
    var expanded by remember { mutableStateOf(false) }

    val dateFormatter = remember { DateTimeFormatter.ofPattern("MMMM yyyy") }

    val totalExpenses = expenses.sumOf { expense ->
        getExpenseOccurrencesInPeriod(expense, selectedMonth).size * expense.amount
    }

    val totalIncome = incomes.sumOf { income ->
        getIncomeOccurrencesInPeriod(income, selectedMonth).size * income.amount
    }
    val availableFunds = totalIncome - totalExpenses

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Budget Overview", style = MaterialTheme.typography.titleMedium)

            Box {
                OutlinedButton(
                    onClick = { expanded = true },
                ) {
                    Text(
                        text = selectedMonth?.format(dateFormatter) ?: "Full Year",
                    )
                }

                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    months.forEach { month ->
                        DropdownMenuItem(
                            text = { Text(month.format(dateFormatter)) },
                            onClick = {
                                selectedMonth = month
                                expanded = false
                            },
                        )
                    }
                    Divider(color = MaterialTheme.colorScheme.surfaceVariant)
                    DropdownMenuItem(
                        text = { Text("Full Year") },
                        onClick = {
                            selectedMonth = null
                            expanded = false
                        },
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                SummaryRow("Total Income", totalIncome)
                SummaryRow("Total Expenses", totalExpenses)
                SummaryRow("Available Funds", availableFunds)

                Spacer(Modifier.height(8.dp))

                val progress = if (totalIncome > 0.0) {
                    (totalExpenses / totalIncome).toFloat()
                        .coerceIn(0f, 1f)
                } else {
                    0f
                }
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = when {
                        progress < 0.5f -> customGreen
                        progress == 1f -> MaterialTheme.colorScheme.error
                        else -> ProgressIndicatorDefaults.linearColor
                    },
                )
            }
        }
    }
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
    MyBudgetTheme(isDarkTheme = true) {
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

val mockData = listOf(
    "Rent" to 40f,
    "Food" to 30f,
    "Transport" to 20f,
    "Entertainment" to 10f,
)

val mockColors = listOf(
    Color(0xFF4CAF50),
    Color(0xFFFFC107),
    Color(0xFF2196F3),
    Color(0xFFE91E63),
)

@Preview(showBackground = true)
@Composable
fun ExpensePieChartPreview() {
    MaterialTheme {
        ExpensePieChart(
            data = mockData,
            pieColors = mockColors,
            modifier = Modifier
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.background),
        )
    }
}

val mockExpenses = listOf(
    Expense(
        name = "Rent",
        amount = 1000.0,
        priority = ExpensePriority.REQUIRED,
        frequency = ExpenseFrequency.MONTHLY,
        category = ExpenseCategory.HOME,
    ),
    Expense(
        name = "Food",
        amount = 300.0,
        priority = ExpensePriority.REQUIRED,
        frequency = ExpenseFrequency.WEEKLY,
        category = ExpenseCategory.FOOD,
    ),
    Expense(
        name = "Transport",
        amount = 50.0,
        priority = ExpensePriority.LUXURY,
        frequency = ExpenseFrequency.ONE_TIME,
        category = ExpenseCategory.ENTERTAINMENT,
    ),
)

val total = mockExpenses.sumOf { it.amount }

val grouped = mockExpenses.groupBy { it.category }

val segments = grouped.entries.mapIndexed { index, (_, expenses) ->
    val categoryTotal = expenses.sumOf { it.amount }
    ProgressSegment(
        fraction = (categoryTotal / total).toFloat(),
        color = mockColors[index % mockColors.size],
    )
}

@Preview(showBackground = true)
@Composable
fun MultiSegmentLinearProgressBarPreview() {
    MaterialTheme {
        MultiSegmentLinearProgressBar(
            segments = segments,
            filteredExpenses = mockExpenses,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BudgetPeriodOverviewPreview() {
    MaterialTheme {
        BudgetPeriodOverview(
            incomes = listOf(
                Income(
                    name = "Job",
                    amount = 2500.0,
                    frequency = IncomeFrequency.MONTHLY,
                ),
            ),
            expenses = mockExpenses,
        )
    }
}
