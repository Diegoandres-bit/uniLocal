package com.example.myapplication.ui.screens

import android.util.Patterns
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import com.example.myapplication.ui.components.Button
import com.example.myapplication.ui.components.InputTextField
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.viewmodel.UsersViewModel

@Composable
fun ResetPasswordScreenForm(
    onBack: () -> Unit = {},
    onSendCode: (String) -> Unit = {},
    onAlreadyHaveCode: () -> Unit = {},
    usersViewModel: UsersViewModel = viewModel()
) {
    var contact by remember { mutableStateOf("") } // email o phone
    val isInvalidContact = contact.isBlank() || !isValidEmailOrPhone(contact)
    var showInformativeNote by rememberSaveable { mutableStateOf(false) }
    val uiState by usersViewModel.uiState.collectAsState()
    val recoveryCode = uiState.recoveryCode

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .imePadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 16.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.Outlined.ArrowBack,
                    contentDescription = stringResource(R.string.cd_back)
                )
            }
            Spacer(Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.reset_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )
        }

        // el card central
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.TopCenter
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                shape = RoundedCornerShape(16.dp),
                color = colorResource(R.color.white),
                shadowElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = stringResource(R.string.reset_h1),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.reset_subtitle),
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorResource(R.color.dark_grey)
                    )

                    InputTextField(
                        value = contact,
                        label = stringResource(R.string.reset_contact_label),
                        supportingText = stringResource(R.string.reset_contact_supporting),
                        onValueChange = { contact = it },
                        onValidate = { v -> v.isBlank() || !isValidEmailOrPhone(v) },
                        iconDescription = stringResource(R.string.cd_contact_icon),
                        icon = Icons.Outlined.Person,
                        isPassword = false
                    )

                    // Enviar código
                    Button(
                        onClick = {
                            val sanitized = contact.trim()
                            usersViewModel.requestPasswordReset(sanitized)
                            showInformativeNote = true
                            onSendCode(sanitized)
                        },
                        enabled = !isInvalidContact,
                        color = colorResource(R.color.green),
                        icon = Icons.Outlined.Send,
                        text = stringResource(R.string.reset_send_code),
                        contentColor = colorResource(R.color.white),
                        isLoading = uiState.isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    )

                    // Ya tengo un código
                    Button(
                        onClick = onAlreadyHaveCode,
                        enabled = true,
                        color = colorResource(R.color.green),
                        icon = Icons.Outlined.Lock,
                        text = stringResource(R.string.reset_have_code),
                        contentColor = colorResource(R.color.white),
                        isLoading = false,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    )

                    // Nota informativa (dinámica)
                    val infoLabel = uiState.infoMessage
                    if (showInformativeNote && (infoLabel != null || uiState.error != null)) {
                        Spacer(Modifier.height(12.dp))
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = colorResource(R.color.green_50),
                            tonalElevation = 0.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Info,
                                    contentDescription = stringResource(R.string.cd_info_icon),
                                    tint = colorResource(R.color.green)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = infoLabel ?: uiState.error ?: stringResource(R.string.reset_info_note),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colorResource(R.color.dark_grey)
                                )
                            }
                        }
                    }
                    recoveryCode?.let { code ->
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.reset_code_label, code),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = colorResource(R.color.green)
                        )
                    }
                    uiState.error?.let {
                        Spacer(Modifier.height(8.dp))
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

private fun isValidEmailOrPhone(input: String): Boolean {
    val trimmed = input.trim()
    val isEmail = Patterns.EMAIL_ADDRESS.matcher(trimmed).matches()
    val isPhone = Patterns.PHONE.matcher(trimmed).matches()
    return isEmail || isPhone
}
