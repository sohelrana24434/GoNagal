package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.GuestStorage
import com.example.model.OrdersRepository
import com.example.model.PaymentMethodType
import com.example.model.Product
import com.example.model.WishlistRepository
import com.example.ui.theme.GoNagalGreen
import com.example.ui.theme.GoNagalNavy
import com.example.ui.theme.GoNagalOrange
import com.example.ui.theme.GoNagalTextMuted
import com.example.ui.theme.GoNagalTextPrimary
import com.example.ui.theme.GoNagalTextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailOrderBottomSheet(
    product: Product,
    onDismiss: () -> Unit,
    onAddToCart: (Product, Int, String?, String?) -> Unit,
    onDirectOrderNow: (Product, Int, String?, String?, String, String, PaymentMethodType) -> Unit,
    onWatchVideo: ((Product) -> Unit)? = null,
    onOpenSellerStore: ((String) -> Unit)? = null,
    onOrderPlacedSuccess: ((orderId: String) -> Unit)? = null
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scrollState = rememberScrollState()

    // Step state: false = Product Details, true = Mandatory Checkout Form
    var isCheckoutStep by remember { mutableStateOf(false) }

    // Product selections
    var quantity by remember { mutableIntStateOf(1) }
    var selectedColor by remember { mutableStateOf(product.colorVariants.firstOrNull()) }
    var selectedSize by remember { mutableStateOf(product.sizeVariants.firstOrNull()) }
    var isWishlisted by remember { mutableStateOf(WishlistRepository.isWishlisted(product)) }

    // Delivery & Location selections
    var selectedDivision by remember {
        mutableStateOf(GuestStorage.getLastCustomerDivision(context).ifEmpty { "ঢাকা (Dhaka)" })
    }
    var districtInput by remember {
        mutableStateOf(GuestStorage.getLastCustomerDistrict(context).ifEmpty { "ঢাকা" })
    }
    var thanaInput by remember {
        mutableStateOf(GuestStorage.getLastCustomerThana(context).ifEmpty { "" })
    }
    var customerName by remember {
        mutableStateOf(GuestStorage.getLastCustomerName(context).ifEmpty { "" })
    }
    var customerPhone by remember {
        mutableStateOf(GuestStorage.getLastCustomerPhone(context).ifEmpty { "" })
    }
    var detailedAddress by remember {
        mutableStateOf(GuestStorage.getLastCustomerAddress(context).ifEmpty { "" })
    }
    var deliveryAreaType by remember {
        mutableStateOf(
            if (GuestStorage.getLastCustomerDistrict(context).contains("outside", ignoreCase = true) ||
                !GuestStorage.getLastCustomerDistrict(context).contains("ঢাকা", ignoreCase = true)
            ) "ঢাকার বাইরে (৳১০০)" else "ঢাকা সিটি (৳৬০)"
        )
    }

    // Validation error states
    var nameError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var districtError by remember { mutableStateOf<String?>(null) }
    var thanaError by remember { mutableStateOf<String?>(null) }
    var addressError by remember { mutableStateOf<String?>(null) }
    var generalError by remember { mutableStateOf<String?>(null) }

    // Submission & Confirmation states
    var isSubmitting by remember { mutableStateOf(false) }
    var confirmedOrderId by remember { mutableStateOf<String?>(null) }

    val deliveryFee = if (deliveryAreaType.contains("ঢাকা সিটি")) 60.0 else 100.0
    val itemsSubtotal = product.sellingPrice * quantity
    val totalPayable = itemsSubtotal + deliveryFee

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
        unfocusedPlaceholderColor = Color(0xFF94A3B8),
        errorBorderColor = Color(0xFFDC2626),
        errorLabelColor = Color(0xFFDC2626)
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
            if (!isCheckoutStep) {
                // ==========================================
                // STEP 1: PRODUCT DETAILS & VARIANT SELECTION
                // ==========================================

                // Header Action Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFFF0FDF4)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.Verified, contentDescription = null, tint = GoNagalGreen, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "GoNagal Mall Verified",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoNagalGreen
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Wishlist toggle
                        IconButton(
                            onClick = {
                                isWishlisted = WishlistRepository.toggleWishlist(product)
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = if (isWishlisted) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Wishlist",
                                tint = if (isWishlisted) Color(0xFFEF4444) else GoNagalTextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Share
                        IconButton(onClick = { }, modifier = Modifier.size(36.dp)) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = "Share", tint = GoNagalNavy, modifier = Modifier.size(18.dp))
                        }

                        // Close
                        IconButton(onClick = onDismiss, modifier = Modifier.size(36.dp)) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = GoNagalTextMuted, modifier = Modifier.size(20.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Product Image
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFF8FAFC),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        val imgUrl = product.imageUrl.ifEmpty { "https://images.unsplash.com/photo-1523381210434-271e8be1f52b?w=600&auto=format&fit=crop&q=80" }
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(imgUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = product.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(14.dp))
                        )

                        // Video tag
                        Surface(
                            onClick = { onWatchVideo?.invoke(product) },
                            shape = RoundedCornerShape(20.dp),
                            color = Color.Black.copy(alpha = 0.7f),
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.PlayCircleOutline, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("ভিডিও দেখুন", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Price and Title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = product.formattedSellingPrice,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = GoNagalGreen
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = product.formattedOriginalPrice,
                            fontSize = 13.sp,
                            color = GoNagalTextMuted,
                            textDecoration = TextDecoration.LineThrough
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = GoNagalOrange.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "${product.discountPercent}% OFF",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = GoNagalOrange,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = product.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoNagalNavy,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Quantity Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "পরিমাণ (Quantity):",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoNagalNavy
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(8.dp))
                            .background(Color(0xFFF8FAFC), RoundedCornerShape(8.dp))
                    ) {
                        IconButton(
                            onClick = { if (quantity > 1) quantity-- },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(16.dp))
                        }
                        Text(
                            text = "$quantity",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoNagalNavy,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                        IconButton(
                            onClick = { quantity++ },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(16.dp))
                        }
                    }
                }

                // Size Variants
                if (product.sizeVariants.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "সাইজ নির্বাচন করুন:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoNagalNavy
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        product.sizeVariants.forEach { size ->
                            val isSelected = selectedSize == size
                            Surface(
                                onClick = { selectedSize = size },
                                shape = RoundedCornerShape(6.dp),
                                color = if (isSelected) GoNagalGreen else Color.White,
                                border = BorderStroke(1.dp, if (isSelected) GoNagalGreen else Color(0xFFCBD5E1))
                            ) {
                                Text(
                                    text = size,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else GoNagalNavy,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

                // Color Variants
                if (product.colorVariants.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "কালার নির্বাচন করুন:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoNagalNavy
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        product.colorVariants.forEach { col ->
                            val isSelected = selectedColor == col
                            Surface(
                                onClick = { selectedColor = col },
                                shape = RoundedCornerShape(6.dp),
                                color = if (isSelected) GoNagalNavy else Color(0xFFF1F5F9),
                                border = BorderStroke(1.dp, if (isSelected) GoNagalNavy else Color(0xFFCBD5E1))
                            ) {
                                Text(
                                    text = col,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else GoNagalNavy,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Price Summary Card
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("আইটেম সাবটোটাল ($quantity টি):", fontSize = 11.5.sp, color = GoNagalTextMuted)
                            Text("৳%,.0f".format(itemsSubtotal), fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = GoNagalNavy)
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("ডেলিভারি চার্জ (হোম ডেলিভারি):", fontSize = 11.5.sp, color = GoNagalTextMuted)
                            Text("৳৬০ - ৳১০০", fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold, color = GoNagalNavy)
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = Color(0xFFE2E8F0))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("পেমেন্ট পদ্ধতি:", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = GoNagalNavy)
                            Text("ক্যাশ অন ডেলিভারি (COD)", fontSize = 11.5.sp, fontWeight = FontWeight.ExtraBold, color = GoNagalGreen)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action Buttons: Add to Cart & Proceed to Checkout
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            onAddToCart(product, quantity, selectedColor, selectedSize)
                            onDismiss()
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                    ) {
                        Icon(Icons.Default.AddShoppingCart, contentDescription = null, tint = GoNagalNavy, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("কার্টে রাখুন", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoNagalNavy)
                    }

                    Button(
                        onClick = {
                            // Advance to mandatory checkout information form!
                            isCheckoutStep = true
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GoNagalGreen),
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                    ) {
                        Text("অর্ডার করুন (ক্যাশ অন ডেলিভারি)", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

            } else {
                // ========================================================
                // STEP 2: MANDATORY CHECKOUT FORM (NAME, PHONE, FULL ADDRESS)
                // ========================================================

                // Checkout Header with Back Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { isCheckoutStep = false },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = GoNagalNavy)
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Column {
                            Text("অর্ডারের তথ্য প্রদান করুন", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = GoNagalNavy)
                            Text("ক্যাশ অন ডেলিভারি (পণ্য পেয়ে মূল্য পরিশোধ)", fontSize = 10.5.sp, color = GoNagalGreen, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(34.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = GoNagalTextMuted)
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFE2E8F0))

                // Selected Product Mini Summary Card
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(product.imageUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = product.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = product.title,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoNagalNavy,
                                maxLines = 1
                            )
                            val variantText = listOfNotNull(selectedSize?.let { "সাইজ: $it" }, selectedColor?.let { "কালার: $it" }).joinToString(" • ")
                            if (variantText.isNotEmpty()) {
                                Text(text = variantText, fontSize = 10.sp, color = GoNagalTextMuted)
                            }
                            Text(
                                text = "$quantity টি × ${product.formattedSellingPrice} = ৳%,.0f".format(itemsSubtotal),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = GoNagalGreen
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 1. Mandatory Name
                Text("১. গ্রাহকের পূর্ণ নাম (Full Name)*", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoNagalNavy)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = customerName,
                    onValueChange = {
                        customerName = it
                        nameError = null
                        generalError = null
                    },
                    placeholder = { Text("যেমন: মোঃ তানভীর হোসেন", fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = GoNagalGreen, modifier = Modifier.size(18.dp)) },
                    isError = nameError != null,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = darkFieldColors
                )
                if (nameError != null) {
                    Text(text = nameError!!, color = Color(0xFFDC2626), fontSize = 10.5.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 4.dp, top = 2.dp))
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 2. Mandatory Active Phone Number (11 digits)
                Text("২. সচল মোবাইল নম্বর (Active Phone Number)*", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoNagalNavy)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = customerPhone,
                    onValueChange = {
                        customerPhone = it
                        phoneError = null
                        generalError = null
                    },
                    placeholder = { Text("যেমন: 01712345678 (১১ ডিজিট)", fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = GoNagalGreen, modifier = Modifier.size(18.dp)) },
                    isError = phoneError != null,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = darkFieldColors
                )
                if (phoneError != null) {
                    Text(text = phoneError!!, color = Color(0xFFDC2626), fontSize = 10.5.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 4.dp, top = 2.dp))
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 3. Division Selection
                Text("৩. বিভাগ নির্বাচন করুন (Division)*", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoNagalNavy)
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
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 4. District and Upazila / Thana
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("৪. জেলা (District)*", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = GoNagalNavy)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = districtInput,
                            onValueChange = {
                                districtInput = it
                                districtError = null
                                generalError = null
                            },
                            placeholder = { Text("যেমন: ঢাকা", fontSize = 11.5.sp) },
                            leadingIcon = { Icon(Icons.Default.LocationCity, contentDescription = null, tint = GoNagalNavy, modifier = Modifier.size(16.dp)) },
                            isError = districtError != null,
                            singleLine = true,
                            colors = darkFieldColors
                        )
                        if (districtError != null) {
                            Text(text = districtError!!, color = Color(0xFFDC2626), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text("৫. উপজেলা / থানা*", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = GoNagalNavy)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = thanaInput,
                            onValueChange = {
                                thanaInput = it
                                thanaError = null
                                generalError = null
                            },
                            placeholder = { Text("যেমন: মিরপুর / সদর", fontSize = 11.5.sp) },
                            isError = thanaError != null,
                            singleLine = true,
                            colors = darkFieldColors
                        )
                        if (thanaError != null) {
                            Text(text = thanaError!!, color = Color(0xFFDC2626), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 5. Full Delivery Address
                Text("৬. বিস্তারিত ডেলিভারি ঠিকানা (গ্রাম, রাস্তা, বাড়ি নং ও এলাকা)*", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoNagalNavy)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = detailedAddress,
                    onValueChange = {
                        detailedAddress = it
                        addressError = null
                        generalError = null
                    },
                    placeholder = { Text("যেমন: বাড়ি ১২, রোড ৪, ব্লক সি, মিরপুর ১০", fontSize = 11.5.sp) },
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = GoNagalOrange, modifier = Modifier.size(18.dp)) },
                    isError = addressError != null,
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    colors = darkFieldColors
                )
                if (addressError != null) {
                    Text(text = addressError!!, color = Color(0xFFDC2626), fontSize = 10.5.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 4.dp, top = 2.dp))
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Delivery Charge Area Selector
                Text("ডেলিভারি এলাকা ও চার্জ নির্বাচন করুন:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoNagalNavy)
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                        "ঢাকা সিটি (৳৬০)" to "ডেলিভারি ২৪-৪৮ ঘণ্টা",
                        "ঢাকার বাইরে (৳১০০)" to "ডেলিভারি ২-৩ কার্যদিবস"
                    ).forEach { (areaLabel, subtitle) ->
                        val isSelected = deliveryAreaType.startsWith(areaLabel.take(6))
                        Surface(
                            onClick = { deliveryAreaType = areaLabel },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) Color(0xFFF0FDF4) else Color.White,
                            border = BorderStroke(1.5.dp, if (isSelected) GoNagalGreen else Color(0xFFCBD5E1))
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    text = areaLabel,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) GoNagalGreen else GoNagalNavy
                                )
                                Text(text = subtitle, fontSize = 9.sp, color = GoNagalTextMuted)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Payment Method: Exclusive Cash on Delivery
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFF0FDF4),
                    border = BorderStroke(1.dp, Color(0xFFBBF7D0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "💵", fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("পেমেন্ট মাধ্যম: ক্যাশ অন ডেলিভারি (Cash on Delivery)", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = GoNagalGreen)
                            Text("পণ্য হাতে পেয়ে সম্পূর্ণ মূল্য পরিশোধ করুন। কোনো অগ্রিম টাকা লাগবে না।", fontSize = 10.sp, color = Color(0xFF166534))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Total Bill Breakdown Card
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("পণ্য মূল্য ($quantity টি):", fontSize = 11.sp, color = GoNagalTextMuted)
                            Text("৳%,.0f".format(itemsSubtotal), fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = GoNagalNavy)
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("ডেলিভারি চার্জ ($deliveryAreaType):", fontSize = 11.sp, color = GoNagalTextMuted)
                            Text("৳%,.0f".format(deliveryFee), fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = GoNagalNavy)
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = Color(0xFFCBD5E1))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("সর্বমোট প্রদেয় বিল:", fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = GoNagalNavy)
                            Text("৳%,.0f".format(totalPayable), fontSize = 16.sp, fontWeight = FontWeight.Black, color = GoNagalGreen)
                        }
                    }
                }

                if (generalError != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = generalError!!,
                        color = Color(0xFFDC2626),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Final Confirm Order Button (Anti double-submit protected)
                Button(
                    onClick = {
                        if (isSubmitting) return@Button

                        val name = customerName.trim()
                        val phone = customerPhone.trim()
                        val district = districtInput.trim()
                        val thana = thanaInput.trim()
                        val address = detailedAddress.trim()

                        var hasError = false
                        if (name.isEmpty()) {
                            nameError = "অনুগ্রহ করে আপনার পূর্ণ নাম লিখুন"
                            hasError = true
                        }
                        if (phone.isEmpty() || phone.length < 10) {
                            phoneError = "সঠিক ১১ ডিজিটের মোবাইল নম্বর দিন (যেমন: 017XXXXXXXX)"
                            hasError = true
                        }
                        if (district.isEmpty()) {
                            districtError = "জেলার নাম লিখুন"
                            hasError = true
                        }
                        if (thana.isEmpty()) {
                            thanaError = "উপজেলা বা থানার নাম লিখুন"
                            hasError = true
                        }
                        if (address.isEmpty()) {
                            addressError = "গ্রাম, রাস্তা, বাড়ি নং ও সম্পূর্ণ ডেলিভারি ঠিকানা লিখুন"
                            hasError = true
                        }

                        if (hasError) {
                            generalError = "সবগুলো আবশ্যকীয় ফিল্ড পূরণ করে আবার চেষ্টা করুন।"
                            return@Button
                        }

                        isSubmitting = true
                        generalError = null

                        // Save customer info to local device storage for future convenience
                        GuestStorage.saveLastCustomerInfo(
                            context = context,
                            name = name,
                            phone = phone,
                            address = address,
                            district = district,
                            division = selectedDivision,
                            thana = thana
                        )

                        coroutineScope.launch {
                            delay(500) // Brief processing feedback
                            val guestDeviceId = GuestStorage.getOrCreateGuestId(context)
                            val fullCombinedAddress = "$address, $thana, $district, $selectedDivision"
                            val variantInfo = listOfNotNull(selectedSize?.let { "Size: $it" }, selectedColor?.let { "Color: $it" }).joinToString(", ")
                            val fullProductTitle = product.title + (if (variantInfo.isNotEmpty()) " ($variantInfo)" else "")

                            try {
                                val generatedId = OrdersRepository.addNewOrder(
                                    productName = "$fullProductTitle × $quantity",
                                    itemCount = quantity,
                                    totalAmountFormatted = "৳%,.0f".format(totalPayable),
                                    iconEmoji = product.imageUrl,
                                    deliveryType = "Home Delivery ($deliveryAreaType)",
                                    paymentMethod = "ক্যাশ অন ডেলিভারি (Cash on Delivery)",
                                    destination = fullCombinedAddress,
                                    customerName = name,
                                    customerPhone = phone,
                                    customerAddress = address,
                                    division = selectedDivision,
                                    district = district,
                                    thana = thana,
                                    guestId = guestDeviceId
                                )
                                isSubmitting = false
                                confirmedOrderId = generatedId
                            } catch (e: Exception) {
                                isSubmitting = false
                                generalError = e.message ?: "অর্ডার সম্পন্ন হতে ত্রুটি হয়েছে।"
                            }
                        }
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GoNagalGreen),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("অর্ডার যাচাই করা হচ্ছে...", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    } else {
                        Text("অর্ডার নিশ্চিত করুন (Confirm Order)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // ========================================================
    // ORDER CONFIRMATION SCREEN / DIALOG
    // ========================================================
    confirmedOrderId?.let { orderId ->
        AlertDialog(
            onDismissRequest = {
                confirmedOrderId = null
                onDismiss()
            },
            confirmButton = {
                Button(
                    onClick = {
                        confirmedOrderId = null
                        onDismiss()
                        onOrderPlacedSuccess?.invoke(orderId)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoNagalGreen)
                ) {
                    Text("অর্ডার ট্র্যাক করুন", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        confirmedOrderId = null
                        onDismiss()
                    }
                ) {
                    Text("ঠিক আছে")
                }
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = GoNagalGreen, modifier = Modifier.size(26.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("অর্ডার সফলভাবে গ্রহণ করা হয়েছে!", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = GoNagalNavy)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        color = Color(0xFFF0FDF4),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("ইউনিক অর্ডার নম্বর:", fontSize = 11.sp, color = GoNagalTextMuted)
                            Text("#$orderId", fontSize = 16.sp, fontWeight = FontWeight.Black, color = GoNagalGreen)
                        }
                    }

                    Text("👤 গ্রাহকের নাম: ${customerName.trim()}", fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold, color = GoNagalNavy)
                    Text("📞 মোবাইল নম্বর: ${customerPhone.trim()}", fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold, color = GoNagalNavy)
                    Text("📍 ডেলিভারি ঠিকানা: ${detailedAddress.trim()}, ${thanaInput.trim()}, ${districtInput.trim()}", fontSize = 11.sp, color = Color(0xFF475569))
                    Text("💵 সর্বমোট প্রদেয়: ৳%,.0f (ক্যাশ অন ডেলিভারি)".format(totalPayable), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoNagalNavy)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("আমাদের কাস্টমার কেয়ার প্রতিনিধি শীঘ্রই আপনার সাথে যোগাযোগ করবেন। কোনো অগ্রিম টাকা দেওয়ার প্রয়োজন নেই।", fontSize = 10.sp, color = GoNagalTextMuted)
                }
            }
        )
    }
}
