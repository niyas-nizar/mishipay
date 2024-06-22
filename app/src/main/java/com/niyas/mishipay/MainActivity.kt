package com.niyas.mishipay

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.niyas.mishipay.navigation.BarcodeScannerNavigation
import com.niyas.mishipay.screens.BarcodeViewModel
import com.niyas.mishipay.ui.theme.MishiPayTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val viewModel by viewModels<BarcodeViewModel>()
            val navController = rememberNavController()

            MishiPayTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BarcodeScannerNavigation(viewModel, navController)
                }
            }
        }
    }

}