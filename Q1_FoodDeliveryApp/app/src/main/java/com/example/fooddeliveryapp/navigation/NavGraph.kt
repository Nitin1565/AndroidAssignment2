package com.example.fooddeliveryapp.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.fooddeliveryapp.ui.components.CartSummaryBar
import com.example.fooddeliveryapp.ui.components.FoodBottomNavigation
import com.example.fooddeliveryapp.ui.screens.*
import com.example.fooddeliveryapp.viewmodel.FoodDeliveryViewModel
import kotlinx.coroutines.launch

@Composable
fun FoodDeliveryApp() {
    val navController = rememberNavController()
    val viewModel: FoodDeliveryViewModel = viewModel()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val currentBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry.value?.destination?.route ?: "Home"

    val showBottomBar = currentRoute in listOf("Home", "Search", "Orders", "Profile")
    val itemCount = viewModel.getCartItemsCount()
    val totalPrice = viewModel.getCartTotal()

    val showSnackbar = { message: String ->
        coroutineScope.launch {
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            if (showBottomBar) {
                // If there are items in the cart and we're on Home or Search, show the Cart Summary above bottom nav
                androidx.compose.foundation.layout.Column {
                    if ((currentRoute == "Home" || currentRoute == "Search") && itemCount > 0) {
                        CartSummaryBar(
                            itemCount = itemCount,
                            totalPrice = totalPrice,
                            onViewCart = { navController.navigate("Cart") }
                        )
                    }
                    FoodBottomNavigation(
                        currentRoute = currentRoute,
                        onNavigate = { route ->
                            if (currentRoute != route) {
                                navController.navigate(route) {
                                    popUpTo("Home") { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "Home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("Home") {
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToSearch = { navController.navigate("Search") },
                    onNavigateToProfile = { navController.navigate("Profile") },
                    onNavigateToRestaurant = { id -> navController.navigate("Restaurant/$id") },
                    showSnackbar = { showSnackbar(it) }
                )
            }
            composable("Search") {
                SearchScreen(
                    viewModel = viewModel,
                    onNavigateToRestaurant = { id -> navController.navigate("Restaurant/$id") },
                    showSnackbar = { showSnackbar(it) }
                )
            }
            composable("Orders") {
                OrdersScreen(viewModel = viewModel)
            }
            composable("Profile") {
                ProfileScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable("Restaurant/{id}") { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id") ?: ""
                RestaurantDetailsScreen(
                    restaurantId = id,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    showSnackbar = { showSnackbar(it) }
                )
            }
            composable("Cart") {
                CartScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onCheckout = { navController.navigate("Checkout") }
                )
            }
            composable("Checkout") {
                CheckoutScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onPlaceOrder = {
                        viewModel.placeOrder()
                        navController.navigate("OrderSuccess") {
                            popUpTo("Home") { inclusive = false }
                        }
                    }
                )
            }
            composable("OrderSuccess") {
                OrderSuccessScreen(
                    onBackToHome = {
                        navController.navigate("Home") {
                            popUpTo("Home") { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
