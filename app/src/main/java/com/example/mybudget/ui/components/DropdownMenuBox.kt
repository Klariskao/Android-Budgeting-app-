package com.example.mybudget.ui.components

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.mybudget.data.model.ExpenseFrequency
import com.example.mybudget.data.model.ExpensePriority
import com.example.mybudget.data.model.IncomeFrequency

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> DropdownMenuBox(
    label: String,
    options: List<T>,
    selected: T,
    onSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier,
    ) {
        OutlinedTextField(
            readOnly = true,
            value = selected.toString(),
            onValueChange = {},
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor(),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.toString()) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewIncomeTypeDropdown() {
    var selected by remember { mutableStateOf(IncomeFrequency.MONTHLY) }
    DropdownMenuBox(
        label = "Income Type",
        options = IncomeFrequency.entries,
        selected = selected,
        onSelected = { selected = it },
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewExpenseTypeDropdown() {
    var selected by remember { mutableStateOf(ExpensePriority.LUXURY) }
    DropdownMenuBox(
        label = "Expense Type",
        options = ExpensePriority.entries.toList(),
        selected = selected,
        onSelected = { selected = it },
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewExpenseFrequencyDropdown() {
    var selected by remember { mutableStateOf(ExpenseFrequency.MONTHLY) }
    DropdownMenuBox(
        label = "Frequency",
        options = ExpenseFrequency.entries,
        selected = selected,
        onSelected = { selected = it },
    )
}
