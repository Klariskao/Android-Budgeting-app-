package com.example.mybudget.ui.screens

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mybudget.data.local.ExpenseDao
import com.example.mybudget.data.local.IncomeDao
import com.example.mybudget.data.local.MockExpenseDao
import com.example.mybudget.data.local.MockIncomeDao
import com.example.mybudget.data.local.MockSettingsDataStore
import com.example.mybudget.data.local.SettingsDataStore
import com.example.mybudget.data.local.SettingsDataStoreImpl
import com.example.mybudget.data.model.Budget
import com.example.mybudget.data.util.JsonExporter.exportBudgetToDownloads
import com.example.mybudget.data.util.JsonHelper.deserializeFromJson
import com.example.mybudget.ui.SharedBudgetViewModel
import com.example.mybudget.ui.theme.MyBudgetTheme
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.getKoin

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun SettingsScreen() {
    val settingsDataStore: SettingsDataStoreImpl = getKoin().get()

    val isDarkTheme by settingsDataStore.darkThemeFlow.collectAsState(initial = false)
    val selectedCurrency by settingsDataStore.currencyFlow.collectAsState(initial = "USD")

    val sharedBudgetViewModel: SharedBudgetViewModel = koinViewModel()
    val budget by sharedBudgetViewModel.budget.collectAsState()

    val expenseDao: ExpenseDao = getKoin().get()
    val incomeDao: IncomeDao = getKoin().get()

    SettingsScreenContent(
        settingsDataStore,
        isDarkTheme,
        selectedCurrency,
        budget,
        expenseDao,
        incomeDao,
    )
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun SettingsScreenContent(
    settingsDataStore: SettingsDataStore,
    isDarkTheme: Boolean,
    selectedCurrency: String,
    budget: Budget,
    expenseDao: ExpenseDao,
    incomeDao: IncomeDao,
) {
    val scope = rememberCoroutineScope()
    var notificationsEnabled by remember { mutableStateOf(true) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .padding(top = 16.dp),
    ) {
        Text(
            "Settings",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary,
        )

        Spacer(Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Dark Theme", Modifier.weight(1f), color = MaterialTheme.colorScheme.primary)
            Switch(
                checked = isDarkTheme,
                onCheckedChange = {
                    scope.launch { settingsDataStore.setDarkTheme(it) }
                },
            )
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Notifications", Modifier.weight(1f), color = MaterialTheme.colorScheme.primary)
            Switch(
                checked = notificationsEnabled,
                onCheckedChange = { notificationsEnabled = it },
            )
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Currency", Modifier.weight(1f), color = MaterialTheme.colorScheme.primary)
            DropdownMenuCurrencySelector(
                selected = selectedCurrency,
                onCurrencySelected = {
                    scope.launch { settingsDataStore.setCurrency(it) }
                },
            )
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

        Spacer(Modifier.height(24.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
        ) {
            SettingsButton(budget = budget)
            ImportButton(expenseDao = expenseDao, incomeDao = incomeDao)
        }
    }
}

@Composable
fun DropdownMenuCurrencySelector(selected: String, onCurrencySelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val currencies = listOf("USD", "EUR", "GBP", "JPY")

    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Text(selected)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            currencies.forEach {
                DropdownMenuItem(
                    text = { Text(it) },
                    onClick = {
                        onCurrencySelected(it)
                        expanded = false
                    },
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun SettingsButton(budget: Budget) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Button(
        onClick = {
            scope.launch {
                try {
                    val file = exportBudgetToDownloads(context, budget)
                    Toast.makeText(context, "Exported to $file", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(context, "Export failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        },
    ) {
        Text("Export Data")
    }
}

@Composable
fun ImportButton(expenseDao: ExpenseDao, incomeDao: IncomeDao) {
    val context: Context = LocalContext.current
    val scope = rememberCoroutineScope()
    var pendingBudget by remember { mutableStateOf<Budget?>(null) }
    var showMergeDialog by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri: Uri? ->
            if (uri != null) {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION,
                )
                try {
                    val inputStream = context.contentResolver.openInputStream(uri)
                    val jsonString = inputStream?.bufferedReader()?.use { it.readText() }
                    if (!jsonString.isNullOrEmpty()) {
                        pendingBudget = deserializeFromJson<Budget>(jsonString)
                        showMergeDialog = true
                    } else {
                        Toast.makeText(context, "File was empty", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Import failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        },
    )

    Button(
        onClick = {
            launcher.launch(arrayOf("*/*"))
        },
    ) {
        Text("Import Data")
    }

    if (showMergeDialog && pendingBudget != null) {
        AlertDialog(
            onDismissRequest = { showMergeDialog = false },
            title = { Text("Import Options") },
            text = {
                Text(
                    "Would you like to replace your current data or merge it with the imported data?",
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showMergeDialog = false
                        scope.launch {
                            // Replace logic
                            incomeDao.clearAll()
                            expenseDao.clearAll()
                            incomeDao.insertAll(pendingBudget!!.incomes)
                            expenseDao.insertAll(pendingBudget!!.expenses)
                            Toast
                                .makeText(context, "Data replaced successfully", Toast.LENGTH_SHORT)
                                .show()
                        }
                    },
                ) {
                    Text("Replace")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showMergeDialog = false
                        scope.launch {
                            // Merge logic
                            incomeDao.insertAll(pendingBudget!!.incomes)
                            expenseDao.insertAll(pendingBudget!!.expenses)
                            Toast
                                .makeText(context, "Data merged successfully", Toast.LENGTH_SHORT)
                                .show()
                        }
                    },
                ) {
                    Text("Merge")
                }
            },
        )
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    MaterialTheme {
        SettingsScreenContent(
            settingsDataStore = MockSettingsDataStore(),
            isDarkTheme = false,
            selectedCurrency = "USD",
            budget = Budget(),
            expenseDao = MockExpenseDao(),
            incomeDao = MockIncomeDao(),
        )
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SettingsScreenPreviewDark() {
    MyBudgetTheme(isDarkTheme = true) {
        SettingsScreenContent(
            settingsDataStore = MockSettingsDataStore(),
            isDarkTheme = false,
            selectedCurrency = "USD",
            budget = Budget(),
            expenseDao = MockExpenseDao(),
            incomeDao = MockIncomeDao(),
        )
    }
}
