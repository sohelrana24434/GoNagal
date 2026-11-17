package com.example.model

data class UserCartItem(
    val product: Product,
    var quantity: Int = 1,
    var selectedColor: String? = null,
    var selectedSize: String? = null,
    var isSelected: Boolean = true
)

object CartRepository {
    val items = mutableListOf<UserCartItem>(
        UserCartItem(
            product = MockMarketplaceData.allCatalogProducts[0], // Digital Portable Weight Scale 50 Kg
            quantity = 1,
            selectedColor = "Black"
        ),
        UserCartItem(
            product = MockMarketplaceData.allCatalogProducts[1], // Elegant Women Comfort Bra
            quantity = 2,
            selectedColor = "Beige",
            selectedSize = "34"
        )
    )

    fun addToCart(product: Product, quantity: Int = 1, color: String? = null, size: String? = null) {
        val existing = items.find { it.product.id == product.id && it.selectedColor == color && it.selectedSize == size }
        if (existing != null) {
            existing.quantity += quantity
        } else {
            items.add(0, UserCartItem(product, quantity, color, size, isSelected = true))
        }
    }

    fun removeAt(index: Int) {
        if (index in items.indices) {
            items.removeAt(index)
        }
    }

    fun clearCart() {
        items.clear()
    }
}
