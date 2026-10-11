package com.harvestpay.app.ui.screens

import com.harvestpay.app.localization.AppText
import com.harvestpay.app.R
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
import androidx.compose.runtime.saveable.rememberSaveable
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
    var currentPassword by rememberSaveable { mutableStateOf("") }
    var newPassword by rememberSaveable { mutableStateOf("") }
    var confirmation by rememberSaveable { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text(
                AppText.text(R.string.text_change_password),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 14.dp),
            )
        }
        item {
            Text(
                AppText.text(R.string.text_enter_your_current_password_then_choose_a_new_password),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        item {
            PasswordField(
                value = currentPassword,
                onValueChange = { currentPassword = it },
                label = AppText.text(R.string.text_current_password),
                imeAction = ImeAction.Next,
            )
        }
        item {
            PasswordField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = AppText.text(R.string.text_new_password),
                imeAction = ImeAction.Next,
            )
        }
        item {
            PasswordField(
                value = confirmation,
                onValueChange = { confirmation = it },
                label = AppText.text(R.string.text_confirm_new_password),
                imeAction = ImeAction.Done,
                isError = confirmation.isNotEmpty() && confirmation != newPassword,
                supportingText = if (confirmation.isNotEmpty() && confirmation != newPassword) {
                    AppText.text(R.string.text_passwords_do_not_match)
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
                Text(AppText.text(R.string.text_change_password))
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
                    contentDescription = if (visible) AppText.text(R.string.text_hide_password) else AppText.text(R.string.text_show_password),
                )
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = imeAction),
        isError = isError,
        supportingText = supportingText?.let { message -> { Text(message) } },
        modifier = Modifier.fillMaxWidth(),
    )
}
