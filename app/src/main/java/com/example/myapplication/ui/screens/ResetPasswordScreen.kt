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

@Composable
fun ResetPasswordScreenForm(
    onBack: () -> Unit = {},
    onSendCode: (String) -> Unit = {},
    onAlreadyHaveCode: () -> Unit = {}
) {
    var contact by remember { mutableStateOf("") } // email o phone
    val isInvalidContact = contact.isBlank() || !isValidEmailOrPhone(contact)
    var isLoading by rememberSaveable { mutableStateOf(false) }
    var showInformativeNote by rememberSaveable { mutableStateOf(false) }

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
                            isLoading = true
                            onSendCode(contact.trim())
                            showInformativeNote = true
                            isLoading = false
                        },
                        enabled = !isInvalidContact,
                        color = colorResource(R.color.green),
                        icon = Icons.Outlined.Send,
                        text = stringResource(R.string.reset_send_code),
                        contentColor = colorResource(R.color.white),
                        isLoading = isLoading,
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
                    if (showInformativeNote) {
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
                                    text = stringResource(R.string.reset_info_note),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colorResource(R.color.dark_grey)
                                )
                            }
                        }
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



