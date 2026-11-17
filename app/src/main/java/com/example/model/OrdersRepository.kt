package com.example.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.GoNagalGreen
import com.example.ui.theme.GoNagalOrange

data class OrderItem(
    val orderId: String,
    val date: String,
    val itemCount: Int,
    val totalAmount: String,
    var status: String,
    var statusColor: Color,
    val iconEmoji: String,
    val productName: String,
    val deliveryType: String = "Home Delivery",
    val paymentMethod: String = "Cash on Delivery",
    val destination: String = "Dhaka City",
    val customerName: String = "সম্মানিত ক্রেতা",
    val customerPhone: String = "017XXXXXXXX",
    val customerAddress: String = "ঢাকা সিটি",
    val division: String = "ঢাকা (Dhaka)",
    val district: String = "ঢাকা",
    val thana: String = "সদর",
    val guestId: String = "",
    var isCancelled: Boolean = false,
    var cancelReason: String? = null
)

object OrdersRepository {
    val orderList = mutableListOf(
        OrderItem(
            orderId = "GN-92841",
            date = "Today, 02:45 PM",
            itemCount = 2,
            totalAmount = "৳4,340",
            status = "Processing in Hub",
            statusColor = Color(0xFF3B82F6),
            iconEmoji = "🎧",
            productName = "Wireless Noise Cancelling Earbuds Pro + 1 more",
            deliveryType = "Home Delivery (Dhaka)",
            paymentMethod = "Cash on Delivery (ক্যাশ অন ডেলিভারি)",
            destination = "মিরপুর ১০, ঢাকা",
            customerName = "তানভীর হোসেন",
            customerPhone = "01712345678",
            customerAddress = "রোড #২, ব্লক #বি, মিরপুর ১০",
            division = "ঢাকা",
            district = "ঢাকা",
            thana = "মিরপুর",
            guestId = "SAMPLE"
        ),
        OrderItem(
            orderId = "GN-88120",
            date = "08 Sep 2026",
            itemCount = 1,
            totalAmount = "৳2,490",
            status = "Out for Delivery",
            statusColor = GoNagalOrange,
            iconEmoji = "⌚",
            productName = "Smart Fitness Watch AMOLED Display",
            deliveryType = "Home Delivery",
            paymentMethod = "Cash on Delivery (ক্যাশ অন ডেলিভারি)",
            destination = "ধানমন্ডি, ঢাকা",
            customerName = "তানভীর হোসেন",
            customerPhone = "01712345678",
            customerAddress = "বাড়ি #৪৫, রোড #৭/এ, ধানমন্ডি",
            division = "ঢাকা",
            district = "ঢাকা",
            thana = "ধানমন্ডি",
            guestId = "SAMPLE"
        ),
        OrderItem(
            orderId = "GN-71932",
            date = "29 Aug 2026",
            itemCount = 3,
            totalAmount = "৳5,280",
            status = "Delivered",
            statusColor = GoNagalGreen,
            iconEmoji = "👔",
            productName = "Premium Pure Cotton Slim Fit Panjabi",
            deliveryType = "Home Delivery",
            paymentMethod = "Cash on Delivery (ক্যাশ অন ডেলিভারি)",
            destination = "উত্তরা, ঢাকা",
            customerName = "তানভীর হোসেন",
            customerPhone = "01712345678",
            customerAddress = "সেক্টর ৩, উত্তরা, ঢাকা",
            division = "ঢাকা",
            district = "ঢাকা",
            thana = "উত্তরা",
            guestId = "SAMPLE"
        )
    )

    /**
     * Enforces mandatory collection of customer name, phone, and complete delivery address.
     * Prevents empty or invalid orders from being registered in the system.
     */
    fun addNewOrder(
        productName: String,
        itemCount: Int,
        totalAmountFormatted: String,
        iconEmoji: String,
        deliveryType: String,
        paymentMethod: String,
        destination: String,
        customerName: String = "",
        customerPhone: String = "",
        customerAddress: String = "",
        division: String = "",
        district: String = "",
        thana: String = "",
        guestId: String = ""
    ): String {
        val trimmedName = customerName.trim()
        val trimmedPhone = customerPhone.trim()
        val trimmedAddress = customerAddress.trim().ifEmpty { destination.trim() }

        // Mandatory validation check at repository level
        require(trimmedName.isNotBlank()) {
            "গ্রাহকের সম্পূর্ণ নাম প্রদান করা বাধ্যতামূলক।"
        }
        require(trimmedPhone.isNotBlank() && trimmedPhone.length >= 10) {
            "সঠিক ১১ ডিজিটের সচল মোবাইল নম্বর প্রদান করা বাধ্যতামূলক।"
        }
        require(trimmedAddress.isNotBlank()) {
            "সম্পূর্ণ ডেলিভারি ঠিকানা প্রদান করা বাধ্যতামূলক।"
        }

        val newId = "GN-" + (100000..999999).random()
        val newOrder = OrderItem(
            orderId = newId,
            date = "Just now (আজকে)",
            itemCount = itemCount,
            totalAmount = totalAmountFormatted,
            status = "Order Placed & Confirmed",
            statusColor = GoNagalGreen,
            iconEmoji = iconEmoji,
            productName = productName,
            deliveryType = deliveryType,
            paymentMethod = paymentMethod,
            destination = trimmedAddress,
            customerName = trimmedName,
            customerPhone = trimmedPhone,
            customerAddress = trimmedAddress,
            division = division.ifEmpty { "ঢাকা (Dhaka)" },
            district = district.ifEmpty { "ঢাকা" },
            thana = thana.ifEmpty { "সদর" },
            guestId = guestId
        )
        orderList.add(0, newOrder)
        return newId
    }

    fun cancelOrder(orderId: String, reason: String): Boolean {
        val order = orderList.find { it.orderId == orderId } ?: return false
        order.status = "Cancelled (বাতিল করা হয়েছে)"
        order.statusColor = Color(0xFFEF4444)
        order.isCancelled = true
        order.cancelReason = reason
        return true
    }

    fun getOrdersForGuest(guestId: String): List<OrderItem> {
        return orderList.filter { it.guestId.isEmpty() || it.guestId == guestId || it.guestId == "SAMPLE" }
    }
}
