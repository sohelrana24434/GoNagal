package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.GoNagalGreen
import com.example.ui.theme.GoNagalNavy
import com.example.ui.theme.GoNagalTextMuted

enum class LogoSize {
    COMPACT,
    MEDIUM,
    LARGE
}

@Composable
fun GoNagalBrandLogo(
    modifier: Modifier = Modifier,
    logoSize: LogoSize = LogoSize.MEDIUM,
    customHeight: Dp? = null,
    showTagline: Boolean = true,
    taglineInBengali: Boolean = true,
    isDarkBackground: Boolean = false
) {
    val logoHeight: Dp = customHeight ?: when (logoSize) {
        LogoSize.COMPACT -> 64.dp
        LogoSize.MEDIUM -> 92.dp
        LogoSize.LARGE -> 136.dp
    }

    val taglineFontSize = when (logoSize) {
        LogoSize.COMPACT -> 10.sp
        LogoSize.MEDIUM -> 12.sp
        LogoSize.LARGE -> 14.sp
    }

    val taglineColor = if (isDarkBackground) Color(0xFFF1F5F9) else GoNagalTextMuted

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (isDarkBackground) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color.White,
                shadowElevation = 2.dp,
                modifier = Modifier.padding(2.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.gonagal_brand_logo),
                    contentDescription = "GoNagal Clothing - Wear Your Style",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .height(logoHeight)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        } else {
            Image(
                painter = painterResource(id = R.drawable.gonagal_brand_logo),
                contentDescription = "GoNagal Clothing - Wear Your Style",
                contentScale = ContentScale.Fit,
                modifier = Modifier.height(logoHeight)
            )
        }

        if (showTagline && logoSize != LogoSize.COMPACT) {
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .clip(CircleShape)
                        .background(GoNagalGreen)
                )
                Text(
                    text = if (taglineInBengali) "যা চান, তা আপনার নাগালে" else "Wear Your Style",
                    fontSize = taglineFontSize,
                    color = taglineColor,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .clip(CircleShape)
                        .background(GoNagalGreen)
                )
            }
        }
    }
}
