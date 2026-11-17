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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.ui.components.GoNagalTextField
import com.example.ui.components.LogoSize
import com.example.ui.theme.GoNagalBackground
import com.example.ui.theme.GoNagalBorder
import com.example.ui.theme.GoNagalGreen
import com.example.ui.theme.GoNagalNavy
import com.example.ui.theme.GoNagalRed
import com.example.ui.theme.GoNagalTextPrimary
import com.example.ui.theme.GoNagalTextSecondary
import com.example.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    authViewModel: AuthViewModel,
    onRegisterSuccess: () -> Unit,
    onNavigateLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    val authState by authViewModel.authState.collectAsState()
    val scrollState = rememberScrollState()

    var nameInput by remember { mutableStateOf("") }
    var contactInput by remember { mutableStateOf("") }
    var shopNameInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(UserRole.CUSTOMER) }
    var agreedToTerms by remember { mutableStateOf(true) }

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
            .testTag("register_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Brand Logo
        GoNagalBrandLogo(
            logoSize = LogoSize.MEDIUM,
            showTagline = true,
            taglineInBengali = true
        )

        Spacer(modifier = Modifier.height(20.dp))

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
                    text = "Create GoNagal Account",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoNagalNavy
                )
                Text(
                    text = "Join Bangladesh's premier multi-vendor platform",
                    fontSize = 13.sp,
                    color = GoNagalTextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )

                // Role Selector Radio Pills
                Text(
                    text = "I want to register as:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = GoNagalTextSecondary,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        UserRole.CUSTOMER to "Shopper",
                        UserRole.SELLER to "Merchant/Seller",
                        UserRole.AFFILIATE to "Affiliate"
                    ).forEach { (role, label) ->
                        val isSelected = selectedRole == role
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) GoNagalGreen else Color(0xFFF1F5F9),
                            border = if (isSelected) null else BorderStroke(1.dp, GoNagalBorder),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedRole = role }
                        ) {
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else GoNagalNavy,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 10.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Name Input
                GoNagalTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = "Full Name",
                    placeholder = "Enter your full name",
                    leadingIcon = Icons.Default.Person,
                    testTag = "register_name_input"
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Mobile / Email Input
                GoNagalTextField(
                    value = contactInput,
                    onValueChange = { contactInput = it },
                    label = "Mobile Number or Email",
                    placeholder = "017XXXXXXXX or user@mail.com",
                    leadingIcon = Icons.Default.Phone,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    testTag = "register_contact_input"
                )

                // Seller specific: Shop Name
                if (selectedRole == UserRole.SELLER) {
                    Spacer(modifier = Modifier.height(14.dp))
                    GoNagalTextField(
                        value = shopNameInput,
                        onValueChange = { shopNameInput = it },
                        label = "Shop / Merchant Name",
                        placeholder = "e.g., Dhaka Fashion Gallery",
                        leadingIcon = Icons.Default.Storefront,
                        testTag = "register_shop_name_input"
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Password Input
                GoNagalTextField(
                    value = passwordInput,
                    onValueChange = { passwordInput = it },
                    label = "Password (min 6 characters)",
                    placeholder = "Create a strong password",
                    leadingIcon = Icons.Default.Lock,
                    isPassword = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    testTag = "register_password_input"
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Terms agreement
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = agreedToTerms,
                        onCheckedChange = { agreedToTerms = it },
                        colors = CheckboxDefaults.colors(checkedColor = GoNagalGreen)
                    )
                    Text(
                        text = "I agree to GoNagal Terms of Service, Privacy Policy & Marketplace Rules",
                        fontSize = 12.sp,
                        color = GoNagalTextSecondary,
                        lineHeight = 16.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage,
                        color = GoNagalRed,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Submit Button
                GoNagalPrimaryButton(
                    text = if (selectedRole == UserRole.SELLER) "Register as Seller" else "Create Account",
                    isLoading = isLoading,
                    enabled = agreedToTerms,
                    onClick = {
                        authViewModel.registerAccount(
                            name = nameInput,
                            phoneOrEmail = contactInput,
                            pass = passwordInput,
                            role = selectedRole,
                            shopName = if (selectedRole == UserRole.SELLER) shopNameInput else null,
                            onSuccess = onRegisterSuccess
                        )
                    },
                    testTag = "register_submit_button"
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Back to Login Prompt
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Already have an account? ",
                fontSize = 14.sp,
                color = GoNagalTextSecondary
            )
            Text(
                text = "Sign In",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = GoNagalGreen,
                modifier = Modifier
                    .clickable { onNavigateLogin() }
                    .testTag("login_link")
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
