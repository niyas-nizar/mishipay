package com.niyas.mishipay.screens.composables

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ReusableDoubleButtons(
    negativeButtonAction: () -> Unit,
    positiveButtonAction: () -> Unit,
    negativeButtonText: String,
    positiveButtonText: String
) {
    Column {
        OutlinedButton(
            shape = RoundedCornerShape(5.dp),
            onClick = negativeButtonAction, modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Text(text = negativeButtonText, color = Color.Black)
        }
        Button(
            onClick = positiveButtonAction,
            shape = RoundedCornerShape(5.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            Text(text = positiveButtonText, color = Color.White)
        }
    }
}

@Preview(
    showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO, showSystemUi = true
)
@Composable
fun CartButtonsPreview() {
    ReusableDoubleButtons(
        negativeButtonAction = {},
        positiveButtonAction = {},
        negativeButtonText = "Negative Button",
        positiveButtonText = "Positive Button"
    )
}