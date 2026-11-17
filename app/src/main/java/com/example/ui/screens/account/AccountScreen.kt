package com.example.ui.screens.account

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.GuestStorage
import com.example.model.MockMarketplaceData
import com.example.model.OrderItem
import com.example.model.OrdersRepository
import com.example.model.WishlistRepository
import com.example.ui.theme.GoNagalBackground
import com.example.ui.theme.GoNagalGreen
import com.example.ui.theme.GoNagalNavy
import com.example.ui.theme.GoNagalOrange
import com.example.ui.theme.GoNagalTextMuted
import com.example.viewmodel.AuthViewModel

enum class BuyerOrderTab(val title: String) {
    ACTIVE("চলমান অর্ডার (Active)"),
    DELIVERED("পূর্ববর্তী অর্ডার (Past)")
}

@Composable
fun AccountScreen(
    authViewModel: AuthViewModel,
    onLogout: () -> Unit,
    onNavigateToOrders: () -> Unit = {},
    onNavigateToWishlist: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Guest identity and safe device identifier
    val guestId = remember { GuestStorage.getOrCreateGuestId(context) }
    var customerName by remember {
        mutableStateOf(GuestStorage.getLastCustomerName(context).ifEmpty { "গেস্ট ক্রেতা (Guest Shopper)" })
    }
    var customerPhone by remember {
        mutableStateOf(GuestStorage.getLastCustomerPhone(context).ifEmpty { "ফোন নম্বর যুক্ত করুন" })
    }
    var customerAddress by remember {
        mutableStateOf(GuestStorage.getLastCustomerAddress(context).ifEmpty { "ডেলিভারি ঠিকানা যুক্ত করুন" })
    }
    var customerDistrict by remember {
        mutableStateOf(GuestStorage.getLastCustomerDistrict(context))
    }

    // Orders tab filter
    var selectedOrderTab by remember { mutableStateOf(BuyerOrderTab.ACTIVE) }

    // Dialog states
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showAddressDialog by remember { mutableStateOf(false) }
    var showSupportDialog by remember { mutableStateOf(false) }
    var showFaqDialog by remember { mutableStateOf(false) }
    var selectedOrderForDetails by remember { mutableStateOf<OrderItem?>(null) }
    var selectedOrderForTracking by remember { mutableStateOf<OrderItem?>(null) }
    var selectedOrderForCancel by remember { mutableStateOf<OrderItem?>(null) }

    // Settings
    var notificationsEnabled by remember { mutableStateOf(true) }
    var selectedLanguage by remember { mutableStateOf("বাংলা (Bangla)") }

    // Isolated buyer orders from repository
    val allBuyerOrders = remember(OrdersRepository.orderList.size) {
        OrdersRepository.getOrdersForGuest(guestId)
    }

    val activeOrders = allBuyerOrders.filter { !it.status.contains("Delivered", ignoreCase = true) && !it.isCancelled }
    val deliveredOrders = allBuyerOrders.filter { it.status.contains("Delivered", ignoreCase = true) || it.isCancelled }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GoNagalBackground)
            .verticalScroll(scrollState)
    ) {
        // ==========================================
        // 1. BUYER PROFILE HEADER CARD
        // ==========================================
        Surface(
            color = GoNagalNavy,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color.White,
                        modifier = Modifier.size(54.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("🛍️", fontSize = 26.sp)
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = customerName,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Profile",
                                tint = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier
                                    .size(16.dp)
                                    .clickable { showEditProfileDialog = true }
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "📞 $customerPhone",
                            fontSize = 11.5.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "ডিভাইস আইডি: $guestId",
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFFBBF7D0),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Buyer Protection Guarantee
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.10f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🛡️", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "১০০% নিরাপদ বায়ার অ্যাকাউন্ট",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "লগইন বা পাসওয়ার্ডের ঝামেলা নেই। ক্যাশ অন ডেলিভারিতে সরাসরি অর্ডার ও ট্র্যাক করুন।",
                                fontSize = 10.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        }

        // ==========================================
        // 2. QUICK ACCESS 4-STAT GRID
        // ==========================================
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // My Orders
            Surface(
                onClick = onNavigateToOrders,
                shape = RoundedCornerShape(10.dp),
                color = Color.White,
                shadowElevation = 1.dp,
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = GoNagalNavy, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("মোট অর্ডার", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoNagalNavy)
                    Text("${allBuyerOrders.size} টি", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GoNagalGreen)
                }
            }

            // Active Orders
            Surface(
                onClick = { selectedOrderTab = BuyerOrderTab.ACTIVE },
                shape = RoundedCornerShape(10.dp),
                color = Color.White,
                shadowElevation = 1.dp,
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.LocalShipping, contentDescription = null, tint = GoNagalOrange, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("চলমান", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoNagalNavy)
                    Text("${activeOrders.size} টি", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GoNagalOrange)
                }
            }

            // Wishlist
            Surface(
                onClick = onNavigateToWishlist,
                shape = RoundedCornerShape(10.dp),
                color = Color.White,
                shadowElevation = 1.dp,
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.Favorite, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("উইশলিস্ট", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoNagalNavy)
                    Text("${WishlistRepository.items.size} টি", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFEF4444))
                }
            }

            // 24/7 Support
            Surface(
                onClick = { showSupportDialog = true },
                shape = RoundedCornerShape(10.dp),
                color = Color.White,
                shadowElevation = 1.dp,
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.HeadsetMic, contentDescription = null, tint = GoNagalGreen, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("সহায়তা", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoNagalNavy)
                    Text("২৪/৭ লাইভ", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GoNagalGreen)
                }
            }
        }

        // ==========================================
        // 3. ORDERS SECTION (ACTIVE & DELIVERED TABS)
        // ==========================================
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📦 আমার অর্ডারসমূহ",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoNagalNavy
                )
                Text(
                    text = "সব অর্ডার দেখুন →",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoNagalGreen,
                    modifier = Modifier.clickable { onNavigateToOrders() }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Tab Selector: Active vs Past Orders
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
                    .padding(3.dp)
            ) {
                BuyerOrderTab.values().forEach { tab ->
                    val isSelected = selectedOrderTab == tab
                    Surface(
                        onClick = { selectedOrderTab = tab },
                        shape = RoundedCornerShape(6.dp),
                        color = if (isSelected) Color.White else Color.Transparent,
                        shadowElevation = if (isSelected) 1.dp else 0.dp,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = tab.title,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) GoNagalNavy else GoNagalTextMuted,
                            modifier = Modifier.padding(vertical = 6.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Display Orders List for selected tab
            val currentList = if (selectedOrderTab == BuyerOrderTab.ACTIVE) activeOrders else deliveredOrders
            if (currentList.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(if (selectedOrderTab == BuyerOrderTab.ACTIVE) "📦" else "✅", fontSize = 28.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (selectedOrderTab == BuyerOrderTab.ACTIVE) "কোনো চলমান অর্ডার নেই" else "কোনো পূর্ববর্তী অর্ডার নেই",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoNagalNavy
                        )
                        Text(
                            text = "আপনার প্রয়োজনীয় পণ্য সরাসরি অর্ডার করুন।",
                            fontSize = 11.sp,
                            color = GoNagalTextMuted
                        )
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    currentList.take(3).forEach { order ->
                        BuyerOrderCard(
                            order = order,
                            onViewDetails = { selectedOrderForDetails = order },
                            onTrackOrder = { selectedOrderForTracking = order },
                            onCancelOrder = { selectedOrderForCancel = order }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // ==========================================
        // 4. SAVED DELIVERY ADDRESS CARD
        // ==========================================
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = GoNagalGreen, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "সংরক্ষিত ডেলিভারি ঠিকানা",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoNagalNavy
                            )
                        }

                        Text(
                            text = "পরিবর্তন করুন",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoNagalGreen,
                            modifier = Modifier.clickable { showAddressDialog = true }
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "👤 প্রাপক: $customerName",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = GoNagalNavy
                    )
                    Text(
                        text = "📞 মোবাইল: $customerPhone",
                        fontSize = 11.sp,
                        color = Color(0xFF475569)
                    )
                    Text(
                        text = "📍 ঠিকানা: $customerAddress, $customerDistrict",
                        fontSize = 11.sp,
                        color = Color(0xFF475569),
                        lineHeight = 15.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // ==========================================
        // 5. RECENTLY VIEWED / RECOMMENDED
        // ==========================================
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.History, contentDescription = null, tint = GoNagalNavy, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("সম্প্রতি দেখা পণ্যসমূহ", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = GoNagalNavy)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(MockMarketplaceData.allCatalogProducts.take(6)) { prod ->
                    Card(
                        modifier = Modifier
                            .width(130.dp)
                            .clickable { },
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(prod.imageUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = prod.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(90.dp)
                                    .clip(RoundedCornerShape(6.dp))
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = prod.title,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoNagalNavy,
                                maxLines = 1
                            )
                            Text(
                                text = prod.formattedSellingPrice,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = GoNagalGreen
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // ==========================================
        // 6. CUSTOMER CARE & HELP
        // ==========================================
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)) {
                    BuyerActionRow(
                        icon = Icons.Default.HeadsetMic,
                        iconTint = GoNagalGreen,
                        title = "গ্রাহক সহায়তা ও কাস্টমার কেয়ার",
                        subtitle = "WhatsApp ও সরাসরি হটলাইন সাপোর্ট",
                        onClick = { showSupportDialog = true }
                    )
                    HorizontalDivider(color = Color(0xFFF1F5F9))
                    BuyerActionRow(
                        icon = Icons.Default.HelpOutline,
                        iconTint = GoNagalOrange,
                        title = "সাধারণ জিজ্ঞাসা ও নিয়মাবলী (FAQ)",
                        subtitle = "ডেলিভারি সময়, রিটার্ন ও ক্যাশ অন ডেলিভারি তথ্য",
                        onClick = { showFaqDialog = true }
                    )
                    HorizontalDivider(color = Color(0xFFF1F5F9))
                    BuyerActionRow(
                        icon = Icons.Default.Security,
                        iconTint = GoNagalNavy,
                        title = "নিরাপত্তা ও গোপনীয়তা নীতি",
                        subtitle = "ক্রেতার তথ্য ও ডিভাইস-সেফ সুরক্ষার বিবরণ",
                        onClick = { }
                    )
                }
            }

            // ==========================================
            // 7. APP SETTINGS & PREFERENCES
            // ==========================================
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("অ্যাপ সেটিংস", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoNagalNavy)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("পুশ নোটিফিকেশন", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = GoNagalNavy)
                            Text("অর্ডার আপডেট ও ডিসকাউন্ট বার্তা", fontSize = 10.sp, color = GoNagalTextMuted)
                        }
                        Switch(
                            checked = notificationsEnabled,
                            onCheckedChange = { notificationsEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = GoNagalGreen)
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFF1F5F9))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("ভাষা (Language)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = GoNagalNavy)
                            Text(selectedLanguage, fontSize = 10.sp, color = GoNagalGreen, fontWeight = FontWeight.Bold)
                        }
                        Surface(
                            onClick = {
                                selectedLanguage = if (selectedLanguage.startsWith("বাংলা")) "English" else "বাংলা (Bangla)"
                            },
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFF1F5F9)
                        ) {
                            Text("বদলান", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoNagalNavy, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "GoNagal Android App v2.4.0 • ক্যাশ অন ডেলিভারি মার্কেটপ্লেস",
                        fontSize = 9.5.sp,
                        color = GoNagalTextMuted,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    // ==========================================
    // DIALOGS & MODALS
    // ==========================================

    // 1. Edit Profile Information Dialog
    if (showEditProfileDialog) {
        var tempName by remember { mutableStateOf(customerName) }
        var tempPhone by remember { mutableStateOf(customerPhone) }
        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = { Text("বায়ার তথ্য আপডেট করুন", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = GoNagalNavy) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = tempName,
                        onValueChange = { tempName = it },
                        label = { Text("আপনার নাম") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = tempPhone,
                        onValueChange = { tempPhone = it },
                        label = { Text("মোবাইল নম্বর") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        customerName = tempName.trim().ifEmpty { "গেস্ট ক্রেতা" }
                        customerPhone = tempPhone.trim().ifEmpty { "017XXXXXXXX" }
                        GuestStorage.saveLastCustomerInfo(
                            context = context,
                            name = customerName,
                            phone = customerPhone,
                            address = customerAddress,
                            district = customerDistrict
                        )
                        showEditProfileDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoNagalGreen)
                ) {
                    Text("সংরক্ষণ করুন")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showEditProfileDialog = false }) {
                    Text("বাতিল")
                }
            }
        )
    }

    // 2. Saved Address Dialog
    if (showAddressDialog) {
        var tempAddr by remember { mutableStateOf(customerAddress) }
        var tempDist by remember { mutableStateOf(customerDistrict) }
        AlertDialog(
            onDismissRequest = { showAddressDialog = false },
            title = { Text("সংরক্ষিত ডেলিভারি ঠিকানা", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = GoNagalNavy) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = tempDist,
                        onValueChange = { tempDist = it },
                        label = { Text("জেলা") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = tempAddr,
                        onValueChange = { tempAddr = it },
                        label = { Text("বিস্তারিত ঠিকানা (বাড়ি, রোড, এলাকা)") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        customerAddress = tempAddr.trim().ifEmpty { "ঢাকা" }
                        customerDistrict = tempDist.trim().ifEmpty { "ঢাকা" }
                        GuestStorage.saveLastCustomerInfo(
                            context = context,
                            name = customerName,
                            phone = customerPhone,
                            address = customerAddress,
                            district = customerDistrict
                        )
                        showAddressDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoNagalGreen)
                ) {
                    Text("ঠিকানা আপডেট করুন")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showAddressDialog = false }) {
                    Text("বাতিল")
                }
            }
        )
    }

    // 3. Customer Care & Support Dialog
    if (showSupportDialog) {
        AlertDialog(
            onDismissRequest = { showSupportDialog = false },
            confirmButton = {
                Button(
                    onClick = { showSupportDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = GoNagalNavy)
                ) {
                    Text("বন্ধ করুন")
                }
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.HeadsetMic, contentDescription = null, tint = GoNagalGreen, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("GoNagal কাস্টমার সাপোর্ট", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = GoNagalNavy)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("আমাদের কাস্টমার কেয়ার প্রতিনিধি সকাল ৮টা থেকে রাত ১১টা পর্যন্ত আপনার সেবায় নিয়োজিত।", fontSize = 11.5.sp, color = GoNagalNavy)

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF0FDF4),
                        border = BorderStroke(1.dp, Color(0xFFBBF7D0)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                Toast.makeText(context, "WhatsApp সাপোর্ট: 01711223344", Toast.LENGTH_SHORT).show()
                            }
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("💬", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("হোয়াটসঅ্যাপে মেসেজ পাঠান", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoNagalGreen)
                                Text("+880 1711-223344", fontSize = 11.sp, color = GoNagalNavy)
                            }
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF8FAFC),
                        border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                Toast.makeText(context, "হটলাইন: 09612-GONAGAL", Toast.LENGTH_SHORT).show()
                            }
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("📞", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("হটলাইন কল করুন (টোল ফ্রি)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoNagalNavy)
                                Text("09612-GONAGAL", fontSize = 11.sp, color = GoNagalTextMuted)
                            }
                        }
                    }
                }
            }
        )
    }

    // 4. FAQ Dialog
    if (showFaqDialog) {
        AlertDialog(
            onDismissRequest = { showFaqDialog = false },
            confirmButton = {
                Button(
                    onClick = { showFaqDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = GoNagalNavy)
                ) {
                    Text("ঠিক আছে")
                }
            },
            title = {
                Text("সাধারণ জিজ্ঞাসা (FAQ)", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = GoNagalNavy)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                        "ডেলিভারি পেতে কতদিন সময় লাগে?" to "ঢাকা সিটিতে ২৪ থেকে ৪৮ ঘণ্টা এবং ঢাকার বাইরে ২ থেকে ৩ কার্যদিবস।",
                        "ক্যাশ অন ডেলিভারি কীভাবে কাজ করে?" to "পণ্য হাতে পেয়ে চেক করে ডেলিভারি ম্যানের কাছে টাকা পরিশোধ করবেন। কোনো অগ্রিম পেমেন্ট নেই।",
                        "অর্ডার বাতিল বা পরিবর্তন করতে চাইলে?" to "অর্ডার প্রসেসিংয়ে থাকা অবস্থায় এই প্রোফাইল থেকেই 'বাতিল করুন' বাটনে চাপ দিয়ে কারণ উল্লেখ করতে পারবেন।"
                    ).forEach { (q, a) ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text("❓ $q", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = GoNagalNavy)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(a, fontSize = 10.5.sp, color = Color(0xFF475569))
                            }
                        }
                    }
                }
            }
        )
    }

    // 5. Order Details Modal
    selectedOrderForDetails?.let { order ->
        AlertDialog(
            onDismissRequest = { selectedOrderForDetails = null },
            confirmButton = {
                Button(
                    onClick = { selectedOrderForDetails = null },
                    colors = ButtonDefaults.buttonColors(containerColor = GoNagalNavy)
                ) {
                    Text("বন্ধ করুন")
                }
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = GoNagalGreen, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("অর্ডারের বিস্তারিত: #${order.orderId}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = GoNagalNavy)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(
                        color = Color(0xFFF0FDF4),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("স্ট্যাটাস: ${order.status}", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = order.statusColor)
                            Text(order.date, fontSize = 10.sp, color = GoNagalTextMuted)
                        }
                    }
                    Text("📦 পণ্য: ${order.productName}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoNagalNavy)
                    Text("👤 গ্রাহকের নাম: ${order.customerName}", fontSize = 11.sp, color = GoNagalNavy)
                    Text("📞 মোবাইল: ${order.customerPhone}", fontSize = 11.sp, color = GoNagalNavy)
                    Text("📍 ডেলিভারি ঠিকানা: ${order.destination}", fontSize = 11.sp, color = Color(0xFF475569))
                    Text("💵 পেমেন্ট মাধ্যম: ${order.paymentMethod}", fontSize = 11.sp, color = GoNagalGreen, fontWeight = FontWeight.SemiBold)
                    HorizontalDivider(color = Color(0xFFE2E8F0))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("সর্বমোট মূল্য (COD):", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoNagalNavy)
                        Text(order.totalAmount, fontSize = 14.sp, fontWeight = FontWeight.Black, color = GoNagalGreen)
                    }
                    if (order.isCancelled && order.cancelReason != null) {
                        Text("বাতিলের কারণ: ${order.cancelReason}", fontSize = 10.5.sp, color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)
                    }
                }
            }
        )
    }

    // 6. Order Tracking Timeline Modal
    selectedOrderForTracking?.let { order ->
        AlertDialog(
            onDismissRequest = { selectedOrderForTracking = null },
            confirmButton = {
                Button(
                    onClick = { selectedOrderForTracking = null },
                    colors = ButtonDefaults.buttonColors(containerColor = GoNagalGreen)
                ) {
                    Text("ঠিক আছে")
                }
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocalShipping, contentDescription = null, tint = GoNagalGreen, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("অর্ডার ট্র্যাকিং: #${order.orderId}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = GoNagalNavy)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val steps = listOf(
                        "১. অর্ডার প্লেসড" to "অর্ডার সফলভাবে গ্রহণ করা হয়েছে।",
                        "২. হাব প্রসেসিং" to "পণ্য প্যাকেজিং ও গুণগত মান পরীক্ষা হচ্ছে।",
                        "৩. ডেলিভারির পথে" to "কুরিয়ার রাইডারের কাছে হস্তান্তর করা হয়েছে।",
                        "৪. ডেলিভার্ড" to "গ্রাহকের ঠিকানায় পৌঁছে মূল্য গ্রহণ করা হবে।"
                    )
                    steps.forEachIndexed { index, (stepTitle, stepDesc) ->
                        val isDone = when (index) {
                            0 -> true
                            1 -> order.status.contains("Processing", ignoreCase = true) || order.status.contains("Delivery", ignoreCase = true) || order.status.contains("Delivered", ignoreCase = true)
                            2 -> order.status.contains("Delivery", ignoreCase = true) || order.status.contains("Delivered", ignoreCase = true)
                            3 -> order.status.contains("Delivered", ignoreCase = true)
                            else -> false
                        }
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                imageVector = if (isDone) Icons.Default.CheckCircle else Icons.Default.Info,
                                contentDescription = null,
                                tint = if (isDone) GoNagalGreen else Color(0xFFCBD5E1),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(stepTitle, fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = if (isDone) GoNagalNavy else GoNagalTextMuted)
                                Text(stepDesc, fontSize = 10.sp, color = GoNagalTextMuted)
                            }
                        }
                    }
                }
            }
        )
    }

    // 7. Cancel Order Dialog
    selectedOrderForCancel?.let { order ->
        var cancelReasonInput by remember { mutableStateOf("ভুল পণ্য অর্ডার করেছি") }
        val cancelReasons = listOf(
            "ভুল পণ্য অর্ডার করেছি",
            "ডেলিভারি ঠিকানা পরিবর্তন করতে হবে",
            "অন্য সময় কিনব",
            "ডেলিভারি দেরিতে হচ্ছে"
        )
        AlertDialog(
            onDismissRequest = { selectedOrderForCancel = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Cancel, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("অর্ডার বাতিলের অনুরোধ", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = GoNagalNavy)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("অর্ডার #${order.orderId} বাতিল করার কারণ নির্বাচন করুন:", fontSize = 11.5.sp, color = GoNagalNavy)
                    cancelReasons.forEach { reason ->
                        val isSelected = cancelReasonInput == reason
                        Surface(
                            onClick = { cancelReasonInput = reason },
                            shape = RoundedCornerShape(6.dp),
                            color = if (isSelected) Color(0xFFFEE2E2) else Color(0xFFF8FAFC),
                            border = BorderStroke(1.dp, if (isSelected) Color(0xFFEF4444) else Color(0xFFCBD5E1)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = reason,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color(0xFFEF4444) else GoNagalNavy,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        OrdersRepository.cancelOrder(order.orderId, cancelReasonInput)
                        Toast.makeText(context, "অর্ডার #${order.orderId} বাতিল করা হয়েছে।", Toast.LENGTH_SHORT).show()
                        selectedOrderForCancel = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text("অর্ডার বাতিল নিশ্চিত করুন", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { selectedOrderForCancel = null }) {
                    Text("ফিরে যান")
                }
            }
        )
    }
}

@Composable
private fun BuyerOrderCard(
    order: OrderItem,
    onViewDetails: () -> Unit,
    onTrackOrder: () -> Unit,
    onCancelOrder: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "#${order.orderId}",
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Black,
                    color = GoNagalNavy
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = order.statusColor.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = order.status,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = order.statusColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = order.productName,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = GoNagalNavy,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(2.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "মোট বিল: ${order.totalAmount}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoNagalGreen
                )
                Text(
                    text = order.date,
                    fontSize = 10.sp,
                    color = GoNagalTextMuted
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                OutlinedButton(
                    onClick = onViewDetails,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(34.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Text("বিস্তারিত", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = GoNagalNavy)
                }

                Button(
                    onClick = onTrackOrder,
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GoNagalGreen),
                    modifier = Modifier
                        .weight(1f)
                        .height(34.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Text("ট্র্যাকিং", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                if (!order.isCancelled && !order.status.contains("Delivered", ignoreCase = true)) {
                    OutlinedButton(
                        onClick = onCancelOrder,
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp),
                        border = BorderStroke(1.dp, Color(0xFFEF4444)),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text("বাতিল", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFFEF4444))
                    }
                }
            }
        }
    }
}

@Composable
private fun BuyerActionRow(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = iconTint.copy(alpha = 0.12f),
                modifier = Modifier.size(34.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(18.dp))
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(title, fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = GoNagalNavy)
                Text(subtitle, fontSize = 10.sp, color = GoNagalTextMuted)
            }
        }
        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = GoNagalTextMuted, modifier = Modifier.size(16.dp))
    }
}
