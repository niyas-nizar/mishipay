package com.niyas.mishipay.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.niyas.mishipay.screens.BarcodeViewModel
import com.niyas.mishipay.screens.CartScreen
import com.niyas.mishipay.screens.ScannerScreen

@Composable
fun BarcodeScannerNavigation(viewModel: BarcodeViewModel, navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = BarcodeScannerScreens.SCANNER_SCREEN.name
    ) {
        composable(route = BarcodeScannerScreens.SCANNER_SCREEN.name, exitTransition = {
            return@composable slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Start, tween(0)
            )
        }, popEnterTransition = {
            return@composable slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.End, tween(0)
            )
        }) {
            ScannerScreen(viewModel, navController)
        }
        composable(route = BarcodeScannerScreens.CART_SCREEN.name, exitTransition = {
            return@composable slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.End, tween(0)
            )
        }, popEnterTransition = {
            return@composable slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.End, tween(0)
            )
        }) {
            CartScreen(viewModel, navController)
        }

    }


}