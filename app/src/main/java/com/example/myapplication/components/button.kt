package com.example.myapplication.components

import android.R.attr.shape
import android.graphics.Color
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.res.colorResource

@Composable
fun button(

    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    color: androidx.compose.ui.graphics.Color,
    text:String,
    contentColor: androidx.compose.ui.graphics.Color
){
    Button(
        onClick = {
            onClick()
        },
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .width(200.dp)
            .height(48.dp)
        ,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = color,
            contentColor = contentColor ,
            disabledContainerColor = colorResource(R.color.grey),
            disabledContentColor = colorResource(R.color.dark_grey)
        )
    ){
        Text(text)
    }
}