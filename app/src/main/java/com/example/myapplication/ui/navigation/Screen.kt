package com.example.myapplication.ui.navigation

sealed class Screen(val route: String) {
    object RoleSelection : Screen("role_selection")
    object BuyerHome : Screen("buyer_home")
    object SellerDashboard : Screen("seller_dashboard")
    object DriverDashboard : Screen("driver_dashboard")
}
