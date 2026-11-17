package com.example.ui.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SlowMotionVideo
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.model.CartRepository
import com.example.model.MockMarketplaceData
import com.example.model.MockReelsData
import com.example.model.OrdersRepository
import com.example.model.Product
import com.example.model.ReelVideo
import com.example.model.WishlistRepository
import com.example.ui.components.CustomerTab
import com.example.ui.components.FlashSaleTimer
import com.example.ui.components.GoNagalBottomBar
import com.example.ui.components.GoNagalBrandLogo
import com.example.ui.components.HeroBannerCarousel
import com.example.ui.components.LogoSize
import com.example.ui.components.ProductCard
import com.example.ui.components.ProductDetailOrderBottomSheet
import com.example.ui.components.SellerStoreBottomSheet
import com.example.ui.screens.account.AccountScreen
import com.example.ui.screens.cart.CartScreen
import com.example.ui.screens.categories.CategoriesScreen
import com.example.ui.screens.chat.GeminiChatScreen
import com.example.ui.screens.orders.OrdersScreen
import com.example.ui.screens.reels.ReelsScreen
import com.example.ui.screens.shop.ShopScreen
import com.example.ui.screens.wishlist.WishlistScreen
import com.example.ui.theme.GoNagalBackground
import com.example.ui.theme.GoNagalGreen
import com.example.ui.theme.GoNagalNavy
import com.example.ui.theme.GoNagalOrange
import com.example.ui.theme.GoNagalTextMuted
import com.example.ui.theme.GoNagalTextPrimary
import com.example.ui.theme.GoNagalTextSecondary
import com.example.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainMarketplaceHomeScreen(
    authViewModel: AuthViewModel,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentTab by remember { mutableStateOf(CustomerTab.HOME) }
    var selectedCategoryFilter by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    var cartCount by remember { mutableIntStateOf(CartRepository.items.size) }

    // Selected product for direct purchase & customization sheet
    var activeProductForOrder by remember { mutableStateOf<Product?>(null) }
    // Selected seller to view shop page
    var activeSellerForStore by remember { mutableStateOf<String?>(null) }

    // Sub-screens overlay states
    var showWishlistOverlay by remember { mutableStateOf(false) }
    var showOrdersOverlay by remember { mutableStateOf(false) }
    var showReelsOverlay by remember { mutableStateOf(false) }
    var showGeminiOverlay by remember { mutableStateOf(false) }
    var showNotificationDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // All catalog products
    val allProducts = MockMarketplaceData.allCatalogProducts
    val filteredProducts = allProducts.filter { product ->
        (selectedCategoryFilter == "All" || product.category.contains(selectedCategoryFilter, ignoreCase = true)) &&
                (searchQuery.isBlank() || product.title.contains(searchQuery, ignoreCase = true) || product.brand.contains(searchQuery, ignoreCase = true))
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        bottomBar = {
            GoNagalBottomBar(
                currentTab = currentTab,
                onTabSelected = { tab ->
                    currentTab = tab
                    showWishlistOverlay = false
                    showOrdersOverlay = false
                    showReelsOverlay = false
                    showGeminiOverlay = false
                },
                cartItemCount = CartRepository.items.size
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                showWishlistOverlay -> {
                    WishlistScreen(
                        onBack = { showWishlistOverlay = false },
                        onProductClick = { activeProductForOrder = it },
                        onAddToCart = { activeProductForOrder = it }
                    )
                }
                showOrdersOverlay -> {
                    OrdersScreen(
                        onBack = { showOrdersOverlay = false }
                    )
                }
                showReelsOverlay -> {
                    ReelsScreen(
                        onProductClick = { activeProductForOrder = it },
                        onAddToCart = { activeProductForOrder = it },
                        onBack = { showReelsOverlay = false }
                    )
                }
                showGeminiOverlay -> {
                    GeminiChatScreen(
                        onBack = { showGeminiOverlay = false }
                    )
                }
                else -> {
                    when (currentTab) {
                        CustomerTab.HOME -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(GoNagalBackground)
                                    .verticalScroll(scrollState)
                                    .testTag("home_screen")
                            ) {
                                // ================= 1. HEADER SECTION =================
                                Surface(
                                    color = Color.White,
                                    shadowElevation = 2.dp,
                                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 14.dp, vertical = 10.dp)
                                    ) {
                                        // Top Row: Prominent Brand Logo & Large Action Icons
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            // Prominent Bold Typographic GoNagal Logo (No image/bag icon)
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "Go",
                                                    fontSize = 28.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = GoNagalGreen,
                                                    letterSpacing = (-0.5).sp
                                                )
                                                Text(
                                                    text = "Nagal",
                                                    fontSize = 28.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = GoNagalNavy,
                                                    letterSpacing = (-0.5).sp
                                                )
                                            }

                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                // Wishlist with Badge
                                                BadgedBox(
                                                    badge = {
                                                        if (WishlistRepository.items.isNotEmpty()) {
                                                            Badge(containerColor = Color(0xFFDC2626)) {
                                                                Text("${WishlistRepository.items.size}", color = Color.White, fontSize = 9.sp)
                                                            }
                                                        }
                                                    },
                                                    modifier = Modifier.clickable { showWishlistOverlay = true }
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Favorite,
                                                        contentDescription = "Wishlist",
                                                        tint = Color(0xFFDC2626),
                                                        modifier = Modifier.size(26.dp)
                                                    )
                                                }

                                                // Notification with Badge
                                                BadgedBox(
                                                    badge = {
                                                        Badge(containerColor = GoNagalOrange) {
                                                            Text("3", color = Color.White, fontSize = 9.sp)
                                                        }
                                                    },
                                                    modifier = Modifier.clickable { showNotificationDialog = true }
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Notifications,
                                                        contentDescription = "Notifications",
                                                        tint = GoNagalNavy,
                                                        modifier = Modifier.size(26.dp)
                                                    )
                                                }

                                                // Cart with Badge
                                                BadgedBox(
                                                    badge = {
                                                        if (CartRepository.items.isNotEmpty()) {
                                                            Badge(containerColor = GoNagalOrange) {
                                                                Text("${CartRepository.items.size}", color = Color.White, fontSize = 9.sp)
                                                            }
                                                        }
                                                    },
                                                    modifier = Modifier.clickable { currentTab = CustomerTab.CART }
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.ShoppingCart,
                                                        contentDescription = "Cart",
                                                        tint = GoNagalNavy,
                                                        modifier = Modifier.size(26.dp)
                                                    )
                                                }

                                                // Profile / Account
                                                Icon(
                                                    imageVector = Icons.Default.AccountCircle,
                                                    contentDescription = "Account",
                                                    tint = GoNagalNavy,
                                                    modifier = Modifier
                                                        .size(28.dp)
                                                        .clickable { currentTab = CustomerTab.ACCOUNT }
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))

                                        // Compact, Modern Search Bar (Height 44dp, Crisp Dark Text)
                                        Surface(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(44.dp),
                                            shape = RoundedCornerShape(10.dp),
                                            color = Color(0xFFF1F5F9),
                                            border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .padding(horizontal = 12.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Search,
                                                    contentDescription = "Search",
                                                    tint = Color(0xFF0F172A),
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                BasicTextField(
                                                    value = searchQuery,
                                                    onValueChange = { searchQuery = it },
                                                    modifier = Modifier.weight(1f),
                                                    singleLine = true,
                                                    textStyle = androidx.compose.ui.text.TextStyle(
                                                        fontSize = 13.sp,
                                                        color = Color(0xFF0F172A),
                                                        fontWeight = FontWeight.Medium
                                                    ),
                                                    decorationBox = { innerTextField ->
                                                        if (searchQuery.isEmpty()) {
                                                            Text(
                                                                text = "পণ্য, পোশাক বা ব্র্যান্ড খুঁজুন...",
                                                                fontSize = 12.5.sp,
                                                                color = Color(0xFF64748B)
                                                            )
                                                        }
                                                        innerTextField()
                                                    }
                                                )
                                                if (searchQuery.isNotEmpty()) {
                                                    Icon(
                                                        imageVector = Icons.Default.Close,
                                                        contentDescription = "Clear Search",
                                                        tint = Color(0xFF64748B),
                                                        modifier = Modifier
                                                            .size(18.dp)
                                                            .clickable { searchQuery = "" }
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                // ================= 2. HERO BANNER CAROUSEL (4 BANNERS, 2-3s AUTO-SLIDE) =================
                                HeroBannerCarousel(
                                    banners = MockMarketplaceData.banners,
                                    onBannerClick = { banner ->
                                        currentTab = CustomerTab.CATEGORIES
                                    },
                                    modifier = Modifier.padding(top = 2.dp)
                                )

                                // ================= 3. PRODUCT CATEGORIES =================
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 16.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 14.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = "ক্যাটাগরি ব্রাউজ করুন (Categories)",
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = GoNagalNavy
                                            )
                                            Text(
                                                text = "পোশাক, গ্যাজেট ও লাইফস্টাইল",
                                                fontSize = 11.sp,
                                                color = GoNagalTextMuted
                                            )
                                        }
                                        Text(
                                            text = "সব দেখুন (১০+)",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GoNagalGreen,
                                            modifier = Modifier.clickable { currentTab = CustomerTab.CATEGORIES }
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    LazyRow(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp)
                                    ) {
                                        item {
                                            CategoryBubbleItem(
                                                icon = "🔥",
                                                name = "All Deals",
                                                isSelected = selectedCategoryFilter == "All",
                                                onClick = { selectedCategoryFilter = "All" }
                                            )
                                        }
                                        items(MockMarketplaceData.categories) { cat ->
                                            CategoryBubbleItem(
                                                icon = cat.iconEmoji,
                                                name = cat.name,
                                                isSelected = selectedCategoryFilter.contains(cat.name, ignoreCase = true),
                                                onClick = {
                                                    selectedCategoryFilter = if (selectedCategoryFilter == cat.name) "All" else cat.name
                                                }
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // ================= 4. NEW OPTION REEL (অপশন রিল) SECTION =================
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFFF1F5F9))
                                        .padding(vertical = 14.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 14.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Surface(
                                                shape = CircleShape,
                                                color = Color(0xFFDC2626),
                                                modifier = Modifier.size(24.dp)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
                                                }
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column {
                                                Text(
                                                    text = "অপশন রিল (Option Reels)",
                                                    fontSize = 15.sp,
                                                    fontWeight = FontWeight.Black,
                                                    color = GoNagalNavy
                                                )
                                                Text(
                                                    text = "ট্রেন্ডিং ভিডিও ও রিয়েল প্রোডাক্ট ট্রায়াল",
                                                    fontSize = 11.sp,
                                                    color = GoNagalTextMuted
                                                )
                                            }
                                        }

                                        Text(
                                            text = "সব রিল দেখুন ➔",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFDC2626),
                                            modifier = Modifier.clickable { showReelsOverlay = true }
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    LazyRow(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp)
                                    ) {
                                        items(MockReelsData.reels) { reel ->
                                            OptionReelItemCard(
                                                reel = reel,
                                                onReelClick = { showReelsOverlay = true },
                                                onProductClick = { activeProductForOrder = reel.taggedProduct }
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // ================= 5. FLASH SALE SECTION =================
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF7ED)),
                                    border = BorderStroke(1.dp, Color(0xFFFED7AA)),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Surface(
                                                    shape = CircleShape,
                                                    color = GoNagalOrange,
                                                    modifier = Modifier.size(28.dp)
                                                ) {
                                                    Box(contentAlignment = Alignment.Center) {
                                                        Icon(Icons.Default.Bolt, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                                    }
                                                }
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Column {
                                                    Text(
                                                        text = "ফ্ল্যাশ সেল (Flash Sale)",
                                                        fontSize = 15.sp,
                                                        fontWeight = FontWeight.ExtraBold,
                                                        color = GoNagalOrange
                                                    )
                                                    Text(
                                                        text = "সর্বোচ্চ ৭৭% পর্যন্ত মূল্যছাড়!",
                                                        fontSize = 10.5.sp,
                                                        fontWeight = FontWeight.SemiBold,
                                                        color = GoNagalTextMuted
                                                    )
                                                }
                                            }

                                            FlashSaleTimer()
                                        }

                                        Spacer(modifier = Modifier.height(12.dp))

                                        LazyRow(
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            items(MockMarketplaceData.flashSaleProducts) { product ->
                                                Box(modifier = Modifier.width(162.dp)) {
                                                    ProductCard(
                                                        product = product,
                                                        onProductClick = { activeProductForOrder = it },
                                                        onAddToCart = { activeProductForOrder = it }
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(18.dp))

                                // ================= 6. TRENDING PRODUCTS =================
                                SectionHeader(
                                    title = "ট্রেন্ডিং পণ্য (Trending Products)",
                                    subtitle = "বর্তমানে সবচেয়ে জনপ্রিয় ফ্যাশন ও পণ্য",
                                    actionText = "সব দেখুন",
                                    onAction = { currentTab = CustomerTab.CATEGORIES }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                LazyRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp)
                                ) {
                                    items(MockMarketplaceData.regularProducts.take(8)) { product ->
                                        Box(modifier = Modifier.width(162.dp)) {
                                            ProductCard(
                                                product = product,
                                                onProductClick = { activeProductForOrder = it },
                                                onAddToCart = { activeProductForOrder = it }
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(18.dp))

                                // ================= 7. TOP SELLERS =================
                                SectionHeader(
                                    title = "টপ সেলার স্টোর (Top Sellers)",
                                    subtitle = "GoNagal ভেরিফাইড ১০০% অরিজিনাল শপ",
                                    actionText = "সব শপ",
                                    onAction = {}
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp)
                                ) {
                                    val sellers = listOf(
                                        Triple("Dhaka Shopping zone", "4.9★ (3,240 reviews)", "https://images.unsplash.com/photo-1441986300917-64674bd600d8?w=200&auto=format&fit=crop&q=80"),
                                        Triple("GoNagal Boutique Mall", "5.0★ (4,120 reviews)", "https://images.unsplash.com/photo-1472851294608-062f824d29cc?w=200&auto=format&fit=crop&q=80"),
                                        Triple("Smart Electronics BD", "4.8★ (1,890 reviews)", "https://images.unsplash.com/photo-1550009158-9ebf69173e03?w=200&auto=format&fit=crop&q=80"),
                                        Triple("Dhaka Fashion Mall", "4.7★ (2,100 reviews)", "https://images.unsplash.com/photo-1489987707025-afc232f7ea0f?w=200&auto=format&fit=crop&q=80")
                                    )
                                    items(sellers) { (name, rating, imageUrl) ->
                                        Card(
                                            modifier = Modifier
                                                .width(200.dp)
                                                .clickable { activeSellerForStore = name },
                                            shape = RoundedCornerShape(12.dp),
                                            colors = CardDefaults.cardColors(containerColor = Color.White),
                                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                                        ) {
                                            Column(modifier = Modifier.padding(12.dp)) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Surface(
                                                        shape = CircleShape,
                                                        color = Color(0xFFF1F5F9),
                                                        modifier = Modifier.size(40.dp)
                                                    ) {
                                                        AsyncImage(
                                                            model = imageUrl,
                                                            contentDescription = name,
                                                            contentScale = ContentScale.Crop,
                                                            modifier = Modifier.fillMaxSize()
                                                        )
                                                    }
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Column {
                                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                                            Text(
                                                                text = name,
                                                                fontSize = 11.5.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                color = GoNagalNavy,
                                                                maxLines = 1
                                                            )
                                                            Spacer(modifier = Modifier.width(3.dp))
                                                            Icon(Icons.Default.Verified, contentDescription = null, tint = GoNagalGreen, modifier = Modifier.size(12.dp))
                                                        }
                                                        Text(rating, fontSize = 10.sp, color = GoNagalOrange, fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Surface(
                                                    shape = RoundedCornerShape(6.dp),
                                                    color = Color(0xFFF0FDF4),
                                                    border = BorderStroke(1.dp, Color(0xFFBBF7D0)),
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    Text(
                                                        text = "স্টোর ভিজিট করুন ➔",
                                                        fontSize = 10.5.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = GoNagalGreen,
                                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                                        modifier = Modifier.padding(vertical = 4.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(18.dp))

                                // ================= 8. SPECIAL OFFERS =================
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = GoNagalNavy)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = GoNagalOrange, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("বিশেষ অফার (Special Offers)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "কুপন কোড GONAGAL10 ব্যবহারে ১০% ক্যাশব্যাক এবং FREE60 তে ডেলিভারি ফ্রি!",
                                                fontSize = 10.5.sp,
                                                color = Color.White.copy(alpha = 0.9f)
                                            )
                                        }
                                        Button(
                                            onClick = { currentTab = CustomerTab.CATEGORIES },
                                            colors = ButtonDefaults.buttonColors(containerColor = GoNagalOrange),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text("শপ করুন", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(18.dp))

                                // ================= 9. RECOMMENDED PRODUCTS (2-COLUMN GRID) =================
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp)
                                ) {
                                    SectionHeader(
                                        title = "আপনার জন্য প্রস্তাবিত (Recommended Products)",
                                        subtitle = "সেরা মূল্য ও দ্রুত হোম ডেলিভারি",
                                        actionText = "সব দেখুন",
                                        onAction = { currentTab = CustomerTab.CATEGORIES }
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Display 2 items per row
                                    val displayProducts = filteredProducts.chunked(2)
                                    displayProducts.forEach { rowItems ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(bottom = 10.dp),
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            rowItems.forEach { product ->
                                                Box(modifier = Modifier.weight(1f)) {
                                                    ProductCard(
                                                        product = product,
                                                        onProductClick = { activeProductForOrder = it },
                                                        onAddToCart = { activeProductForOrder = it }
                                                    )
                                                }
                                            }
                                            if (rowItems.size == 1) {
                                                Spacer(modifier = Modifier.weight(1f))
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(24.dp))
                            }
                        }

                        CustomerTab.REELS -> {
                            ReelsScreen(
                                onProductClick = { activeProductForOrder = it },
                                onAddToCart = { activeProductForOrder = it },
                                onBack = { currentTab = CustomerTab.HOME }
                            )
                        }

                        CustomerTab.CATEGORIES -> {
                            CategoriesScreen(
                                onProductClick = { activeProductForOrder = it },
                                onAddToCart = { activeProductForOrder = it }
                            )
                        }

                        CustomerTab.CART -> {
                            CartScreen(
                                onOrderPlacedNavToOrders = {
                                    showOrdersOverlay = true
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("আপনার অর্ডারটি সফলভাবে গ্রহণ করা হয়েছে! (Order Confirmed)")
                                    }
                                }
                            )
                        }

                        CustomerTab.ACCOUNT -> {
                            AccountScreen(
                                authViewModel = authViewModel,
                                onLogout = onLogout,
                                onNavigateToOrders = { showOrdersOverlay = true },
                                onNavigateToWishlist = { showWishlistOverlay = true }
                            )
                        }
                    }
                }
            }
        }
    }

    // Modal BottomSheet for Product Details & Customization
    activeProductForOrder?.let { product ->
        ProductDetailOrderBottomSheet(
            product = product,
            onDismiss = { activeProductForOrder = null },
            onAddToCart = { p, qty, color, size ->
                CartRepository.addToCart(p, qty, color, size)
                cartCount = CartRepository.items.size
                coroutineScope.launch {
                    val variantDetail = listOfNotNull(color, size).joinToString(", ")
                    val text = if (variantDetail.isNotEmpty()) "'${p.title}' ($variantDetail) কার্টে যোগ হয়েছে!" else "'${p.title}' কার্টে যোগ হয়েছে!"
                    snackbarHostState.showSnackbar(text)
                }
            },
            onDirectOrderNow = { _, _, _, _, _, _, _ -> },
            onOrderPlacedSuccess = { orderId ->
                showOrdersOverlay = true
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("অর্ডার সফল! অর্ডার আইডি: #$orderId (ক্যাশ অন ডেলিভারি)")
                }
            },
            onOpenSellerStore = { sName ->
                activeSellerForStore = sName
            },
            onWatchVideo = { _ ->
                showReelsOverlay = true
            }
        )
    }

    // Modal BottomSheet for Seller Store
    activeSellerForStore?.let { sName ->
        SellerStoreBottomSheet(
            sellerName = sName,
            onDismiss = { activeSellerForStore = null },
            onProductClick = { activeProductForOrder = it },
            onAddToCart = { activeProductForOrder = it }
        )
    }

    // Notifications Dialog
    if (showNotificationDialog) {
        AlertDialog(
            onDismissRequest = { showNotificationDialog = false },
            confirmButton = {
                Button(
                    onClick = { showNotificationDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = GoNagalNavy)
                ) {
                    Text("ঠিক আছে")
                }
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Notifications, contentDescription = null, tint = GoNagalOrange, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("নোটিফিকেশন (Notifications)", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = GoNagalNavy)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)), shape = RoundedCornerShape(8.dp)) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("⚡ ফ্ল্যাশ সেল শুরু হয়েছে!", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoNagalGreen)
                            Text("পোশাক ও গ্যাজেট পণ্যে সর্বোচ্চ ৭৭% ছাড় চলছে।", fontSize = 11.sp, color = GoNagalNavy)
                        }
                    }
                    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)), shape = RoundedCornerShape(8.dp)) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("🎁 ঈদ ভাউচার কোড: EID2026", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoNagalOrange)
                            Text("চেকআউটে কুপন কোড ব্যবহার করে অতিরিক্ত ৳১৫০ ছাড় নিন।", fontSize = 11.sp, color = GoNagalNavy)
                        }
                    }
                    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)), shape = RoundedCornerShape(8.dp)) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("🚚 অর্ডার আপডেট #GN-8921", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoNagalNavy)
                            Text("আপনার অর্ডারটি সেন্ট্রাল হাব থেকে ডেলিভারির উদ্দেশ্যে রওনা হয়েছে।", fontSize = 11.sp, color = GoNagalTextMuted)
                        }
                    }
                }
            }
        )
    }
}

