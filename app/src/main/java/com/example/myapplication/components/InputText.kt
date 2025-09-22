package com.example.myapplication.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun InputTextField(
    value: String,
    label: String,
    supportingText: String,
    onValueChange: (String) -> Unit,
    onValidate: (String) -> Boolean,
    iconDescription: String,
    icon: ImageVector? = null,
    isPassword: Boolean = false,
    enabled: Boolean = true
) {
    var isError by rememberSaveable { mutableStateOf(false) }
    var passwordVisible by rememberSaveable { mutableStateOf(false) } // 👈 Nuevo estado

    OutlinedTextField(
        value = value,
        onValueChange = {
            onValueChange(it)
            isError = onValidate(it)
        },
        isError = isError,
        enabled = enabled,
        supportingText = {
            if (isError) {
                Text(text = supportingText)
            }
        },
        label = {
            Text(label)
        },
        leadingIcon = {
            if (icon != null) {
                Icon(imageVector = icon, contentDescription = iconDescription)
            }
        },
        // 👇 Ojo solo aparece si es un password
        trailingIcon = {
            if (isPassword && value.isNotEmpty()) {
                val image = if (passwordVisible) {
                    Icons.Filled.Visibility
                } else {
                    Icons.Filled.VisibilityOff
                }
                val description = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = description)
                }
            }
        },
        shape = RoundedCornerShape(10.dp),
        visualTransformation = if (isPassword && !passwordVisible) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}
