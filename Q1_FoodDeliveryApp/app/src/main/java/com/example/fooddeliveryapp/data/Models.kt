package com.example.fooddeliveryapp.data

data class FoodItem(
    val id: String,
    val restaurantId: String,
    val name: String,
    val description: String,
    val price: Double,
    val rating: Double,
    val category: String
)

data class Restaurant(
    val id: String,
    val name: String,
    val rating: Double,
    val deliveryTime: String,
    val deliveryFee: Double,
    val categories: List<String>,
    val isRecommended: Boolean = false
)

data class CartItem(
    val foodItem: FoodItem,
    val quantity: Int
)

data class Order(
    val orderId: String,
    val restaurantName: String,
    val totalAmount: Double,
    val status: String,
    val date: String
)

data class Notification(
    val id: String,
    val message: String,
    var isRead: Boolean = false
)

object DummyData {
    val locations = listOf(
        "Home — Chandigarh, Punjab",
        "University — Phagwara, Punjab",
        "Work — Mohali, Punjab"
    )

    val restaurants = listOf(
        Restaurant("r1", "Pizza District", 4.5, "20-30 min", 2.99, listOf("Pizza", "Fast Food")),
        Restaurant("r2", "The Urban Kitchen", 4.2, "30-40 min", 0.0, listOf("Biryani", "Indian")),
        Restaurant("r3", "Burger Point", 4.0, "15-25 min", 1.99, listOf("Burger", "Fast Food")),
        Restaurant("r4", "Sweet Treats", 4.8, "20-30 min", 0.0, listOf("Desserts")),
        Restaurant("r5", "Dragon Wok", 4.3, "35-45 min", 3.99, listOf("Chinese"))
    )

    val foods = listOf(
        FoodItem("f1", "r1", "Margherita Pizza", "Classic cheese and tomato base", 9.99, 4.5, "Pizza"),
        FoodItem("f2", "r1", "Pepperoni Pizza", "Loaded with pepperoni and extra cheese", 12.99, 4.8, "Pizza"),
        FoodItem("f3", "r1", "Cheese Burst Pizza", "Double cheese crust pizza", 14.99, 4.7, "Pizza"),
        FoodItem("f4", "r1", "Garlic Bread", "Freshly baked garlic bread sticks", 4.99, 4.2, "Pizza"),
        
        FoodItem("f5", "r2", "Chicken Biryani", "Aromatic basmati rice with tender chicken", 8.99, 4.6, "Biryani"),
        FoodItem("f6", "r2", "Mutton Biryani", "Rich and spicy mutton biryani", 11.99, 4.8, "Biryani"),
        FoodItem("f7", "r2", "Paneer Tikka", "Grilled cottage cheese blocks", 6.99, 4.3, "Indian"),
        FoodItem("f8", "r2", "Butter Naan", "Soft Indian bread", 1.99, 4.5, "Indian"),
        
        FoodItem("f9", "r3", "Classic Burger", "Juicy beef patty with fresh lettuce", 5.99, 4.1, "Burger"),
        FoodItem("f10", "r3", "Cheese Burger", "Beef patty with extra melting cheese", 6.99, 4.4, "Burger"),
        FoodItem("f11", "r3", "Fries", "Crispy golden potato fries", 2.99, 4.0, "Burger"),
        FoodItem("f12", "r3", "Coke", "Chilled refreshing beverage", 1.50, 4.5, "Drinks"),
        
        FoodItem("f13", "r4", "Chocolate Brownie", "Warm chocolate brownie with syrup", 4.99, 4.8, "Desserts"),
        FoodItem("f14", "r4", "Vanilla Ice Cream", "Classic vanilla scoop", 3.99, 4.5, "Desserts"),
        
        FoodItem("f15", "r5", "Hakka Noodles", "Stir-fried noodles with veggies", 7.99, 4.2, "Chinese"),
        FoodItem("f16", "r5", "Manchurian", "Spicy vegetable balls in gravy", 6.99, 4.3, "Chinese")
    )

    val previousOrders = listOf(
        Order("FD1024", "Pizza District", 24.97, "Delivered", "Yesterday, 8:00 PM"),
        Order("FD1018", "The Urban Kitchen", 18.98, "Delivered", "Oct 10, 1:30 PM")
    )

    val notifications = listOf(
        Notification("n1", "Your order is arriving soon", false),
        Notification("n2", "30% discount available on your first order", false),
        Notification("n3", "Pizza District is offering free delivery", true)
    )
}
