package com.example.ui.screens.shop

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MockMarketplaceData
import com.example.model.Product
import com.example.ui.components.ProductCard
import com.example.ui.theme.GoNagalBackground
import com.example.ui.theme.GoNagalGreen
import com.example.ui.theme.GoNagalNavy
import com.example.ui.theme.GoNagalOrange
import com.example.ui.theme.GoNagalTextMuted
import com.example.ui.theme.GoNagalTextSecondary

enum class SortOption(val titleBangla: String, val titleEnglish: String) {
    POPULARITY("জনপ্রিয়", "Most Popular"),
    PRICE_LOW_HIGH("দাম: কম থেকে বেশি", "Price: Low to High"),
    PRICE_HIGH_LOW("দাম: বেশি থেকে কম", "Price: High to Low"),
    RATING("সেরা রেটিং", "Top Rated"),
    DISCOUNT("সর্বোচ্চ ছাড়", "Biggest Discount")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopScreen(
    onProductClick: (Product) -> Unit,
    onAddToCart: (Product) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedSort by remember { mutableStateOf(SortOption.POPULARITY) }
    var showSortMenu by remember { mutableStateOf(false) }
    var showFilterSheet by remember { mutableStateOf(false) }

    // Filter states
    var selectedBrand by remember { mutableStateOf<String?>(null) }
    var minPriceFilter by remember { mutableDoubleStateOf(0.0) }
    var maxPriceFilter by remember { mutableDoubleStateOf(50000.0) }
    var minRatingFilter by remember { mutableFloatStateOf(0.0f) }
    var minDiscountFilter by remember { mutableStateOf(0) }
    var inStockOnly by remember { mutableStateOf(false) }

    val allProducts = MockMarketplaceData.allCatalogProducts

    // Available brands list
    val allBrands = remember {
        listOf("All") + allProducts.map { it.brand }.distinct().filter { it.isNotBlank() }.take(8)
    }

    // Filtered and Sorted products
    val filteredProducts = allProducts.filter { product ->
        val matchesSearch = searchQuery.isBlank() ||
                product.title.contains(searchQuery, ignoreCase = true) ||
                product.brand.contains(searchQuery, ignoreCase = true) ||
                product.category.contains(searchQuery, ignoreCase = true)

        val matchesBrand = selectedBrand == null || selectedBrand == "All" || product.brand.equals(selectedBrand, ignoreCase = true)
        val matchesPrice = product.sellingPrice in minPriceFilter..maxPriceFilter
        val matchesRating = product.rating >= minRatingFilter
        val matchesDiscount = product.discountPercent >= minDiscountFilter
        val matchesStock = !inStockOnly || product.inStock

        matchesSearch && matchesBrand && matchesPrice && matchesRating && matchesDiscount && matchesStock
    }.let { list ->
        when (selectedSort) {
            SortOption.POPULARITY -> list.sortedByDescending { it.reviewCount }
            SortOption.PRICE_LOW_HIGH -> list.sortedBy { it.sellingPrice }
            SortOption.PRICE_HIGH_LOW -> list.sortedByDescending { it.sellingPrice }
            SortOption.RATING -> list.sortedByDescending { it.rating }
            SortOption.DISCOUNT -> list.sortedByDescending { it.discountPercent }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GoNagalBackground)
    ) {
        // Header & Search Bar
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
                    Column {
                        Text(
                            text = "GoNagal Shop (সব পণ্য)",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoNagalNavy
                        )
                        Text(
                            text = "${filteredProducts.size} টি অনুমোদিত পণ্য প্রদর্শিত হচ্ছে",
                            fontSize = 11.sp,
                            color = GoNagalTextMuted
                        )
                    }

                    // Filter Button
                    Surface(
                        onClick = { showFilterSheet = true },
                        shape = RoundedCornerShape(8.dp),
                        color = if (selectedBrand != null || minRatingFilter > 0 || minDiscountFilter > 0 || inStockOnly) Color(0xFFF0FDF4) else Color(0xFFF1F5F9),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (selectedBrand != null || minRatingFilter > 0 || minDiscountFilter > 0 || inStockOnly) GoNagalGreen else Color.Transparent
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = "Filter",
                                tint = GoNagalGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "ফিল্টার",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoNagalNavy
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Search Input Field
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFF1F5F9)
                ) {
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("ব্র্যান্ড, গ্যাজেট, থ্রি-পিস বা পণ্য খুঁজুন...", fontSize = 12.sp, color = GoNagalTextMuted) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = GoNagalTextMuted, modifier = Modifier.size(18.dp)) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = GoNagalTextMuted,
                                    modifier = Modifier.clickable { searchQuery = "" }
                                )
                            }
                        },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Sort and Quick Filter Chips Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Sort Dropdown Trigger
                    Box {
                        Surface(
                            onClick = { showSortMenu = true },
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFF8FAFC),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "সর্ট: ${selectedSort.titleBangla}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = GoNagalNavy
                                )
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = GoNagalNavy, modifier = Modifier.size(16.dp))
                            }
                        }

                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false }
                        ) {
                            SortOption.values().forEach { opt ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = "${opt.titleBangla} (${opt.titleEnglish})",
                                            fontSize = 12.sp,
                                            fontWeight = if (selectedSort == opt) FontWeight.Bold else FontWeight.Normal,
                                            color = if (selectedSort == opt) GoNagalGreen else GoNagalNavy
                                        )
                                    },
                                    onClick = {
                                        selectedSort = opt
                                        showSortMenu = false
                                    }
                                )
                            }
                        }
                    }

                    // Brand Quick Filter Chips
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.weight(1f).padding(start = 8.dp)
                    ) {
                        items(allBrands) { brand ->
                            val isSelected = (brand == "All" && selectedBrand == null) || selectedBrand == brand
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    selectedBrand = if (brand == "All" || selectedBrand == brand) null else brand
                                },
                                label = { Text(brand, fontSize = 10.5.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = GoNagalGreen,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }
        }

        // Product Catalog 2-Column List
        if (filteredProducts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🔍", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "কোনো পণ্য খুঁজে পাওয়া যায়নি",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoNagalNavy
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "অন্য কি-ওয়ার্ড দিয়ে খুঁজুন অথবা ফিল্টার রিসেট করুন",
                        fontSize = 12.sp,
                        color = GoNagalTextMuted
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                val chunked = filteredProducts.chunked(2)
                items(chunked) { rowItems ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        rowItems.forEach { product ->
                            Box(modifier = Modifier.weight(1f)) {
                                ProductCard(
                                    product = product,
                                    onProductClick = { onProductClick(it) },
                                    onAddToCart = { onAddToCart(it) }
                                )
                            }
                        }
                        if (rowItems.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }

    // Filter Bottom Sheet
    if (showFilterSheet) {
        val filterSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false },
            sheetState = filterSheetState,
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("পণ্য ফিল্টার (Product Filter)", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = GoNagalNavy)
                    Text(
                        text = "Reset All",
                        fontSize = 12.sp,
                        color = Color(0xFFEF4444),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            selectedBrand = null
                            minPriceFilter = 0.0
                            maxPriceFilter = 50000.0
                            minRatingFilter = 0.0f
                            minDiscountFilter = 0
                            inStockOnly = false
                        }
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFE2E8F0))

                // Price Range Presets
                Text("১. মূল্য সীমা (Price Range)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoNagalNavy)
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("All" to (0.0 to 50000.0), "৳৫০০ এর নিচে" to (0.0 to 500.0), "৳৫০০-৳২০০০" to (500.0 to 2000.0), "৳২০০০+" to (2000.0 to 50000.0)).forEach { (label, range) ->
                        val isSel = minPriceFilter == range.first && maxPriceFilter == range.second
                        Surface(
                            onClick = {
                                minPriceFilter = range.first
                                maxPriceFilter = range.second
                            },
                            shape = RoundedCornerShape(6.dp),
                            color = if (isSel) GoNagalGreen else Color(0xFFF1F5F9),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = label,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) Color.White else GoNagalNavy,
                                modifier = Modifier.padding(vertical = 6.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Minimum Rating
                Text("২. স্টার রেটিং (Customer Rating)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoNagalNavy)
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(0.0f to "All Stars", 4.0f to "4.0★+", 4.5f to "4.5★+").forEach { (stars, label) ->
                        val isSel = minRatingFilter == stars
                        Surface(
                            onClick = { minRatingFilter = stars },
                            shape = RoundedCornerShape(6.dp),
                            color = if (isSel) GoNagalOrange else Color(0xFFF1F5F9),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) Color.White else GoNagalNavy,
                                modifier = Modifier.padding(vertical = 6.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Discount Filter
                Text("৩. ডিসকাউন্ট (Discount %)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoNagalNavy)
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(0 to "All", 20 to "20%+", 40 to "40%+", 50 to "50%+").forEach { (disc, label) ->
                        val isSel = minDiscountFilter == disc
                        Surface(
                            onClick = { minDiscountFilter = disc },
                            shape = RoundedCornerShape(6.dp),
                            color = if (isSel) GoNagalGreen else Color(0xFFF1F5F9),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) Color.White else GoNagalNavy,
                                modifier = Modifier.padding(vertical = 6.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // In stock toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("ইন স্টক শুধুমাত্র (In Stock Only)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoNagalNavy)
                        Text("উপলব্ধ রেডি-টু-ডেলিভারি আইটেমসমূহ", fontSize = 10.sp, color = GoNagalTextMuted)
                    }
                    Switch(
                        checked = inStockOnly,
                        onCheckedChange = { inStockOnly = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = GoNagalGreen)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Surface(
                    onClick = { showFilterSheet = false },
                    shape = RoundedCornerShape(10.dp),
                    color = GoNagalGreen,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "ফিল্টার প্রয়োগ করুন (${filteredProducts.size} পণ্য)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
