package com.example.myapplication.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.example.myapplication.model.ReviewStatus
import com.example.myapplication.ui.theme.GreenCompany
import com.example.myapplication.ui.theme.RedCompany
import com.example.myapplication.R

@Composable
fun StatusFilterChips(
    selectedTab: ReviewStatus?,
    onSelect: (ReviewStatus?) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth()
    ) {
        FilterChip(
            selected = selectedTab == null,
            onClick = { onSelect(null) },
            label = { Text(stringResource(R.string.chip_all)) },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primary,
                selectedLabelColor = Color.White
            )
        )

        FilterChip(
            selected = selectedTab == ReviewStatus.APPROVED,
            onClick = { onSelect(ReviewStatus.APPROVED) },
            label = { Text(stringResource(R.string.chip_approved)) },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = GreenCompany,
                selectedLabelColor = Color.White
            )
        )

        FilterChip(
            selected = selectedTab == ReviewStatus.REJECTED,
            onClick = { onSelect(ReviewStatus.REJECTED) },
            label = { Text(stringResource(R.string.chip_rejected)) },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = RedCompany,
                selectedLabelColor = Color.White
            )
        )
    }
}
