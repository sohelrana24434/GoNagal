package com.example.model

data class User(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val role: UserRole = UserRole.CUSTOMER,
    val avatarUrl: String? = null,
    val shopName: String? = null,
    val walletBalance: Double = 500.0,
    val isPhoneVerified: Boolean = true
)

sealed interface AuthState {
    data object Idle : AuthState
    data object Loading : AuthState
    data class Authenticated(val user: User) : AuthState
    data class Error(val message: String) : AuthState
}
