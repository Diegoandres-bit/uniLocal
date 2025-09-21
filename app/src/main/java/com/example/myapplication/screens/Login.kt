package com.example.myapplication.screens


import android.util.Patterns
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import com.example.myapplication.components.InputTextField
import com.example.myapplication.components.Button


@Composable
fun LoginForm() {
    var isLoading by rememberSaveable { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var emailval by remember { mutableStateOf(false) }
    var passwordvall by remember { mutableStateOf(false) }

    Column(

        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            color = colorResource(R.color.white)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(R.drawable.usimbol),
                        contentDescription = stringResource(R.string.ULogo),
                        modifier = Modifier.width(40.dp)
                    )
                    Text(
                        stringResource(R.string.uniLocal),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                InputTextField(
                    email,
                    stringResource(R.string.txt_Email),
                    stringResource(R.string.email_error),
                    onValueChange  ={
                        email=it
                    },
                    onValidate = {
                         email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()
                    },

                    "email icon",
                    Icons.Outlined.Email,
                    isPassword = false
                )
                InputTextField(
                    password,
                    stringResource(R.string.txt_passsword),
                    stringResource(R.string.password_error),
                    onValueChange  ={
                        password=it
                        },

                    onValidate = {
                        password.isBlank() || password.length < 8
                    },

                    "password icon",
                    Icons.Outlined.Key,
                    isPassword = true
                )


                val isFormValid = !emailval && !passwordvall

                Button(
                    onClick = {
                        isLoading = true
                        println("Usuario: $email, Contraseña: $password")
                        //isLoading = false

                    },
                    enabled = isFormValid,
                    color = colorResource(R.color.green),
                    text = stringResource(R.string.btn_Inicio_Sesion),
                    contentColor = colorResource(R.color.white),
                    isLoading = isLoading

                )
                HorizontalDivider(
                    thickness = 1.dp,
                    color = colorResource(R.color.grey)
                )

            }
        }

    }
}


