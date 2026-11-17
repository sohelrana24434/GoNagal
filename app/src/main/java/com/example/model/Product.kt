package com.example.model

data class Product(
    val id: String,
    val title: String,
    val category: String,
    val brand: String,
    val originalPrice: Double,
    val sellingPrice: Double,
    val discountPercent: Int,
    val rating: Float = 4.7f,
    val reviewCount: Int = 12,
    val stockCount: Int = 50,
    val inStock: Boolean = stockCount > 0,
    val sellerName: String = "GoNagal Verified Mall",
    val isVerifiedSeller: Boolean = true,
    val isFlashSale: Boolean = false,
    val isBestSeller: Boolean = false,
    val isTrending: Boolean = false,
    val imageUrl: String = "",
    val imageEmoji: String = "",
    val colorVariants: List<String> = listOf("Default", "Black", "White"),
    val sizeVariants: List<String> = emptyList()
) {
    val formattedSellingPrice: String
        get() = "৳%,.0f".format(sellingPrice)

    val formattedOriginalPrice: String
        get() = "৳%,.0f".format(originalPrice)
}
