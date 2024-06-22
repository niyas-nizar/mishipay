package com.niyas.mishipay.screens.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ReusableDoubleButtons(negativeButtonAction: () -> Unit, positiveButtonAction: () -> Unit, negativeButtonText: String, positiveButtonText: String) {
    Button(
        onClick = negativeButtonAction, modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text = negativeButtonText)
    }
    Button(
        onClick = positiveButtonAction, modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text = positiveButtonText)
    }
}