package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GuestStorage
import com.example.model.PaymentMethodType
import com.example.ui.theme.GoNagalGreen
import com.example.ui.theme.GoNagalNavy
import com.example.ui.theme.GoNagalOrange
import com.example.ui.theme.GoNagalTextMuted
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartCheckoutBottomSheet(
    subtotal: Double,
    itemCount: Int,
    onDismiss: () -> Unit,
    onOrderSuccess: (detailedAddress: String, district: String, deliveryType: String, paymentMethod: PaymentMethodType) -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    // Form states
    var customerName by remember {
        mutableStateOf(GuestStorage.getLastCustomerName(context).ifEmpty { "" })
    }
    var customerPhone by remember {
        mutableStateOf(GuestStorage.getLastCustomerPhone(context).ifEmpty { "" })
    }
    var selectedDivision by remember { mutableStateOf("ঢাকা (Dhaka)") }
    var districtInput by remember {
        mutableStateOf(GuestStorage.getLastCustomerDistrict(context).ifEmpty { "ঢাকা (Dhaka)" })
    }
    var upazilaInput by remember { mutableStateOf("") }
    var detailedAddress by remember {
        mutableStateOf(GuestStorage.getLastCustomerAddress(context).ifEmpty { "" })
    }
    var deliveryAreaType by remember { mutableStateOf("ঢাকা সিটি (৳৬০)") }
    var orderNotes by remember { mutableStateOf("") }

    // Coupon code state
    var couponInput by remember { mutableStateOf("GONAGAL10") }
    var appliedCoupon by remember { mutableStateOf("GONAGAL10") }
    var couponDiscount by remember { mutableDoubleStateOf(subtotal * 0.10) }
    var couponMessage by remember { mutableStateOf("১০% কুপন ছাড় সক্রিয় করা হয়েছে!") }

    // Form errors & submit loading
    var errorMessage by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }

    val deliveryFee = if (deliveryAreaType.contains("ঢাকা সিটি")) 60.0 else 100.0
    val totalAmount = maxOf(0.0, subtotal - couponDiscount + deliveryFee)

    val divisions = listOf(
        "ঢাকা (Dhaka)",
        "চট্টগ্রাম (Chattogram)",
        "রাজশাহী (Rajshahi)",
        "খুলনা (Khulna)",
        "বরিশাল (Barishal)",
        "সিলেট (Sylhet)",
        "রংপুর (Rangpur)",
        "ময়মনসিংহ (Mymensingh)"
    )

    // Shared high-contrast text field colors
    val darkFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color(0xFF0F172A),
        unfocusedTextColor = Color(0xFF0F172A),
        focusedContainerColor = Color.White,
        unfocusedContainerColor = Color.White,
        focusedBorderColor = GoNagalGreen,
        unfocusedBorderColor = Color(0xFFCBD5E1),
        focusedLabelColor = GoNagalNavy,
        unfocusedLabelColor = Color(0xFF475569),
        focusedPlaceholderColor = Color(0xFF64748B),
        unfocusedPlaceholderColor = Color(0xFF94A3B8)
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 680.dp)
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "চেকআউট ও ক্যাশ অন ডেলিভারি",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoNagalNavy
                    )
                    Text(
                        text = "$itemCount টি পণ্যের ডেলিভারি তথ্য প্রদান করুন",
                        fontSize = 11.5.sp,
                        color = GoNagalTextMuted
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = GoNagalTextMuted)
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFE2E8F0))

            // 1. Customer Personal Information
            Text(
                text = "১. গ্রাহকের নাম ও মোবাইল নম্বর",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = GoNagalNavy
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = customerName,
                onValueChange = {
                    customerName = it
                    errorMessage = ""
                },
                label = { Text("পূর্ণ নাম (Full Name)*", fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold) },
                placeholder = { Text("যেমন: মোঃ করিম আহমেদ", fontSize = 12.sp) },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = GoNagalGreen, modifier = Modifier.size(18.dp)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = darkFieldColors
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = customerPhone,
                onValueChange = {
                    customerPhone = it
                    errorMessage = ""
                },
                label = { Text("সচল মোবাইল নম্বর (Active Phone Number)*", fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold) },
                placeholder = { Text("যেমন: 01712345678 বা 018...", fontSize = 12.sp) },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = GoNagalGreen, modifier = Modifier.size(18.dp)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = darkFieldColors
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 2. Division & District
            Text(
                text = "২. বিভাগ ও জেলা নির্বাচন",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = GoNagalNavy
            )
            Spacer(modifier = Modifier.height(6.dp))

            Text("বিভাগ (Division):", fontSize = 11.sp, color = GoNagalTextMuted)
            Spacer(modifier = Modifier.height(4.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(divisions) { div ->
                    val isSelected = selectedDivision == div
                    Surface(
                        onClick = {
                            selectedDivision = div
                            if (!div.startsWith("ঢাকা")) {
                                deliveryAreaType = "ঢাকার বাইরে (৳১০০)"
                            } else {
                                deliveryAreaType = "ঢাকা সিটি (৳৬০)"
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) GoNagalGreen else Color(0xFFF1F5F9),
                        modifier = Modifier.padding(vertical = 2.dp)
                    ) {
                        Text(
                            text = div.substringBefore(" ("),
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else GoNagalNavy,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = districtInput,
                    onValueChange = {
                        districtInput = it
                        errorMessage = ""
                    },
                    label = { Text("জেলা (District)*", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    placeholder = { Text("যেমন: ঢাকা / চট্টগ্রাম", fontSize = 11.5.sp) },
                    leadingIcon = { Icon(Icons.Default.LocationCity, contentDescription = null, tint = GoNagalNavy, modifier = Modifier.size(16.dp)) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = darkFieldColors
                )

                OutlinedTextField(
                    value = upazilaInput,
                    onValueChange = {
                        upazilaInput = it
                        errorMessage = ""
                    },
                    label = { Text("উপজেলা / থানা*", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    placeholder = { Text("যেমন: মিরপুর / সদর", fontSize = 11.5.sp) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = darkFieldColors
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Detailed Address
            OutlinedTextField(
                value = detailedAddress,
                onValueChange = {
                    detailedAddress = it
                    errorMessage = ""
                },
                label = { Text("বিস্তারিত ঠিকানা (গ্রাম, রাস্তা, বাড়ি, ফ্ল্যাট)*", fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold) },
                placeholder = { Text("যেমন: বাড়ি ১২, রোড ৪, সেক্টর ৭, উত্তরা", fontSize = 12.sp) },
                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = GoNagalOrange, modifier = Modifier.size(18.dp)) },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3,
                colors = darkFieldColors
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 3. Delivery Method Selection
            Text(
                text = "৩. ডেলিভারি এলাকা ও চার্জ",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = GoNagalNavy
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    "ঢাকা সিটি (৳৬০)" to "ডেলিভারি ২৪-৪৮ ঘণ্টা",
                    "ঢাকার বাইরে (৳১০০)" to "ডেলিভারি ২-৩ কার্যদিবস"
                ).forEach { (areaLabel, subtitle) ->
                    val isSelected = deliveryAreaType.startsWith(areaLabel.take(6))
                    Surface(
                        onClick = { deliveryAreaType = areaLabel },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) Color(0xFFF0FDF4) else Color.White,
                        border = androidx.compose.foundation.BorderStroke(
                            1.5.dp,
                            if (isSelected) GoNagalGreen else Color(0xFFCBD5E1)
                        )
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = areaLabel,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) GoNagalGreen else GoNagalNavy
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = subtitle,
                                fontSize = 9.5.sp,
                                color = GoNagalTextMuted
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Optional Note
            OutlinedTextField(
                value = orderNotes,
                onValueChange = { orderNotes = it },
                label = { Text("বিশেষ নোট (ঐচ্ছিক)", fontSize = 11.sp) },
                placeholder = { Text("ডেলিভারি সংক্রান্ত কোনো বিশেষ নির্দেশনা থাকলে লিখুন", fontSize = 11.5.sp) },
                leadingIcon = { Icon(Icons.Default.Notes, contentDescription = null, tint = GoNagalTextMuted, modifier = Modifier.size(16.dp)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = darkFieldColors
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 4. Coupon Code
            Text(
                text = "৪. কুপন কোড (Coupon Code)",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = GoNagalNavy
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = couponInput,
                    onValueChange = { couponInput = it.uppercase() },
                    placeholder = { Text("যেমন: GONAGAL10, EID2026", fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.LocalOffer, contentDescription = null, tint = GoNagalOrange, modifier = Modifier.size(18.dp)) },
                    singleLine = true,
                    colors = darkFieldColors,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        val clean = couponInput.trim().uppercase()
                        when (clean) {
                            "GONAGAL10" -> {
                                appliedCoupon = clean
                                couponDiscount = subtotal * 0.10
                                couponMessage = "১০% ছাড় প্রয়োগ হয়েছে (-৳%,.0f)".format(couponDiscount)
                            }
                            "EID2026" -> {
                                appliedCoupon = clean
                                couponDiscount = 150.0
                                couponMessage = "ঈদ স্পেশাল ৳১৫০ ছাড় প্রয়োগ হয়েছে!"
                            }
                            "FREE60" -> {
                                appliedCoupon = clean
                                couponDiscount = 60.0
                                couponMessage = "ফ্রি ডেলিভারি ৳৬০ ছাড় কার্যকর হয়েছে!"
                            }
                            else -> {
                                appliedCoupon = ""
                                couponDiscount = 0.0
                                couponMessage = "ভুল কুপন কোড (যেমন: GONAGAL10)"
                            }
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GoNagalNavy),
                    modifier = Modifier.height(50.dp)
                ) {
                    Text("Apply", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            if (couponMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = couponMessage,
                    fontSize = 11.sp,
                    color = if (appliedCoupon.isNotEmpty()) GoNagalGreen else Color(0xFFEF4444),
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 5. 100% Cash On Delivery Highlight Card (NO online payment/bKash/card)
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBBF7D0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("💵", fontSize = 28.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "১০০% ক্যাশ অন ডেলিভারি",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = GoNagalGreen
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.Default.Security, contentDescription = null, tint = GoNagalGreen, modifier = Modifier.size(14.dp))
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "কোনো অগ্রিম টাকা দিতে হবে না। পণ্য হাতে পেয়ে চেক করে ডেলিভারি ম্যানকে টাকা পরিশোধ করুন।",
                            fontSize = 11.sp,
                            color = Color(0xFF166534),
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 6. Transparent Price Breakdown
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "সর্বমোট বিলের বিবরণ",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoNagalNavy
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("পণ্যের মূল্য ($itemCount টি আইটেম):", fontSize = 11.5.sp, color = Color(0xFF475569))
                        Text("৳%,.0f".format(subtotal), fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F172A))
                    }

                    if (couponDiscount > 0) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("কুপন ছাড় ($appliedCoupon):", fontSize = 11.5.sp, color = GoNagalGreen, fontWeight = FontWeight.Bold)
                            Text("-৳%,.0f".format(couponDiscount), fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = GoNagalGreen)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("ডেলিভারি চার্জ ($deliveryAreaType):", fontSize = 11.5.sp, color = Color(0xFF475569))
                        Text("৳%,.0f".format(deliveryFee), fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F172A))
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFE2E8F0))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("সর্বমোট প্রদেয় টাকা (ক্যাশ অন ডেলিভারি):", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = GoNagalNavy)
                        Text("৳%,.0f".format(totalAmount), fontSize = 17.sp, fontWeight = FontWeight.ExtraBold, color = GoNagalGreen)
                    }
                }
            }

            if (errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage,
                    color = Color(0xFFDC2626),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 7. Confirm Order Button with Anti-Double Submit
            Button(
                onClick = {
                    if (isSubmitting) return@Button

                    val trimmedName = customerName.trim()
                    val trimmedPhone = customerPhone.trim()
                    val trimmedAddress = detailedAddress.trim()

                    if (trimmedName.isEmpty()) {
                        errorMessage = "অনুগ্রহ করে আপনার সম্পূর্ণ নাম লিখুন"
                        return@Button
                    }
                    if (trimmedPhone.isEmpty() || trimmedPhone.length < 10) {
                        errorMessage = "অনুগ্রহ করে সঠিক ১১ ডিজিটের মোবাইল নম্বর দিন"
                        return@Button
                    }
                    if (trimmedAddress.isEmpty()) {
                        errorMessage = "অনুগ্রহ করে সম্পূর্ণ বিস্তারিত ঠিকানা লিখুন"
                        return@Button
                    }

                    isSubmitting = true
                    errorMessage = ""

                    // Save guest details for future frictionless orders
                    GuestStorage.saveLastCustomerInfo(
                        context = context,
                        name = trimmedName,
                        phone = trimmedPhone,
                        address = trimmedAddress,
                        district = districtInput
                    )

                    coroutineScope.launch {
                        delay(600) // Brief feedback
                        val fullAddress = "$trimmedAddress, $upazilaInput, $districtInput, $selectedDivision" +
                                if (orderNotes.isNotBlank()) " (নোট: $orderNotes)" else ""
                        onOrderSuccess(
                            fullAddress,
                            "$districtInput ($deliveryAreaType)",
                            "হোম ক্যাশ অন ডেলিভারি",
                            PaymentMethodType.CASH_ON_DELIVERY
                        )
                        isSubmitting = false
                        onDismiss()
                    }
                },
                enabled = !isSubmitting,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GoNagalGreen),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("অর্ডার প্রক্রিয়াধীন...", color = Color.White, fontSize = 13.5.sp, fontWeight = FontWeight.Bold)
                } else {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("অর্ডার নিশ্চিত করুন (ক্যাশ অন ডেলিভারি)", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
