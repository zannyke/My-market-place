package com.example.myapplication.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.role_selection.RoleSelectionScreen
import com.example.myapplication.ui.buyer.home.BuyerHomeScreen
import com.example.myapplication.ui.seller.dashboard.SellerDashboardScreen
import com.example.myapplication.ui.driver.dashboard.DriverDashboardScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.RoleSelection.route) {
        composable(Screen.RoleSelection.route) {
            RoleSelectionScreen(
                onRoleSelected = { role ->
                    when (role) {
                        "Buyer" -> navController.navigate(Screen.BuyerHome.route)
                        "Seller" -> navController.navigate(Screen.SellerDashboard.route)
                        "Driver" -> navController.navigate(Screen.DriverDashboard.route)
                    }
                }
            )
        }
        composable(Screen.BuyerHome.route) {
            BuyerHomeScreen()
        }
        composable(Screen.SellerDashboard.route) {
            SellerDashboardScreen()
        }
        composable(Screen.DriverDashboard.route) {
            DriverDashboardScreen()
        }
    }
}
