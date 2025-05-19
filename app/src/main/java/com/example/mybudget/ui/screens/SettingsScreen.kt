package com.example.mybudget.ui.screens

import android.content.res.Configuration
import android.widget.Toast
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
import androidx.compose.material.Divider
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mybudget.ui.theme.MyBudgetTheme

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    var isDarkTheme by remember { mutableStateOf(false) }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var selectedCurrency by remember { mutableStateOf("USD") }

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
                onCheckedChange = { isDarkTheme = it },
            )
        }

        Divider(color = MaterialTheme.colorScheme.surfaceVariant)

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

        Divider(color = MaterialTheme.colorScheme.surfaceVariant)

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Currency", Modifier.weight(1f), color = MaterialTheme.colorScheme.primary)
            DropdownMenuCurrencySelector(
                selected = selectedCurrency,
                onCurrencySelected = { selectedCurrency = it },
            )
        }

        Divider(color = MaterialTheme.colorScheme.surfaceVariant)

        Spacer(Modifier.height(24.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Button(
                onClick = {
                    // TODO: Perform export
                    Toast.makeText(context, "Export started", Toast.LENGTH_SHORT).show()
                },
            ) {
                Text("Export Data")
            }

            Button(
                onClick = {
                    // TODO: Perform import
                    Toast.makeText(context, "Import started", Toast.LENGTH_SHORT).show()
                },
            ) {
                Text("Import Data")
            }
        }
    }
}

@Composable
fun DropdownMenuCurrencySelector(selected: String, onCurrencySelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val currencies = listOf("USD", "EUR", "GBP", "JPY", "Local")

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

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    MaterialTheme {
        SettingsScreen()
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SettingsScreenPreviewDark() {
    MyBudgetTheme {
        SettingsScreen()
    }
}
