package com.example.fooddeliveryapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fooddeliveryapp.data.DummyData
import com.example.fooddeliveryapp.viewmodel.FoodDeliveryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: FoodDeliveryViewModel,
    onNavigateToRestaurant: (String) -> Unit,
    showSnackbar: (String) -> Unit
) {
    val query by viewModel.searchQuery.collectAsState()
    val filteredRestaurants = viewModel.getFilteredRestaurants()
    val filteredFoods = viewModel.getFilteredFoods()
    val favoriteRestaurants by viewModel.favoriteRestaurants.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { viewModel.updateSearchQuery(it) },
                placeholder = { Text("Search food or restaurants") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon") },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                modifier = Modifier.weight(1f),
                singleLine = true
            )
        }

        if (filteredRestaurants.isEmpty() && filteredFoods.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.SearchOff, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.Gray)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No food or restaurants found", style = MaterialTheme.typography.titleMedium, color = Color.Gray)
                }
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                if (filteredRestaurants.isNotEmpty()) {
                    item {
                        Text(
                            "Restaurants",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                    items(filteredRestaurants) { restaurant ->
                        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                            RestaurantCard(
                                restaurant = restaurant,
                                isFavorite = favoriteRestaurants.contains(restaurant.id),
                                onToggleFavorite = {
                                    viewModel.toggleFavorite(restaurant.id)
                                    showSnackbar(if (favoriteRestaurants.contains(restaurant.id)) "Removed from favourites" else "Added to favourites")
                                },
                                onClick = { onNavigateToRestaurant(restaurant.id) }
                            )
                        }
                    }
                }
                
                if (filteredFoods.isNotEmpty()) {
                    item {
                        Text(
                            "Food Items",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                    items(filteredFoods) { food ->
                        val quantity = viewModel.cartItems[food.id]?.quantity ?: 0
                        MenuItemCard(
                            food = food,
                            quantity = quantity,
                            onAdd = {
                                viewModel.addToCart(food)
                                showSnackbar("${food.name} added to cart")
                            },
                            onIncrease = { viewModel.updateCartQuantity(food.id, 1) },
                            onDecrease = { viewModel.updateCartQuantity(food.id, -1) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    viewModel: FoodDeliveryViewModel
) {
    val orders by viewModel.previousOrders.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Past Orders") })
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            items(orders) { order ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable { /* Could navigate to details */ },
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Order #${order.orderId}", fontWeight = FontWeight.Bold)
                            Text(text = order.date, style = MaterialTheme.typography.bodySmall)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = order.restaurantName, style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "$${"%.2f".format(order.totalAmount)}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            Text(
                                text = order.status,
                                color = if (order.status == "Delivered") Color(0xFF4CAF50) else Color(0xFFFF9800),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: FoodDeliveryViewModel,
    onBack: () -> Unit
) {
    val favoriteRestaurants by viewModel.favoriteRestaurants.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = "Avatar", modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Nitin Kumar", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text("nitin.kumar@example.com", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            
            Spacer(modifier = Modifier.height(32.dp))
            
            val options = listOf(
                Pair("Saved Addresses", Icons.Default.LocationOn),
                Pair("Favourite Restaurants (${favoriteRestaurants.size})", Icons.Default.Favorite),
                Pair("Settings", Icons.Default.Settings)
            )
            
            options.forEach { (title, icon) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { }
                        .padding(vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(icon, contentDescription = title, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                    Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Go", tint = Color.Gray)
                }
                Divider()
            }
        }
    }
}
