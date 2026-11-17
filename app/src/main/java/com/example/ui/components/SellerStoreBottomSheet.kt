package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MockMarketplaceData
import com.example.model.Product
import com.example.ui.theme.GoNagalGreen
import com.example.ui.theme.GoNagalNavy
import com.example.ui.theme.GoNagalOrange
import com.example.ui.theme.GoNagalTextMuted

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerStoreBottomSheet(
    sellerName: String,
    onDismiss: () -> Unit,
    onProductClick: (Product) -> Unit,
    onAddToCart: (Product) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var isFollowing by remember { mutableStateOf(false) }
    var followerCount by remember { mutableIntStateOf(1420) }
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Products, 1: Reviews, 2: About

    val allProducts = MockMarketplaceData.allCatalogProducts
    val sellerProducts = allProducts.filter {
        it.sellerName.contains(sellerName, ignoreCase = true) || sellerName.contains(it.sellerName, ignoreCase = true)
    }.ifEmpty { allProducts.take(6) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
        ) {
            // Seller Shop Banner & Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(GoNagalNavy, Color(0xFF1E3A8A), GoNagalGreen)
                        )
                    )
            ) {
                // Top close & share buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.Black.copy(alpha = 0.35f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.Verified, contentDescription = null, tint = GoNagalOrange, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Official GoNagal Mall Store", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color.Black.copy(alpha = 0.35f), CircleShape)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }

                // Shop Logo and Name overlay
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color.White,
                        shadowElevation = 4.dp,
                        modifier = Modifier.size(54.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("🏪", fontSize = 28.sp)
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = sellerName,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = GoNagalOrange, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("4.9 (4,820 ratings)", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("• $followerCount Followers", fontSize = 11.sp, color = Color.White.copy(alpha = 0.85f))
                        }
                    }
                }
            }

            // Follow & Chat Action Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        isFollowing = !isFollowing
                        followerCount += if (isFollowing) 1 else -1
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isFollowing) Color(0xFFF1F5F9) else GoNagalGreen
                    )
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isFollowing) Icons.Default.CheckCircle else Icons.Default.PersonAdd,
                            contentDescription = null,
                            tint = if (isFollowing) GoNagalNavy else Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isFollowing) "Following (অনুসরণ করছেন)" else "+ Follow Store",
                            color = if (isFollowing) GoNagalNavy else Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFF1F5F9),
                    modifier = Modifier.padding(2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Response Time: ", fontSize = 11.sp, color = GoNagalTextMuted)
                        Text("10 Min", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoNagalGreen)
                    }
                }
            }

            // Tabs: Products, Reviews, About
            SecondaryTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("সব পণ্য (${sellerProducts.size})", fontWeight = FontWeight.SemiBold, fontSize = 12.sp) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("রিভিউ ও রেটিং (4.9★)", fontWeight = FontWeight.SemiBold, fontSize = 12.sp) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("দোকানের পরিচিতি", fontWeight = FontWeight.SemiBold, fontSize = 12.sp) }
                )
            }

            HorizontalDivider(color = Color(0xFFF1F5F9))

            // Tab Content
            when (selectedTab) {
                0 -> {
                    // Products Grid
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp)
                    ) {
                        items(sellerProducts) { product ->
                            ProductCard(
                                product = product,
                                onProductClick = onProductClick,
                                onAddToCart = onAddToCart
                            )
                        }
                    }
                }
                1 -> {
                    // Shop Reviews
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(
                            listOf(
                                Triple("তন্ময় হাসান", "৫★ অত্যন্ত দ্রুত ডেলিভারি পেয়েছি। পণ্য ১০০% অরিজিনাল ও ইনটেক ছিলো।", "গতকাল"),
                                Triple("সাবরিনা আক্তার", "৫★ প্যাকেজিং অনেক ভালো ছিলো। সেলারের রেসপন্সও খুব ভালো। ধন্যবাদ GoNagal!", "৩ দিন আগে"),
                                Triple("কবির হোসেন", "৪.৮★ কোয়ালিটি দারুন। দামও অনেক কম ছিলো। আবার অর্ডার করবো।", "১ সপ্তাহ আগে")
                            )
                        ) { (user, review, time) ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = user, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = GoNagalNavy)
                                        Text(text = time, fontSize = 10.sp, color = GoNagalTextMuted)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = review, fontSize = 11.5.sp, color = GoNagalNavy)
                                }
                            }
                        }
                    }
                }
                2 -> {
                    // About Store
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("স্টোরের বিবরণ (About Store)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = GoNagalNavy)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "$sellerName হলো GoNagal-এর ভেরিফাইড প্রিমিয়াম সেলার। আমরা ঢাকা ও সারা বাংলাদেশে জেনুইন কোয়ালিটির ইলেকট্রনিক্স ও লাইফস্টাইল পণ্য সরাসরি ওয়ারেন্টিসহ সরবরাহ করে থাকি।",
                                    fontSize = 11.5.sp,
                                    color = GoNagalNavy
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                    Column {
                                        Text("পণ্য সংখ্যা", fontSize = 10.sp, color = GoNagalTextMuted)
                                        Text("${sellerProducts.size}+", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = GoNagalNavy)
                                    }
                                    Column {
                                        Text("ইতিবাচক রেটিং", fontSize = 10.sp, color = GoNagalTextMuted)
                                        Text("৯৮.৫%", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = GoNagalGreen)
                                    }
                                    Column {
                                        Text("অন-টাইম শিপমেন্ট", fontSize = 10.sp, color = GoNagalTextMuted)
                                        Text("৯৯.২%", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = GoNagalNavy)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
