package com.example.model

data class ReelVideo(
    val id: String,
    val title: String,
    val description: String,
    val creatorName: String,
    val creatorHandle: String,
    val musicTrack: String,
    val likesCount: Int,
    val commentsCount: Int,
    val sharesCount: Int,
    val isLiked: Boolean = false,
    val isFollowing: Boolean = false,
    val taggedProduct: Product,
    val videoBgGradientStart: Long,
    val videoBgGradientEnd: Long,
    val videoThumbnailUrl: String = "",
    val productDemoIcon: String = ""
)

object MockReelsData {
    val reels = listOf(
        ReelVideo(
            id = "reel_1",
            title = "Digital Portable Weight Scale 50 Kg Test",
            description = "Black Dhaka Shopping zone portable luggage & market scale. Accurate reading up to 50kg!",
            creatorName = "Dhaka Shopping Review",
            creatorHandle = "@dhakashopping",
            musicTrack = "Original Sound - Dhaka Shopping Review",
            likesCount = 4280,
            commentsCount = 312,
            sharesCount = 520,
            taggedProduct = MockMarketplaceData.allCatalogProducts[0],
            videoBgGradientStart = 0xFF0F172A,
            videoBgGradientEnd = 0xFF1E293B,
            videoThumbnailUrl = "https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?w=800&auto=format&fit=crop&q=80"
        ),
        ReelVideo(
            id = "reel_2",
            title = "M-HORSE M17 Pro Max 8GB RAM Unboxing",
            description = "5000mAh Battery, 4G Network & 1 Year Brand Warranty. Best budget smartphone deal!",
            creatorName = "Gadget Lifestyle BD",
            creatorHandle = "@gadgetlifebd",
            musicTrack = "Upbeat Pop - Viral Beats",
            likesCount = 8940,
            commentsCount = 640,
            sharesCount = 1200,
            taggedProduct = MockMarketplaceData.allCatalogProducts[6],
            videoBgGradientStart = 0xFF164E63,
            videoBgGradientEnd = 0xFF0E7490,
            videoThumbnailUrl = "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=800&auto=format&fit=crop&q=80"
        ),
        ReelVideo(
            id = "reel_3",
            title = "Naira Cut Three Piece Unstitched Cotton",
            description = "Exclusive Screen Print design for women. Stylist & fashionable 3-piece dress collection!",
            creatorName = "Dhaka Elegance Boutique",
            creatorHandle = "@dhakaboutique",
            musicTrack = "Traditional Folk Beats - Bangla Vibe",
            likesCount = 12500,
            commentsCount = 890,
            sharesCount = 2300,
            taggedProduct = MockMarketplaceData.allCatalogProducts[35],
            videoBgGradientStart = 0xFF312E81,
            videoBgGradientEnd = 0xFF4338CA,
            videoThumbnailUrl = "https://images.unsplash.com/photo-1583391733956-3750e0ff4e8b?w=800&auto=format&fit=crop&q=80"
        ),
        ReelVideo(
            id = "reel_4",
            title = "CAT STN-28 Glowing Ear Wireless Headphones",
            description = "Over Ear glowing LED lights, Bluetooth 5.0, AUX and TF Card MP3 support!",
            creatorName = "Gamer Shakil",
            creatorHandle = "@gamershakil",
            musicTrack = "Energy Workout Boost - EDM",
            likesCount = 6720,
            commentsCount = 415,
            sharesCount = 980,
            taggedProduct = MockMarketplaceData.allCatalogProducts[12],
            videoBgGradientStart = 0xFF14532D,
            videoBgGradientEnd = 0xFF15803D,
            videoThumbnailUrl = "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=800&auto=format&fit=crop&q=80"
        ),
        ReelVideo(
            id = "reel_5",
            title = "Exclusive Women Pori Gown & Festive Look",
            description = "Designer party one piece gown with elegant flares and embroidery!",
            creatorName = "GoNagal Fashion Hub",
            creatorHandle = "@gonagal.fashion",
            musicTrack = "Fashion Week Acoustic Runway",
            likesCount = 15800,
            commentsCount = 1240,
            sharesCount = 3100,
            taggedProduct = MockMarketplaceData.allCatalogProducts[21],
            videoBgGradientStart = 0xFF4A044E,
            videoBgGradientEnd = 0xFF701A75,
            videoThumbnailUrl = "https://images.unsplash.com/photo-1566174053879-31528523f8ae?w=800&auto=format&fit=crop&q=80"
        ),
        ReelVideo(
            id = "reel_6",
            title = "Household Electric Mini Spice Grinder In Action",
            description = "5-blade 304 Stainless steel grinding fine powder for coffee, spices & grains!",
            creatorName = "Kitchen Master BD",
            creatorHandle = "@kitchenmaster",
            musicTrack = "Lofi Cooking Groove",
            likesCount = 5420,
            commentsCount = 290,
            sharesCount = 840,
            taggedProduct = MockMarketplaceData.allCatalogProducts[45],
            videoBgGradientStart = 0xFF1E3A8A,
            videoBgGradientEnd = 0xFF2563EB,
            videoThumbnailUrl = "https://images.unsplash.com/photo-1585515320310-259814833e62?w=800&auto=format&fit=crop&q=80"
        )
    )
}
