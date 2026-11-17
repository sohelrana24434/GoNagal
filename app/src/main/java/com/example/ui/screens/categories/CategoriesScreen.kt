package com.example.ui.screens.categories

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import com.example.model.MockMarketplaceData
import com.example.model.Product
import com.example.ui.components.ProductCard
import com.example.ui.theme.GoNagalBackground
import com.example.ui.theme.GoNagalGreen
import com.example.ui.theme.GoNagalNavy
import com.example.ui.theme.GoNagalTextMuted

@Composable
fun CategoriesScreen(
    onProductClick: (Product) -> Unit,
    onAddToCart: (Product) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf(MockMarketplaceData.categories.first()) }
    var selectedSubCategory by remember { mutableStateOf("সব পণ্য") }

    val subCategories = remember(selectedCategory) {
        listOf("সব পণ্য", "সেরা ডিল", "নতুন কালেকশন", "জনপ্রিয়")
    }

    val allProducts = MockMarketplaceData.allCatalogProducts
    val categoryProducts = allProducts.filter {
        val matchesCategory = it.category.contains(selectedCategory.name, ignoreCase = true) ||
                selectedCategory.name.contains(it.category, ignoreCase = true)

        val matchesSub = when (selectedSubCategory) {
            "সব পণ্য" -> true
            "সেরা ডিল" -> it.isFlashSale || it.discountPercent >= 40
            "নতুন কালেকশন" -> it.id.hashCode() % 2 == 0
            "জনপ্রিয়" -> it.isTrending || it.isBestSeller
            else -> true
        }
        matchesCategory && matchesSub
    }.ifEmpty { allProducts.take(6) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GoNagalBackground)
    ) {
        // Categories Top Header
        Surface(
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "সব ক্যাটাগরি (All Categories)",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoNagalNavy
                )
                Text(
                    text = "ক্যাটাগরি ও সাব-ক্যাটাগরি অনুযায়ী পণ্য ব্রাউজ করুন",
                    fontSize = 11.5.sp,
                    color = GoNagalTextMuted
                )
            }
        }

        // Two-pane Layout: Left sidebar category list + Right product & subcategory grid
        Row(modifier = Modifier.fillMaxSize()) {
            // Left Category Navigation Rail
            LazyColumn(
                modifier = Modifier
                    .width(100.dp)
                    .fillMaxHeight()
                    .background(Color.White)
            ) {
                items(MockMarketplaceData.categories) { cat ->
                    val isSelected = selectedCategory.id == cat.id
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedCategory = cat
                                selectedSubCategory = "সব পণ্য"
                            }
                            .background(if (isSelected) GoNagalBackground else Color.White)
                            .padding(vertical = 12.dp, horizontal = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Surface(
                                shape = CircleShape,
                                color = if (isSelected) GoNagalGreen.copy(alpha = 0.15f) else Color(0xFFF1F5F9),
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(cat.iconEmoji, fontSize = 22.sp)
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = cat.name,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) GoNagalGreen else GoNagalNavy,
                                maxLines = 2,
                                lineHeight = 13.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }

            // Right Product Grid Pane
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(10.dp)
            ) {
                // Category Banner Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = selectedCategory.name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoNagalNavy
                            )
                            Text(
                                text = selectedCategory.bengaliName,
                                fontSize = 11.sp,
                                color = GoNagalGreen,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Text(
                            text = "${categoryProducts.size} Products",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoNagalTextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Sub-category Filter Chips Bar
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(subCategories) { subCat ->
                        val isSelected = selectedSubCategory == subCat
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedSubCategory = subCat },
                            label = { Text(subCat, fontSize = 10.5.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GoNagalGreen,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(categoryProducts) { product ->
                        ProductCard(
                            product = product,
                            onProductClick = onProductClick,
                            onAddToCart = onAddToCart
                        )
                    }
                }
            }
        }
    }
}
