package com.example.fooddeliveryapp.viewmodel

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import com.example.fooddeliveryapp.data.CartItem
import com.example.fooddeliveryapp.data.DummyData
import com.example.fooddeliveryapp.data.FoodItem
import com.example.fooddeliveryapp.data.Notification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FoodDeliveryViewModel : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _selectedLocation = MutableStateFlow(DummyData.locations.first())
    val selectedLocation: StateFlow<String> = _selectedLocation.asStateFlow()

    private val _favoriteRestaurants = MutableStateFlow<Set<String>>(emptySet())
    val favoriteRestaurants: StateFlow<Set<String>> = _favoriteRestaurants.asStateFlow()

    // Using mutableStateMapOf for Compose recomposition on quantity changes
    val cartItems = mutableStateMapOf<String, CartItem>()

    private val _notifications = MutableStateFlow(DummyData.notifications)
    val notifications: StateFlow<List<Notification>> = _notifications.asStateFlow()

    private val _previousOrders = MutableStateFlow(DummyData.previousOrders)
    val previousOrders = _previousOrders.asStateFlow()

    // Filter states
    private val _minRating = MutableStateFlow(0.0)
    val minRating = _minRating.asStateFlow()

    private val _maxDeliveryTime = MutableStateFlow(100) // in minutes
    val maxDeliveryTime = _maxDeliveryTime.asStateFlow()

    private val _freeDeliveryOnly = MutableStateFlow(false)
    val freeDeliveryOnly = _freeDeliveryOnly.asStateFlow()

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setLocation(location: String) {
        _selectedLocation.value = location
    }

    fun toggleFavorite(restaurantId: String) {
        _favoriteRestaurants.update { current ->
            if (current.contains(restaurantId)) {
                current - restaurantId
            } else {
                current + restaurantId
            }
        }
    }

    fun addToCart(foodItem: FoodItem) {
        val currentQuantity = cartItems[foodItem.id]?.quantity ?: 0
        cartItems[foodItem.id] = CartItem(foodItem, currentQuantity + 1)
    }

    fun updateCartQuantity(foodId: String, delta: Int) {
        val item = cartItems[foodId] ?: return
        val newQuantity = item.quantity + delta
        if (newQuantity <= 0) {
            cartItems.remove(foodId)
        } else {
            cartItems[foodId] = item.copy(quantity = newQuantity)
        }
    }

    fun clearCart() {
        cartItems.clear()
    }

    fun getCartTotal(): Double {
        return cartItems.values.sumOf { it.foodItem.price * it.quantity }
    }
    
    fun getCartItemsCount(): Int {
        return cartItems.values.sumOf { it.quantity }
    }

    fun markNotificationRead(id: String) {
        _notifications.update { currentList ->
            currentList.map { if (it.id == id) it.copy(isRead = true) else it }
        }
    }

    fun applyFilters(rating: Double, maxTime: Int, freeDelivery: Boolean) {
        _minRating.value = rating
        _maxDeliveryTime.value = maxTime
        _freeDeliveryOnly.value = freeDelivery
    }

    fun resetFilters() {
        _minRating.value = 0.0
        _maxDeliveryTime.value = 100
        _freeDeliveryOnly.value = false
    }

    fun placeOrder(restaurantName: String = "Multiple Restaurants") {
        val total = getCartTotal()
        val newOrder = com.example.fooddeliveryapp.data.Order(
            orderId = "FD" + (1000..9999).random(),
            restaurantName = restaurantName,
            totalAmount = total,
            status = "Preparing",
            date = "Just now"
        )
        _previousOrders.update { listOf(newOrder) + it }
        clearCart()
    }

    fun getFilteredRestaurants(): List<com.example.fooddeliveryapp.data.Restaurant> {
        return DummyData.restaurants.filter { restaurant ->
            val matchesCategory = _selectedCategory.value == "All" || restaurant.categories.contains(_selectedCategory.value)
            val matchesSearch = restaurant.name.contains(_searchQuery.value, ignoreCase = true)
            val matchesRating = restaurant.rating >= _minRating.value
            val matchesFee = if (_freeDeliveryOnly.value) restaurant.deliveryFee == 0.0 else true
            
            // simple parse of time string "20-30 min"
            val timeString = restaurant.deliveryTime.split(" ").firstOrNull()?.split("-")?.lastOrNull() ?: "0"
            val time = timeString.toIntOrNull() ?: 0
            val matchesTime = time <= _maxDeliveryTime.value

            matchesCategory && matchesSearch && matchesRating && matchesFee && matchesTime
        }
    }

    fun getFilteredFoods(): List<FoodItem> {
        return DummyData.foods.filter { food ->
            val matchesCategory = _selectedCategory.value == "All" || food.category == _selectedCategory.value
            val matchesSearch = food.name.contains(_searchQuery.value, ignoreCase = true) || food.description.contains(_searchQuery.value, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }
}
