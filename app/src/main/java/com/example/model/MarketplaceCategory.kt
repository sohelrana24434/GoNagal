package com.example.model

data class MarketplaceCategory(
    val id: String,
    val name: String,
    val bengaliName: String,
    val iconEmoji: String,
    val productCount: Int
)

data class HeroBanner(
    val id: String,
    val title: String,
    val subtitle: String,
    val badgeText: String,
    val ctaText: String,
    val startColorHex: Long = 0xFF15803D,
    val endColorHex: Long = 0xFF0D5E1B,
    val drawableRes: Int? = null,
    val imageUrl: String = ""
)
