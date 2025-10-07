package com.example.myapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SlidingPanel(
    modifier: Modifier = Modifier,
    peekHeight: Dp = 160.dp // 🔹 altura visible mínima
) {
    val sheetState = rememberStandardBottomSheetState(
        initialValue = SheetValue.PartiallyExpanded, // empieza medio abierto
        skipHiddenState = true
    )
    val scope = rememberCoroutineScope()

    BottomSheetScaffold(
        scaffoldState = rememberBottomSheetScaffoldState(
            bottomSheetState = sheetState
        ),
        sheetPeekHeight = peekHeight, // 🔹 límite inferior
        sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        sheetDragHandle = {
            Box(
                Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .background(Color.LightGray, RoundedCornerShape(2.dp))

            )
        },
        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Resultados cerca de ti", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text("La Trattoria • 4.8 • 0.6 km")
                Text("Café Aurora • 4.6 • 0.35 km")
                Spacer(Modifier.height(24.dp))
            }
        },
        modifier = modifier
    ) {
        // 🔸 Fondo transparente para ver el mapa detrás
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.Transparent)
        )
    }
}
