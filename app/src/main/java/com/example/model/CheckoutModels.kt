package com.example.model

data class DeliveryAddress(
    val recipientName: String = "MD. Sohel Rana",
    val phone: String = "+880 1712-345678",
    val division: String = "Dhaka",
    val district: String = "Dhaka North",
    val area: String = "Banani, Road 11, Block D",
    val houseDetails: String = "House #45, Flat 3B",
    val addressType: String = "Home"
)

enum class PaymentMethodType(
    val title: String,
    val banglaTitle: String,
    val iconEmoji: String,
    val subtitle: String
) {
    CASH_ON_DELIVERY("Cash on Delivery (COD)", "ক্যাশ অন ডেলিভারি", "💵", "পণ্য হাতে পেয়ে মূল্য পরিশোধ করুন"),
    BKASH("bKash Online Payment", "বিকাশ পেমেন্ট", "📱", "তাৎক্ষণিক ১.৫% অতিরিক্ত ছাড়"),
    NAGAD("Nagad Payment", "নগদ পেমেন্ট", "💳", "সহজ ও দ্রুত ডিজিটাল পেমেন্ট"),
    ROCKET("Rocket / Upay", "রকেট / উপায়", "🏦", "মোবাইল ব্যাংকিং ওয়ালেট"),
    CARD("Debit / Credit Card", "কার্ড পেমেন্ট", "💳", "ভিসা, মাস্টারকার্ড ও এমেক্স")
}

data class OrderPlaced(
    val orderId: String,
    val orderDate: String,
    val items: List<ProductSelectionItem>,
    val deliveryAddress: DeliveryAddress,
    val paymentMethod: PaymentMethodType,
    val deliveryType: String, // "Home Delivery" or "Express Courier Hub"
    val subtotal: Double,
    val deliveryFee: Double,
    val totalAmount: Double,
    val status: String = "Order Confirmed"
)

data class ProductSelectionItem(
    val product: Product,
    val quantity: Int = 1,
    val selectedSize: String? = null,
    val selectedColor: String? = null
)
