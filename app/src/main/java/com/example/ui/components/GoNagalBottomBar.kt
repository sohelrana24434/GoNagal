package com.example.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SlowMotionVideo
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GoNagalGreen
import com.example.ui.theme.GoNagalNavy
import com.example.ui.theme.GoNagalOrange
import com.example.ui.theme.GoNagalTextMuted

enum class CustomerTab(val label: String, val icon: ImageVector) {
    HOME("হোম", Icons.Default.Home),
    REELS("রিলস", Icons.Default.SlowMotionVideo),
    CATEGORIES("ক্যাটাগরি", Icons.Default.Category),
    CART("কার্ট", Icons.Default.ShoppingCart),
    ACCOUNT("প্রোফাইল", Icons.Default.Person)
}

@Composable
fun GoNagalBottomBar(
    currentTab: CustomerTab,
    onTabSelected: (CustomerTab) -> Unit,
    cartItemCount: Int = 0,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier.height(72.dp),
        containerColor = Color.White,
        tonalElevation = 10.dp
    ) {
        CustomerTab.values().forEach { tab ->
            val isSelected = currentTab == tab
            val isReelTab = tab == CustomerTab.REELS

            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(tab) },
                icon = {
                    if (tab == CustomerTab.CART && cartItemCount > 0) {
                        BadgedBox(
                            badge = {
                                Badge(containerColor = GoNagalOrange) {
                                    Text(
                                        text = if (cartItemCount > 99) "99+" else "$cartItemCount",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }
                        ) {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.label,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    } else if (isReelTab) {
                        BadgedBox(
                            badge = {
                                Badge(containerColor = Color(0xFFDC2626)) {
                                    Text(
                                        text = "LIVE",
                                        color = Color.White,
                                        fontSize = 7.5.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }
                        ) {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.label,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    } else {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = tab.label,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                },
                label = {
                    Text(
                        text = tab.label,
                        fontSize = 11.5.sp,
                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                        maxLines = 1
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = if (isReelTab) Color(0xFFDC2626) else GoNagalGreen,
                    selectedTextColor = if (isReelTab) Color(0xFFDC2626) else GoNagalGreen,
                    unselectedIconColor = GoNagalTextMuted,
                    unselectedTextColor = GoNagalTextMuted,
                    indicatorColor = if (isReelTab) Color(0xFFFEE2E2) else GoNagalGreen.copy(alpha = 0.14f)
                )
            )
        }
    }
}
