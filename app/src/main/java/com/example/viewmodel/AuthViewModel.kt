package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.AuthState
import com.example.model.User
import com.example.model.UserRole
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val defaultCustomer = User(
        id = "USR-9921",
        name = "GoNagal Customer",
        email = "customer@gonagal.com",
        phone = "+880 1712-345678",
        role = UserRole.CUSTOMER,
        walletBalance = 1250.0
    )

    private val _currentUser = MutableStateFlow<User?>(defaultCustomer)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Authenticated(defaultCustomer))
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _activeRole = MutableStateFlow(UserRole.CUSTOMER)
    val activeRole: StateFlow<UserRole> = _activeRole.asStateFlow()

    fun loginWithPhoneOrEmail(
        identifier: String,
        pass: String,
        selectedRole: UserRole,
        onSuccess: () -> Unit
    ) {
        if (identifier.isBlank() || pass.isBlank()) {
            _authState.value = AuthState.Error("Please enter your mobile/email and password")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            // Simulate realistic API delay
            delay(800)

            val loggedInUser = User(
                id = "USR-${System.currentTimeMillis() % 10000}",
                name = when (selectedRole) {
                    UserRole.SELLER -> "Dhaka Tech Hub"
                    UserRole.ADMIN -> "GoNagal SuperAdmin"
                    UserRole.AFFILIATE -> "Rahim Promoter"
                    else -> "Sohel Rana"
                },
                email = if (identifier.contains("@")) identifier else "$identifier@gonagal.com",
                phone = if (!identifier.contains("@")) identifier else "+880 1712-345678",
                role = selectedRole,
                shopName = if (selectedRole == UserRole.SELLER) "Dhaka Tech Hub" else null,
                walletBalance = when (selectedRole) {
                    UserRole.SELLER -> 45200.0
                    UserRole.AFFILIATE -> 3450.0
                    else -> 1250.0
                }
            )

            _currentUser.value = loggedInUser
            _activeRole.value = selectedRole
            _authState.value = AuthState.Authenticated(loggedInUser)
            onSuccess()
        }
    }

    fun registerAccount(
        name: String,
        phoneOrEmail: String,
        pass: String,
        role: UserRole,
        shopName: String? = null,
        onSuccess: () -> Unit
    ) {
        if (name.isBlank() || phoneOrEmail.isBlank() || pass.isBlank()) {
            _authState.value = AuthState.Error("All fields are required")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            delay(1000)

            val newUser = User(
                id = "USR-${System.currentTimeMillis() % 10000}",
                name = name,
                email = if (phoneOrEmail.contains("@")) phoneOrEmail else "$phoneOrEmail@gonagal.com",
                phone = if (!phoneOrEmail.contains("@")) phoneOrEmail else "+880 1812-987654",
                role = role,
                shopName = shopName,
                walletBalance = 100.0 // Welcome bonus
            )

            _currentUser.value = newUser
            _activeRole.value = role
            _authState.value = AuthState.Authenticated(newUser)
            onSuccess()
        }
    }

    fun continueAsGuest(onSuccess: () -> Unit) {
        val guest = User(
            id = "GUEST-${System.currentTimeMillis() % 1000}",
            name = "Guest Shopper",
            email = "guest@gonagal.com",
            phone = "+880 1000-000000",
            role = UserRole.GUEST,
            walletBalance = 0.0
        )
        _currentUser.value = guest
        _activeRole.value = UserRole.GUEST
        _authState.value = AuthState.Authenticated(guest)
        onSuccess()
    }

    fun switchRole(role: UserRole) {
        _activeRole.value = role
        val current = _currentUser.value
        if (current != null) {
            _currentUser.value = current.copy(
                role = role,
                shopName = if (role == UserRole.SELLER) (current.shopName ?: "GoNagal Express Shop") else current.shopName
            )
        }
    }

    fun logout() {
        _currentUser.value = null
        _activeRole.value = UserRole.CUSTOMER
        _authState.value = AuthState.Idle
    }

    fun clearError() {
        if (_authState.value is AuthState.Error) {
            _authState.value = AuthState.Idle
        }
    }
}
