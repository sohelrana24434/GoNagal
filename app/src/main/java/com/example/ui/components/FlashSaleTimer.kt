package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GoNagalNavy
import com.example.ui.theme.GoNagalOrange
import kotlinx.coroutines.delay

@Composable
fun FlashSaleTimer(
    modifier: Modifier = Modifier
) {
    val secondsLeft = 14320 // 03h : 58m : 40s fixed countdown

    val hours = secondsLeft / 3600
    val minutes = (secondsLeft % 3600) / 60
    val seconds = secondsLeft % 60

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Timer,
            contentDescription = null,
            tint = GoNagalOrange,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = "Ends in:",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.width(6.dp))

        TimerBox(value = "%02d".format(hours))
        Text(" : ", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoNagalNavy)
        TimerBox(value = "%02d".format(minutes))
        Text(" : ", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoNagalNavy)
        TimerBox(value = "%02d".format(seconds))
    }
}

@Composable
private fun TimerBox(value: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(GoNagalNavy)
            .padding(horizontal = 5.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = value,
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White
        )
    }
}
