package com.example.mybudget.ui.dialogs

import android.content.res.Configuration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.mybudget.ui.theme.MyBudgetTheme

@Composable
fun DeleteConfirmationDialog(name: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = {},
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = { Text("Confirm Deletion") },
        text = { Text("Are you sure you want to delete $name?") },
    )
}

@Preview(showBackground = true)
@Composable
fun DeleteConfirmationDialogPreview() {
    MaterialTheme {
        DeleteConfirmationDialog(
            name = "Job",
            onConfirm = {},
            onDismiss = {},
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun DeleteConfirmationDialogPreviewDark() {
    MyBudgetTheme(isDarkTheme = true) {
        DeleteConfirmationDialog(
            name = "Job",
            onConfirm = {},
            onDismiss = {},
        )
    }
}
