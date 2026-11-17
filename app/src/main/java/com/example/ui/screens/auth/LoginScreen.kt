package com.example.ui.screens.auth

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AuthState
import com.example.model.UserRole
import com.example.ui.components.GoNagalBrandLogo
import com.example.ui.components.GoNagalPrimaryButton
import com.example.ui.components.GoNagalSecondaryButton
import com.example.ui.components.GoNagalTextField
import com.example.ui.components.LogoSize
import com.example.ui.theme.GoNagalBackground
import com.example.ui.theme.GoNagalBorder
import com.example.ui.theme.GoNagalGreen
import com.example.ui.theme.GoNagalGreenContainer
import com.example.ui.theme.GoNagalNavy
import com.example.ui.theme.GoNagalOrange
import com.example.ui.theme.GoNagalRed
import com.example.ui.theme.GoNagalTextMuted
import com.example.ui.theme.GoNagalTextPrimary
import com.example.ui.theme.GoNagalTextSecondary
import com.example.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateRegister: () -> Unit,
    onContinueGuest: () -> Unit,
    modifier: Modifier = Modifier
) {
    val authState by authViewModel.authState.collectAsState()
    val scrollState = rememberScrollState()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Phone, 1: Email
    var phoneInput by remember { mutableStateOf("01712345678") }
    var emailInput by remember { mutableStateOf("customer@gonagal.com") }
    var passwordInput by remember { mutableStateOf("pass1234") }
    var selectedRole by remember { mutableStateOf(UserRole.CUSTOMER) }

    val isLoading = authState is AuthState.Loading
    val errorMessage = (authState as? AuthState.Error)?.message

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GoNagalBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp, vertical = 20.dp)
            .testTag("login_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // GoNagal Brand Logo
        GoNagalBrandLogo(
            logoSize = LogoSize.MEDIUM,
            showTagline = true,
            taglineInBengali = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Main Login Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Welcome to GoNagal",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoNagalNavy
                )
                Text(
                    text = "Sign in to access your multi-vendor marketplace",
                    fontSize = 13.sp,
                    color = GoNagalTextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )

                // Tab Toggle: Phone vs Email
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color(0xFFF1F5F9),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp)),
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = GoNagalGreen,
                            height = 3.dp
                        )
                    },
                    divider = {}
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Phone,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = if (selectedTab == 0) GoNagalGreen else GoNagalTextMuted
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "Mobile",
                                    fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                                    color = if (selectedTab == 0) GoNagalGreen else GoNagalTextSecondary
                                )
                            }
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = if (selectedTab == 1) GoNagalGreen else GoNagalTextMuted
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "Email",
                                    fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                                    color = if (selectedTab == 1) GoNagalGreen else GoNagalTextSecondary
                                )
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Input field
                if (selectedTab == 0) {
                    GoNagalTextField(
                        value = phoneInput,
                        onValueChange = { phoneInput = it },
                        label = "Mobile Number",
                        placeholder = "017XXXXXXXX",
                        leadingIcon = Icons.Default.Phone,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        testTag = "login_phone_input"
                    )
                } else {
                    GoNagalTextField(
                        value = emailInput,
                        onValueChange = { emailInput = it },
                        label = "Email Address",
                        placeholder = "example@mail.com",
                        leadingIcon = Icons.Default.Email,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        testTag = "login_email_input"
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Password input
                GoNagalTextField(
                    value = passwordInput,
                    onValueChange = { passwordInput = it },
                    label = "Password",
                    placeholder = "Enter your password",
                    leadingIcon = Icons.Default.Lock,
                    isPassword = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    testTag = "login_password_input"
                )

                // Role Demo Selector Pills (For easy role testing as required in User Types specification)
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Sign in as:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = GoNagalTextSecondary
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(UserRole.CUSTOMER, UserRole.SELLER, UserRole.ADMIN).forEach { role ->
                            val isSelected = selectedRole == role
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) GoNagalGreen else Color(0xFFF1F5F9),
                                border = if (isSelected) null else BorderStroke(1.dp, GoNagalBorder),
                                modifier = Modifier.clickable { selectedRole = role }
                            ) {
                                Text(
                                    text = role.title,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isSelected) Color.White else GoNagalNavy,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                // Error message
                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = errorMessage,
                        color = GoNagalRed,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Primary Login Button
                GoNagalPrimaryButton(
                    text = "Sign In as ${selectedRole.title}",
                    isLoading = isLoading,
                    onClick = {
                        val identifier = if (selectedTab == 0) phoneInput else emailInput
                        authViewModel.loginWithPhoneOrEmail(
                            identifier = identifier,
                            pass = passwordInput,
                            selectedRole = selectedRole,
                            onSuccess = onLoginSuccess
                        )
                    },
                    testTag = "login_submit_button"
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Forgot password
                TextButton(
                    onClick = { /* Password recovery dialog */ },
                    modifier = Modifier.height(36.dp)
                ) {
                    Text(
                        text = "Forgot Password?",
                        color = GoNagalNavy,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Divider
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = GoNagalBorder)
            Text(
                text = "OR",
                fontSize = 12.sp,
                color = GoNagalTextMuted,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 14.dp)
            )
            HorizontalDivider(modifier = Modifier.weight(1f), color = GoNagalBorder)
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Continue as Guest Button (Instant marketplace access)
        GoNagalSecondaryButton(
            text = "Continue as Guest Shopper",
            icon = Icons.Default.Person,
            onClick = {
                authViewModel.continueAsGuest(onSuccess = onContinueGuest)
            },
            testTag = "guest_login_button"
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Register Prompt
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Don't have an account? ",
                fontSize = 14.sp,
                color = GoNagalTextSecondary
            )
            Text(
                text = "Register Now",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = GoNagalGreen,
                modifier = Modifier
                    .clickable { onNavigateRegister() }
                    .testTag("register_now_link")
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}
