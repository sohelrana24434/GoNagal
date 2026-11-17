package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.model.ChatMessage
import com.example.model.ChatSender
import com.example.model.GeminiContent
import com.example.model.GeminiGenerateRequest
import com.example.model.GeminiPart
import com.example.model.MockMarketplaceData
import com.example.network.GeminiApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class GeminiModelSelection(val modelId: String, val displayName: String, val speedLabel: String) {
    FLASH("gemini-3.5-flash", "Gemini 3.5 Flash", "Balanced & Smart"),
    PRO("gemini-3.1-pro-preview", "Gemini 3.1 Pro", "Deep Reasoning"),
    LITE("gemini-3.1-flash-lite-preview", "Gemini 3.1 Lite", "Ultra Fast")
}

class ChatViewModel : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                text = "আসসালামু আলাইকুম! আমি GoNagal AI স্মার্ট শপিং অ্যাসিস্ট্যান্ট (Gemini দ্বারা চালিত)।\n\nআপনাকে কীভাবে সাহায্য করতে পারি? যেমন: সেরা গ্যাজেট খোঁজা, ডিসকাউন্ট অফার, পাঞ্জাবি সাইজ গাইড, অথবা ডেলিভারি তথ্য জানতে পারেন!",
                sender = ChatSender.BOT,
                modelTag = "Gemini 3.5 Flash"
            )
        )
    )
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _selectedModel = MutableStateFlow(GeminiModelSelection.FLASH)
    val selectedModel: StateFlow<GeminiModelSelection> = _selectedModel.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun selectModel(model: GeminiModelSelection) {
        _selectedModel.value = model
    }

    fun sendMessage(userText: String) {
        if (userText.isBlank() || _isLoading.value) return

        val userMessage = ChatMessage(
            text = userText.trim(),
            sender = ChatSender.USER
        )

        _messages.value = _messages.value + userMessage
        _isLoading.value = true

        val currentModel = _selectedModel.value

        viewModelScope.launch {
            try {
                val apiKey = BuildConfig.GEMINI_API_KEY
                if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
                    // Fallback smart response if API key is not yet set in AI Studio Secrets
                    val fallbackReply = generateSmartLocalReply(userText.trim())
                    _messages.value = _messages.value + ChatMessage(
                        text = "$fallbackReply\n\n*(💡 টিপস: আপনি AI Studio Secrets প্যানেলে GEMINI_API_KEY কনফিগার করলে সরাসরি লাইভ ${currentModel.displayName} থেকে উত্তর পাবেন)*",
                        sender = ChatSender.BOT,
                        modelTag = "${currentModel.displayName} (Assistant)"
                    )
                    _isLoading.value = false
                    return@launch
                }

                // System Instruction to give chatbot the GoNagal shopping assistant role
                val catalogSummary = MockMarketplaceData.flashSaleProducts.joinToString(", ") { "${it.title} (৳${it.sellingPrice})" } +
                        "; " + MockMarketplaceData.regularProducts.joinToString(", ") { "${it.title} (৳${it.sellingPrice})" }

                val systemInstruction = GeminiContent(
                    role = "system",
                    parts = listOf(
                        GeminiPart(
                            text = "You are the official GoNagal E-Commerce Smart Assistant powered by Google Gemini. " +
                                    "Your tone is polite, helpful, and energetic in Bengali and English (Banglish/Bangla preferred). " +
                                    "You help customers find products, suggest deals, explain electronics, fashion sizes, delivery times in Dhaka (24-48 hours) and nationwide (3-5 days), payment methods (bKash, Nagad, COD). " +
                                    "Here are some current featured products in GoNagal: $catalogSummary. " +
                                    "Keep answers concise, formatted with emojis, and highly structured for a mobile screen."
                        )
                    )
                )

                // Multi-turn conversation history
                val contents = _messages.value.takeLast(10).map { msg ->
                    GeminiContent(
                        role = if (msg.sender == ChatSender.USER) "user" else "model",
                        parts = listOf(GeminiPart(text = msg.text))
                    )
                }

                val request = GeminiGenerateRequest(
                    contents = contents,
                    systemInstruction = systemInstruction
                )

                val response = GeminiApiClient.service.generateContent(
                    model = currentModel.modelId,
                    apiKey = apiKey,
                    request = request
                )

                val replyText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    ?: "দুঃখিত, কোনো উত্তর পাওয়া যায়নি। দয়া করে আবার চেষ্টা করুন।"

                _messages.value = _messages.value + ChatMessage(
                    text = replyText,
                    sender = ChatSender.BOT,
                    modelTag = currentModel.displayName
                )
            } catch (e: Exception) {
                // Graceful fallback with offline store intelligence
                val fallbackReply = generateSmartLocalReply(userText)
                _messages.value = _messages.value + ChatMessage(
                    text = "$fallbackReply\n\n*(নেটওয়ার্ক ব্যাহত হওয়ায় অফলাইন অ্যাসিস্ট্যান্ট মোড থেকে উত্তর দেওয়া হয়েছে: ${e.localizedMessage ?: "Connection error"})*",
                    sender = ChatSender.BOT,
                    isError = false,
                    modelTag = "${currentModel.displayName} (Offline)"
                )
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun generateSmartLocalReply(prompt: String): String {
        val lower = prompt.lowercase()
        return when {
            lower.contains("ডিসকাউন্ট") || lower.contains("offer") || lower.contains("ছাড়") || lower.contains("sale") -> {
                "🎉 **GoNagal সুপার সেভার অফার!**\n\n• Wireless Noise Cancelling Earbuds Pro: ৪৭% ছাড়ে মাত্র ৳১,৮৫০!\n• Smart Fitness Watch AMOLED: ৪১% ছাড়ে ৳২,৪৯০!\n• 65W GaN Fast Charger: ৪৩% ছাড়ে ৳১,২৫০!\n\nফ্ল্যাশ সেল সেকশনে গিয়ে কাউন্টডাউন শেষ হওয়ার আগেই অর্ডার করতে পারেন।"
            }
            lower.contains("ডেলিভারি") || lower.contains("delivery") || lower.contains("সময়") || lower.contains("খরচ") -> {
                "🚚 **ডেলিভারি সংক্রান্ত তথ্য:**\n\n• ঢাকা মেট্রো এরিয়া: ২৪ থেকে ৪৮ ঘণ্টার মধ্যে ডেলিভারি (চার্জ মাত্র ৳৬০)।\n• সারা বাংলাদেশ: ৩ থেকে ৫ কর্মদিবসের মধ্যে নির্ভরযোগ্য কুরিয়ারে ক্যাশ অন ডেলিভারি (COD)।"
            }
            lower.contains("পাঞ্জাবি") || lower.contains("panjabi") || lower.contains("শার্ট") || lower.contains("fashion") -> {
                "👔 **GoNagal প্রিমিয়াম ফ্যাশন:**\nআমাদের খাঁটি ১০০% কম্বড সুতির 'LoomCraft Slim Fit Panjabi' কালেকশন এখন লাইভ। প্রিমিয়াম কাটিং ও সাইজ (M, L, XL, XXL) রয়েছে। মূল্য মাত্র ৳১,৬৫০ (অরিজিনাল ৳২,৮০০)।"
            }
            lower.contains("মোবাইল") || lower.contains("phone") || lower.contains("গ্যাজেট") || lower.contains("watch") || lower.contains("ঘড়ি") -> {
                "📱 **টপ রেটেড গ্যাজেটস:**\n• FitPulse Smartwatch AMOLED ডিসপ্লে, হার্ট রেট ও স্লিপ ট্র্যাকিং সহ।\n• AnkerX 10000mAh ম্যাগনেটিক পাওয়ার ব্যাংক।\nসব গ্যাজেটে রয়েছে অফিসিয়াল ব্র্যান্ড ওয়ারেন্টি!"
            }
            lower.contains("পেমেন্ট") || lower.contains("টাকা") || lower.contains("payment") || lower.contains("bkash") || lower.contains("nagad") -> {
                "💳 **পেমেন্ট অপশন:**\nGoNagal-এ আপনি বিকাশ (bKash), নগদ (Nagad), রকেট, ডেবিট/ক্রেডিট কার্ড এবং পণ্য হাতে পেয়ে ক্যাশ অন ডেলিভারি (COD) মাধ্যমে নিশ্চিন্তে পেমেন্ট করতে পারবেন।"
            }
            else -> {
                "আমি আপনার বার্তাটি পেয়েছি: '$prompt'।\n\nGoNagal-এ গ্যাজেটস, ইলেকট্রনিক্স, পুরুষ ও নারীদের ফ্যাশন এবং নিত্যপ্রয়োজনীয় গ্রোসারি পণ্যের ভেরিফাইড কালেকশন রয়েছে। আপনি কোনো নির্দিষ্ট আইটেম বা ব্র্যান্ড খুঁজলে বলুন, আমি এখনই খুঁজে দিচ্ছি!"
            }
        }
    }

    fun clearHistory() {
        _messages.value = listOf(
            ChatMessage(
                text = "কথোপকথন রিসেট করা হয়েছে। আপনার কেনাকাটায় কী সাহায্য করতে পারি?",
                sender = ChatSender.BOT,
                modelTag = _selectedModel.value.displayName
            )
        )
    }
}
