package com.example.model

enum class UserRole(val title: String, val description: String) {
    GUEST("Guest", "Instant access with local cart"),
    CUSTOMER("Customer", "Discover, shop, and track orders"),
    SELLER("Seller", "Manage shop, products, and sales"),
    AFFILIATE("Affiliate", "Share links and earn commissions"),
    ADMIN("Admin", "Platform moderation and controls")
}
