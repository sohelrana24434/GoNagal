package com.example.data

import android.content.Context
import java.util.UUID

object GuestStorage {
    private const val PREFS_NAME = "gonagal_guest_prefs"
    private const val KEY_GUEST_ID = "key_guest_device_id"
    private const val KEY_ONBOARDING_COMPLETED = "key_onboarding_completed"
    private const val KEY_GUEST_NAME = "key_guest_customer_name"
    private const val KEY_GUEST_PHONE = "key_guest_phone"
    private const val KEY_GUEST_ADDRESS = "key_guest_address"
    private const val KEY_GUEST_DISTRICT = "key_guest_district"

    private const val KEY_GUEST_DIVISION = "key_guest_division"
    private const val KEY_GUEST_THANA = "key_guest_thana"

    fun getOrCreateGuestId(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val existing = prefs.getString(KEY_GUEST_ID, null)
        if (!existing.isNullOrBlank()) {
            return existing
        }
        val newGuestId = "GN-GUEST-${UUID.randomUUID().toString().take(8).uppercase()}"
        prefs.edit().putString(KEY_GUEST_ID, newGuestId).apply()
        return newGuestId
    }

    fun hasCompletedOnboarding(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_ONBOARDING_COMPLETED, false)
    }

    fun setOnboardingCompleted(context: Context, completed: Boolean = true) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_ONBOARDING_COMPLETED, completed).apply()
    }

    fun saveLastCustomerInfo(
        context: Context,
        name: String,
        phone: String,
        address: String,
        district: String,
        division: String = "ঢাকা (Dhaka)",
        thana: String = ""
    ) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putString(KEY_GUEST_NAME, name)
            .putString(KEY_GUEST_PHONE, phone)
            .putString(KEY_GUEST_ADDRESS, address)
            .putString(KEY_GUEST_DISTRICT, district)
            .putString(KEY_GUEST_DIVISION, division)
            .putString(KEY_GUEST_THANA, thana)
            .apply()
    }

    fun getLastCustomerName(context: Context): String {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_GUEST_NAME, "") ?: ""
    }

    fun getLastCustomerPhone(context: Context): String {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_GUEST_PHONE, "") ?: ""
    }

    fun getLastCustomerAddress(context: Context): String {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_GUEST_ADDRESS, "") ?: ""
    }

    fun getLastCustomerDistrict(context: Context): String {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_GUEST_DISTRICT, "ঢাকা সিটি (Dhaka City)") ?: "ঢাকা সিটি (Dhaka City)"
    }

    fun getLastCustomerDivision(context: Context): String {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_GUEST_DIVISION, "ঢাকা (Dhaka)") ?: "ঢাকা (Dhaka)"
    }

    fun getLastCustomerThana(context: Context): String {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_GUEST_THANA, "") ?: ""
    }
}
