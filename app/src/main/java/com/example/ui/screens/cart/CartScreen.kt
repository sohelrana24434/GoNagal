package com.example.ui.screens.cart

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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CartRepository
import com.example.model.OrdersRepository
import com.example.model.PaymentMethodType
import com.example.model.Product
import com.example.model.UserCartItem
import com.example.ui.components.CartCheckoutBottomSheet
import com.example.ui.theme.GoNagalBackground
import com.example.ui.theme.GoNagalGreen
import com.example.ui.theme.GoNagalNavy
import com.example.ui.theme.GoNagalOrange
import com.example.ui.theme.GoNagalTextMuted
import com.example.ui.theme.GoNagalTextPrimary
import com.example.ui.theme.GoNagalTextSecondary

@Composable
fun CartScreen(
    onOrderPlacedNavToOrders: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cartItems = remember { mutableStateListOf<UserCartItem>().apply { addAll(CartRepository.items) } }
    var showCheckoutSheet by remember { mutableStateOf(false) }

    // Coupon states
    var couponInput by remember { mutableStateOf("") }
    var appliedCoupon by remember { mutableStateOf("") }
    var couponDiscount by remember { mutableDoubleStateOf(0.0) }
    var couponMessage by remember { mutableStateOf("") }

    val subtotal = cartItems.filter { it.isSelected }.sumOf { it.product.sellingPrice * it.quantity }
    val deliveryFee = if (subtotal > 0) 60.0 else 0.0
    val total = maxOf(0.0, subtotal - couponDiscount + deliveryFee)

    val allSelected = cartItems.isNotEmpty() && cartItems.all { it.isSelected }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GoNagalBackground)
    ) {
        // Header
        Surface(
            color = Color.White,
            shadowElevation = 2.dp
        ) {
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
                    Column {
                        Text(
                            text = "Shopping Cart (${cartItems.size})",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoNagalNavy
                        )
                        Text(
                            text = "পণ্য নির্বাচন করুন ও সহজে অর্ডার দিন",
                            fontSize = 11.sp,
                            color = GoNagalTextMuted
                        )
                    }

                    if (cartItems.isNotEmpty()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = allSelected,
                                onCheckedChange = { checked ->
                                    cartItems.forEachIndexed { i, item ->
                                        cartItems[i] = item.copy(isSelected = checked)
                                    }
                                },
                                colors = CheckboxDefaults.colors(checkedColor = GoNagalGreen)
                            )
                            Text(
                                text = "সব নির্বাচন",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = GoNagalNavy
                            )
                        }
                    }
                }
            }
        }

        if (cartItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🛒", fontSize = 56.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "আপনার কার্ট সম্পূর্ণ খালি",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoNagalNavy
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "আমাদের সেরা ডিল ও ডিসকাউন্ট থেকে পছন্দের পণ্য যোগ করুন",
                        fontSize = 12.sp,
                        color = GoNagalTextMuted
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                itemsIndexed(cartItems) { index, item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = item.isSelected,
                                    onCheckedChange = { checked ->
                                        cartItems[index] = item.copy(isSelected = checked)
                                    },
                                    colors = CheckboxDefaults.colors(checkedColor = GoNagalGreen)
                                )

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFF8FAFC),
                                    modifier = Modifier.size(54.dp)
                                ) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(item.product.imageUrl.ifEmpty { "https://images.unsplash.com/photo-1523381210434-271e8be1f52b?w=200&auto=format&fit=crop&q=80" })
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = item.product.title,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.product.title,
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = GoNagalNavy,
                                        maxLines = 2
                                    )
                                    if (!item.selectedColor.isNullOrBlank() || !item.selectedSize.isNullOrBlank()) {
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = listOfNotNull(item.selectedColor, item.selectedSize).joinToString(" • "),
                                            fontSize = 10.sp,
                                            color = GoNagalGreen,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.Bottom) {
                                        Text(
                                            text = item.product.formattedSellingPrice,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GoNagalGreen
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        if (item.product.originalPrice > item.product.sellingPrice) {
                                            Text(
                                                text = item.product.formattedOriginalPrice,
                                                fontSize = 10.sp,
                                                color = GoNagalTextMuted,
                                                textDecoration = TextDecoration.LineThrough
                                            )
                                        }
                                    }
                                }

                                IconButton(
                                    onClick = {
                                        CartRepository.removeAt(index)
                                        cartItems.removeAt(index)
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Remove",
                                        tint = Color(0xFFEF4444),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 8.dp),
                                color = Color(0xFFF1F5F9)
                            )

                            // Quantity Stepper Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "সেলার: ${item.product.sellerName}",
                                    fontSize = 10.sp,
                                    color = GoNagalTextMuted
                                )

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFF1F5F9)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        IconButton(
                                            onClick = {
                                                if (item.quantity > 1) {
                                                    item.quantity--
                                                    cartItems[index] = item.copy(quantity = item.quantity)
                                                }
                                            },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(imageVector = Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(14.dp))
                                        }
                                        Text(
                                            text = "${item.quantity}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GoNagalNavy,
                                            modifier = Modifier.padding(horizontal = 8.dp)
                                        )
                                        IconButton(
                                            onClick = {
                                                item.quantity++
                                                cartItems[index] = item.copy(quantity = item.quantity)
                                            },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(imageVector = Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(14.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Coupon Code Section in Cart
                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocalOffer, contentDescription = null, tint = GoNagalOrange, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("কুপন বা ডিসকাউন্ট ভাউচার (Coupon)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoNagalNavy)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                OutlinedTextField(
                                    value = couponInput,
                                    onValueChange = { couponInput = it.uppercase() },
                                    placeholder = { Text("GONAGAL10 / EID2026", fontSize = 11.sp) },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        val code = couponInput.trim().uppercase()
                                        when (code) {
                                            "GONAGAL10" -> {
                                                appliedCoupon = code
                                                couponDiscount = subtotal * 0.10
                                                couponMessage = "১০% ছাড় প্রয়োগ হয়েছে!"
                                            }
                                            "EID2026" -> {
                                                appliedCoupon = code
                                                couponDiscount = 150.0
                                                couponMessage = "ঈদ স্পেশাল ৳১৫০ ছাড় যোগ হয়েছে!"
                                            }
                                            "FREE60" -> {
                                                appliedCoupon = code
                                                couponDiscount = 60.0
                                                couponMessage = "ফ্রি ডেলিভারি ৳৬০ ছাড় যোগ হয়েছে!"
                                            }
                                            else -> {
                                                appliedCoupon = ""
                                                couponDiscount = 0.0
                                                couponMessage = "ভুল কুপন কোড (GONAGAL10 বা EID2026 ব্যবহার করুন)"
                                            }
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = GoNagalNavy),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(50.dp)
                                ) {
                                    Text("Apply", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            if (couponMessage.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = couponMessage,
                                    fontSize = 10.5.sp,
                                    color = if (appliedCoupon.isNotEmpty()) GoNagalGreen else Color(0xFFEF4444),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Price Breakdown Card
                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("অর্ডার সারাংশ (Order Summary)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = GoNagalNavy)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("নির্বাচিত পণ্যের মূল্য:", fontSize = 11.sp, color = GoNagalTextMuted)
                                Text("৳%,.0f".format(subtotal), fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = GoNagalNavy)
                            }
                            if (couponDiscount > 0) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("কুপন ছাড় ($appliedCoupon):", fontSize = 11.sp, color = GoNagalGreen, fontWeight = FontWeight.Bold)
                                    Text("-৳%,.0f".format(couponDiscount), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoNagalGreen)
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("আনুমানিক ডেলিভারি ফি:", fontSize = 11.sp, color = GoNagalTextMuted)
                                Text("৳%,.0f".format(deliveryFee), fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = GoNagalNavy)
                            }
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFF1F5F9))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("সর্বমোট প্রদেয় বিল:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = GoNagalNavy)
                                Text("৳%,.0f".format(total), fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = GoNagalGreen)
                            }
                        }
                    }
                }
            }

            // Checkout Footer
            Surface(
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "সর্বমোট প্রদেয় বিল", fontSize = 11.sp, color = GoNagalTextMuted)
                            Text(
                                text = "৳%,.0f".format(total),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = GoNagalGreen
                            )
                        }

                        val selectedItemsCount = cartItems.filter { it.isSelected }.sumOf { it.quantity }
                        Button(
                            onClick = { showCheckoutSheet = true },
                            enabled = selectedItemsCount > 0,
                            colors = ButtonDefaults.buttonColors(containerColor = GoNagalGreen),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(44.dp)
                        ) {
                            Text(
                                text = "চেকআউট ($selectedItemsCount টি)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }

    // Checkout Bottom Sheet
    if (showCheckoutSheet) {
        val context = androidx.compose.ui.platform.LocalContext.current
        val selectedCount = cartItems.filter { it.isSelected }.sumOf { it.quantity }
        CartCheckoutBottomSheet(
            subtotal = subtotal,
            itemCount = selectedCount,
            onDismiss = { showCheckoutSheet = false },
            onOrderSuccess = { detailedAddress, district, deliveryType, paymentMethod ->
                val primaryTitle = cartItems.filter { it.isSelected }.firstOrNull()?.product?.title ?: "Various Items"
                val guestName = com.example.data.GuestStorage.getLastCustomerName(context).ifEmpty { "সম্মানিত ক্রেতা" }
                val guestPhone = com.example.data.GuestStorage.getLastCustomerPhone(context).ifEmpty { "01712345678" }
                val guestDeviceId = com.example.data.GuestStorage.getOrCreateGuestId(context)
                OrdersRepository.addNewOrder(
                    productName = if (selectedCount > 1) "$primaryTitle + ${selectedCount - 1} more" else primaryTitle,
                    itemCount = selectedCount,
                    totalAmountFormatted = "৳%,.0f".format(total),
                    iconEmoji = cartItems.filter { it.isSelected }.firstOrNull()?.product?.imageEmoji ?: "📦",
                    deliveryType = deliveryType,
                    paymentMethod = "${paymentMethod.banglaTitle} (${paymentMethod.title})",
                    destination = detailedAddress,
                    customerName = guestName,
                    customerPhone = guestPhone,
                    customerAddress = detailedAddress,
                    guestId = guestDeviceId
                )
                // Remove selected items from cart
                val toRemove = cartItems.filter { it.isSelected }
                toRemove.forEach {
                    val idx = CartRepository.items.indexOfFirst { c -> c.product.id == it.product.id }
                    if (idx >= 0) CartRepository.removeAt(idx)
                }
                cartItems.removeAll { it.isSelected }
                onOrderPlacedNavToOrders()
            }
        )
    }
}
