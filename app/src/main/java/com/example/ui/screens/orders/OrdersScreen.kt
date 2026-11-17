package com.example.ui.screens.orders

import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.OrderItem
import com.example.model.OrdersRepository
import com.example.ui.theme.GoNagalBackground
import com.example.ui.theme.GoNagalGreen
import com.example.ui.theme.GoNagalNavy
import com.example.ui.theme.GoNagalOrange
import com.example.ui.theme.GoNagalTextMuted
import com.example.ui.theme.GoNagalTextSecondary

enum class OrderFilterTab(val titleBangla: String, val titleEnglish: String) {
    ALL("সব অর্ডার", "All Orders"),
    PENDING("পেন্ডিং", "Pending"),
    PROCESSING("প্রসেসিং", "Processing"),
    SHIPPED("অন দ্য ওয়ে", "Shipped"),
    DELIVERED("ডেলিভার্ড", "Delivered"),
    CANCELLED("বাতিল", "Cancelled"),
    RETURN_REFUND("রিটার্ন/রিফান্ড", "Return/Refund")
}

@Composable
fun OrdersScreen(
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val allOrders = remember { OrdersRepository.orderList }
    var selectedTab by remember { mutableStateOf(OrderFilterTab.ALL) }

    var trackingOrder by remember { mutableStateOf<OrderItem?>(null) }
    var invoiceOrder by remember { mutableStateOf<OrderItem?>(null) }

    val filteredOrders = allOrders.filter { order ->
        when (selectedTab) {
            OrderFilterTab.ALL -> true
            OrderFilterTab.PENDING -> order.status.contains("Pending", ignoreCase = true) || order.status.contains("Placed", ignoreCase = true)
            OrderFilterTab.PROCESSING -> order.status.contains("Processing", ignoreCase = true) || order.status.contains("Hub", ignoreCase = true)
            OrderFilterTab.SHIPPED -> order.status.contains("Delivery", ignoreCase = true) || order.status.contains("Shipped", ignoreCase = true)
            OrderFilterTab.DELIVERED -> order.status.contains("Delivered", ignoreCase = true)
            OrderFilterTab.CANCELLED -> order.status.contains("Cancel", ignoreCase = true)
            OrderFilterTab.RETURN_REFUND -> order.status.contains("Refund", ignoreCase = true) || order.status.contains("Return", ignoreCase = true)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GoNagalBackground)
    ) {
        // Header
        Surface(color = Color.White, shadowElevation = 2.dp) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (onBack != null) {
                            IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = GoNagalNavy
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                        Column {
                            Text(
                                text = "আমার অর্ডারসমূহ (${allOrders.size})",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoNagalNavy
                            )
                            Text(
                                text = "লাইভ ট্র্যাকিং, চালান ও অর্ডারের সর্বশেষ অবস্থা",
                                fontSize = 10.5.sp,
                                color = GoNagalTextMuted
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFFF0FDF4)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = GoNagalGreen,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "নিরাপদ ডেলিভারি",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoNagalGreen
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Order Status Tabs Carousel
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(OrderFilterTab.values()) { tab ->
                        val isSelected = selectedTab == tab
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedTab = tab },
                            label = {
                                Text(
                                    text = tab.titleBangla,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GoNagalNavy,
                                selectedLabelColor = Color.White,
                                containerColor = Color(0xFFF1F5F9),
                                labelColor = GoNagalNavy
                            )
                        )
                    }
                }
            }
        }

        if (filteredOrders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📦", fontSize = 56.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "এই ট্যাবে কোনো অর্ডার নেই",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoNagalNavy
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "অন্য ট্যাব পরীক্ষা করুন অথবা নতুন শপিং করুন",
                        fontSize = 12.sp,
                        color = GoNagalTextMuted
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredOrders) { order ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Order #${order.orderId}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoNagalNavy
                                )
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = order.statusColor.copy(alpha = 0.12f)
                                ) {
                                    Text(
                                        text = order.status,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = order.statusColor,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "তারিখ: ${order.date}",
                                    fontSize = 11.sp,
                                    color = GoNagalTextMuted
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "• ${order.destination}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = GoNagalGreen
                                )
                            }

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 10.dp),
                                color = Color(0xFFF1F5F9)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFF8FAFC),
                                    modifier = Modifier.size(52.dp)
                                ) {
                                    if (order.iconEmoji.startsWith("http")) {
                                        AsyncImage(
                                            model = ImageRequest.Builder(LocalContext.current)
                                                .data(order.iconEmoji)
                                                .crossfade(true)
                                                .build(),
                                            contentDescription = order.productName,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    } else {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(text = order.iconEmoji, fontSize = 28.sp)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = order.productName,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = GoNagalNavy,
                                        maxLines = 2
                                    )
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = "${order.deliveryType} • ${order.paymentMethod}",
                                        fontSize = 10.5.sp,
                                        color = GoNagalTextMuted
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = order.totalAmount,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = GoNagalGreen
                                    )
                                    Text(
                                        text = "${order.itemCount} টি পণ্য",
                                        fontSize = 10.sp,
                                        color = GoNagalTextMuted
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Action buttons: Live Track & Invoice / Details
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { trackingOrder = order },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(36.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.LocalShipping,
                                            contentDescription = null,
                                            tint = GoNagalNavy,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "লাইভ ট্র্যাকিং",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = GoNagalNavy
                                        )
                                    }
                                }

                                Button(
                                    onClick = { invoiceOrder = order },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(36.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F5F9)),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Receipt,
                                            contentDescription = null,
                                            tint = GoNagalNavy,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "চালান ও বিবরণ",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = GoNagalNavy
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Live Tracking Dialog
    if (trackingOrder != null) {
        val order = trackingOrder!!
        AlertDialog(
            onDismissRequest = { trackingOrder = null },
            confirmButton = {
                Button(
                    onClick = { trackingOrder = null },
                    colors = ButtonDefaults.buttonColors(containerColor = GoNagalGreen),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("ঠিক আছে", fontWeight = FontWeight.Bold)
                }
            },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("অর্ডার ট্র্যাকিং (#${order.orderId})", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = GoNagalNavy)
                    IconButton(onClick = { trackingOrder = null }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = GoNagalTextMuted)
                    }
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "আনুমানিক ডেলিভারি: আজ রাত ৮টা নাগাদ", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoNagalGreen)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Timeline steps
                    val steps = listOf(
                        Triple("অর্ডার প্লেস করা হয়েছে", "পেমেন্ট নিশ্চিত ও অর্ডার ভেরিফাইড", true),
                        Triple("সেন্ট্রাল হাবে প্রসেসিং", "প্যাকিং ও কোয়ালিটি চেক সম্পন্ন", true),
                        Triple("রাইডারের কাছে হস্তান্তরিত", "GoNagal এক্সপ্রেস ডেলিভারি ভ্যান রওনা হয়েছে", true),
                        Triple("ডেলিভারির পথে (Out for Delivery)", "আপনার ঠিকানার কাছাকাছি", order.status.contains("Delivery") || order.status.contains("Delivered")),
                        Triple("ডেলিভারি সম্পন্ন", "গ্রাহকের কাছে সফলভাবে অর্পণ", order.status.contains("Delivered"))
                    )

                    steps.forEachIndexed { idx, (stepTitle, stepDesc, isDone) ->
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Surface(
                                    shape = CircleShape,
                                    color = if (isDone) GoNagalGreen else Color(0xFFCBD5E1),
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(text = "${idx + 1}", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                if (idx < steps.size - 1) {
                                    Box(
                                        modifier = Modifier
                                            .width(2.dp)
                                            .height(30.dp)
                                            .background(if (isDone) GoNagalGreen else Color(0xFFCBD5E1))
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = stepTitle,
                                    fontSize = 12.sp,
                                    fontWeight = if (isDone) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isDone) GoNagalNavy else GoNagalTextMuted
                                )
                                Text(text = stepDesc, fontSize = 9.5.sp, color = GoNagalTextMuted)
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                        }
                    }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = GoNagalGreen, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("ডেলিভারি রাইডার: রাসেল মাহমুদ", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoNagalNavy)
                                Text("হটলাইন: 09612-GONAGAL", fontSize = 10.sp, color = GoNagalGreen)
                            }
                        }
                    }
                }
            }
        )
    }

    // Invoice & Details Dialog
    if (invoiceOrder != null) {
        val order = invoiceOrder!!
        AlertDialog(
            onDismissRequest = { invoiceOrder = null },
            confirmButton = {
                Button(
                    onClick = { invoiceOrder = null },
                    colors = ButtonDefaults.buttonColors(containerColor = GoNagalNavy),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("বন্ধ করুন", fontWeight = FontWeight.Bold)
                }
            },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Description, contentDescription = null, tint = GoNagalGreen, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("ডিজিটাল চালান (Invoice)", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = GoNagalNavy)
                    }
                    IconButton(onClick = { invoiceOrder = null }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = GoNagalTextMuted)
                    }
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("GoNagal Bangladesh Ltd.", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoNagalGreen)
                                Text("অর্ডার: #${order.orderId}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoNagalNavy)
                            }
                            Text("তারিখ: ${order.date}", fontSize = 10.sp, color = GoNagalTextMuted)
                            Text("ডেলিভারি ঠিকানা: ${order.destination}", fontSize = 10.sp, color = GoNagalTextMuted)
                            Text("পেমেন্ট পদ্ধতি: ${order.paymentMethod}", fontSize = 10.sp, color = GoNagalTextMuted)

                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFCBD5E1))

                            Text("আইটেম: ${order.productName}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = GoNagalNavy)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("পরিমাণ: ${order.itemCount} টি", fontSize = 10.sp, color = GoNagalTextMuted)
                                Text("স্ট্যাটাস: ${order.status}", fontSize = 10.sp, color = order.statusColor, fontWeight = FontWeight.Bold)
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFCBD5E1))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("সর্বমোট প্রদেয় অর্থ:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoNagalNavy)
                                Text(order.totalAmount, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = GoNagalGreen)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "এটি একটি কম্পিউটার-জেনারেটেড অফিশিয়াল GoNagal ইনভয়েস। ভ্যাট ও ট্যাক্স অন্তর্ভুক্ত।",
                        fontSize = 9.sp,
                        color = GoNagalTextMuted,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        )
    }
}
