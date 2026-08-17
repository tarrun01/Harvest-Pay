package com.harvestpay.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun ChangePasswordScreen(
    onChangePassword: (currentPassword: String, newPassword: String, confirmation: String) -> Unit,
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmation by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text(
                "Change password",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 14.dp),
            )
        }
        item {
            Text(
                "Enter your current password, then choose a new password with at least 6 characters.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        item {
            PasswordField(
                value = currentPassword,
                onValueChange = { currentPassword = it },
                label = "Current password",
                imeAction = ImeAction.Next,
            )
        }
        item {
            PasswordField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = "New password",
                imeAction = ImeAction.Next,
            )
        }
        item {
            PasswordField(
                value = confirmation,
                onValueChange = { confirmation = it },
                label = "Confirm new password",
                imeAction = ImeAction.Done,
                isError = confirmation.isNotEmpty() && confirmation != newPassword,
                supportingText = if (confirmation.isNotEmpty() && confirmation != newPassword) {
                    "Passwords do not match."
                } else {
                    null
                },
            )
        }
        item {
            Button(
                onClick = { onChangePassword(currentPassword, newPassword, confirmation) },
                enabled = currentPassword.isNotBlank() && newPassword.isNotBlank() && confirmation.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(52.dp),
            ) {
                Text("Change password")
            }
        }
        item { Spacer(Modifier.height(42.dp)) }
    }
}

@Composable
private fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    imeAction: ImeAction,
    isError: Boolean = false,
    supportingText: String? = null,
) {
    var visible by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = { visible = !visible }) {
                Icon(
                    if (visible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                    contentDescription = if (visible) "Hide password" else "Show password",
                )
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = imeAction),
        isError = isError,
        supportingText = supportingText?.let { message -> { Text(message) } },
        modifier = Modifier.fillMaxWidth(),
    )
}
