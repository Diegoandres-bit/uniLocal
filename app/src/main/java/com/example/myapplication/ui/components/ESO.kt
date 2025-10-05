package com.example.myapplication.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope

import kotlinx.coroutines.launch
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetExample() {
    val scope = rememberCoroutineScope()
    val sheetState = rememberStandardBottomSheetState(
        initialValue = SheetValue.PartiallyExpanded
    )

    val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = sheetState)

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 80.dp,
        sheetContent = {
            Text("Contenido del panel deslizante")
        }
    ) {
        Button(onClick = {
            scope.launch {
                if (sheetState.currentValue == SheetValue.PartiallyExpanded) {
                    sheetState.expand()
                } else {
                    sheetState.partialExpand()
                }
            }
        }) {
            Text("Deslizar panel")
        }
    }
}