@Composable
private fun OptionReelItemCard(
    reel: ReelVideo,
    onReelClick: (ReelVideo) -> Unit,
    onProductClick: (Product) -> Unit
) {
    Card(
        modifier = Modifier
            .width(130.dp)
            .height(190.dp)
            .clickable { onReelClick(reel) },
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Real photo background
            val imgUrl = reel.videoThumbnailUrl.ifEmpty { reel.taggedProduct.imageUrl }
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imgUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = reel.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Scrim overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.25f),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.85f)
                            )
                        )
                    )
            )

            // Top Play / Reel Tag
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color(0xFFDC2626),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(11.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "REEL",
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }
            }

            // Center Play Icon Button
            Surface(
                shape = CircleShape,
                color = Color.Black.copy(alpha = 0.45f),
                border = BorderStroke(1.5.dp, Color.White),
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(34.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Bottom Title & Tagged Product Chip
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
            ) {
                Text(
                    text = reel.title,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 13.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Surface(
                    onClick = { onProductClick(reel.taggedProduct) },
                    shape = RoundedCornerShape(4.dp),
                    color = Color.White.copy(alpha = 0.95f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = reel.taggedProduct.formattedSellingPrice,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = GoNagalGreen
                        )
                        Text(
                            text = "কিনুন",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoNagalNavy
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    subtitle: String,
    actionText: String,
    onAction: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = GoNagalNavy
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = GoNagalTextMuted
            )
        }
        Text(
            text = actionText,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = GoNagalGreen,
            modifier = Modifier.clickable { onAction() }
        )
    }
}

@Composable
private fun CategoryBubbleItem(
    icon: String,
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .width(66.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = if (isSelected) GoNagalGreen else Color.White,
            border = if (!isSelected) BorderStroke(1.dp, Color(0xFFE2E8F0)) else null,
            shadowElevation = if (isSelected) 3.dp else 1.dp,
            modifier = Modifier.size(52.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = icon,
                    fontSize = 22.sp
                )
            }
        }
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = name,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) GoNagalGreen else GoNagalNavy,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
