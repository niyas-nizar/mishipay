package com.niyas.mishipay.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.niyas.mishipay.screens.BarcodeViewModel
import com.niyas.mishipay.screens.ScannerScreen

@Composable
fun BarcodeScannerNavigation(viewModel: BarcodeViewModel, navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = BarcodeScannerScreens.SCANNER_SCREEN.name
    ) {
        composable(route = BarcodeScannerScreens.SCANNER_SCREEN.name) {
            ScannerScreen(viewModel)

        }
    }


}