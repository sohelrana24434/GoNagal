package com.example.model

import androidx.compose.runtime.mutableStateListOf

object WishlistRepository {
    val items = mutableStateListOf<Product>().apply {
        // Pre-populate with a few popular items
        if (MockMarketplaceData.allCatalogProducts.isNotEmpty()) {
            add(MockMarketplaceData.allCatalogProducts[0]) // Digital scale
            if (MockMarketplaceData.allCatalogProducts.size > 6) {
                add(MockMarketplaceData.allCatalogProducts[6]) // M-HORSE phone
            }
            if (MockMarketplaceData.allCatalogProducts.size > 12) {
                add(MockMarketplaceData.allCatalogProducts[12]) // Cat headphones
            }
        }
    }

    fun isWishlisted(product: Product): Boolean {
        return items.any { it.id == product.id }
    }

    fun toggleWishlist(product: Product): Boolean {
        val index = items.indexOfFirst { it.id == product.id }
        return if (index >= 0) {
            items.removeAt(index)
            false
        } else {
            items.add(0, product)
            true
        }
    }

    fun removeFromWishlist(product: Product) {
        items.removeAll { it.id == product.id }
    }

    fun clearAll() {
        items.clear()
    }
}
