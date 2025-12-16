package com.example.myapplication.ui.components

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import com.example.myapplication.ui.theme.GreenCompany
import com.example.myapplication.ui.theme.RedCompany
import com.example.myapplication.viewmodel.PlacesViewModel
import kotlinx.coroutines.launch

@Composable
fun PlaceActionButtons(
    placeId: String,
    viewModel: PlacesViewModel,
    onBack: () -> Unit = {}
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 🔹 Ver en mapa
        OutlinedButton(onClick = {
            Toast.makeText(
                context,
                context.getString(R.string.toast_see_on_map),
                Toast.LENGTH_SHORT
            ).show()
        }) {
            Text(stringResource(R.string.btn_view_on_map))
        }

        // 🔹 Autorizar
        Button(
            onClick = {
                viewModel.approvePlace(placeId)
                Toast.makeText(
                    context,
                    context.getString(R.string.toast_approved),
                    Toast.LENGTH_LONG
                ).show()

                coroutineScope.launch {
                    kotlinx.coroutines.delay(1500)
                    onBack()
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = GreenCompany)
        ) {
            Text(stringResource(R.string.btn_approve))
        }

        // 🔹 Rechazar
        Button(
            onClick = {
                viewModel.rejectPlace(placeId)
                Toast.makeText(
                    context,
                    context.getString(R.string.toast_rejected),
                    Toast.LENGTH_LONG
                ).show()

                coroutineScope.launch {
                    kotlinx.coroutines.delay(1500)
                    onBack()
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = RedCompany)
        ) {
            Text(stringResource(R.string.btn_reject))
        }
    }
}
