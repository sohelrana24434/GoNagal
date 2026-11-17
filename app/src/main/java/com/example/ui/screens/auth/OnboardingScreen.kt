package com.example.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.GuestStorage
import com.example.ui.components.GoNagalBrandLogo
import com.example.ui.components.GoNagalPrimaryButton
import com.example.ui.components.LogoSize
import com.example.ui.theme.GoNagalGreen
import com.example.ui.theme.GoNagalNavy
import com.example.ui.theme.GoNagalTextMuted
import com.example.ui.theme.GoNagalTextSecondary
import kotlinx.coroutines.launch

data class OnboardingPageData(
    val imageRes: Int,
    val title: String,
    val subtitle: String,
    val highlightTag: String,
    val iconEmoji: String
)

@Composable
fun OnboardingScreen(
    onFinishOnboarding: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val pages = listOf(
        OnboardingPageData(
            imageRes = R.drawable.gonagal_onb_shop,
            title = "GoNagal-এ স্বাগতম",
            subtitle = "আপনার পছন্দের পণ্য, এখন আরও সহজে।",
            highlightTag = "স্বাগতম",
            iconEmoji = "🛍️"
        ),
        OnboardingPageData(
            imageRes = R.drawable.gonagal_onb_delivery,
            title = "সহজে পণ্য অর্ডার করুন",
            subtitle = "কোনো অ্যাকাউন্ট ছাড়াই সহজে অর্ডার করুন।",
            highlightTag = "সহজ অর্ডার",
            iconEmoji = "⚡"
        ),
        OnboardingPageData(
            imageRes = R.drawable.gonagal_onb_trust,
            title = "পণ্য হাতে পেয়ে টাকা দিন",
            subtitle = "পণ্য গ্রহণ করুন, তারপর টাকা পরিশোধ করুন।",
            highlightTag = "১০০% ক্যাশ অন ডেলিভারি",
            iconEmoji = "💵"
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val coroutineScope = rememberCoroutineScope()

    val completeAndNavigate = {
        GuestStorage.setOnboardingCompleted(context, true)
        onFinishOnboarding()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .testTag("onboarding_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Bar with Logo and Skip button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            GoNagalBrandLogo(
                logoSize = LogoSize.COMPACT,
                customHeight = 56.dp,
                showTagline = false
            )

            TextButton(
                onClick = completeAndNavigate,
                modifier = Modifier.testTag("skip_button")
            ) {
                Text(
                    text = "Skip",
                    color = GoNagalNavy,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Slide Content
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) { pageIndex ->
            val page = pages[pageIndex]
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Illustration Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.88f)
                        .aspectRatio(1.05f)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFFF8FAFC)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = page.imageRes),
                        contentDescription = page.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Highlight Tag
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(GoNagalGreen.copy(alpha = 0.12f))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(page.iconEmoji, fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = page.highlightTag,
                            color = GoNagalGreen,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Title
                Text(
                    text = page.title,
                    fontSize = 23.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = GoNagalNavy,
                    textAlign = TextAlign.Center,
                    lineHeight = 30.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Subtitle
                Text(
                    text = page.subtitle,
                    fontSize = 15.sp,
                    color = GoNagalTextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }
        }

        // Pager Dots Indicator
        Row(
            modifier = Modifier.padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(pages.size) { index ->
                val isSelected = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .height(8.dp)
                        .width(if (isSelected) 24.dp else 8.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) GoNagalGreen else GoNagalGreen.copy(alpha = 0.25f))
                )
            }
        }

        // Next / Get Started Action Button
        val isLastPage = pagerState.currentPage == pages.size - 1
        GoNagalPrimaryButton(
            text = if (isLastPage) "শপিং শুরু করুন" else "পরবর্তী",
            icon = if (!isLastPage) Icons.AutoMirrored.Filled.ArrowForward else Icons.Default.CheckCircle,
            onClick = {
                if (isLastPage) {
                    completeAndNavigate()
                } else {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                }
            },
            testTag = "onboarding_action_button"
        )
    }
}
