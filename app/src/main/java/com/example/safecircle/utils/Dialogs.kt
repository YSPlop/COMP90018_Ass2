package com.example.safecircle.utils

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun ErrorDialog(message: String, onDismiss: () -> Unit) {
  AlertDialog(
      onDismissRequest = onDismiss,
      dismissButton = { TextButton(onClick = onDismiss) {Text("Dismiss")} },
      confirmButton = {},
      icon = {Icon(Icons.Filled.Warning, null)},
      text = { Text(message)},
      title = {Text("Oops...")},
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 0.dp).fillMaxWidth(0.8F)
  )

}

@Composable
@Preview()
fun ErrorDialogPreview() {
    ErrorDialog("Don't panic, the error is on us.") {}
}
