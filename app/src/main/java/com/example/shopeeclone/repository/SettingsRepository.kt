package com.example.shopeeclone.repository

import android.content.Context

object SettingsRepository {
    private const val PREFS_NAME = "shopee_clone_settings"
    private const val KEY_DEFAULT_PAYMENT = "default_payment"

    const val KEY_NOTIFY_PROMO = "notify_promo"
    const val KEY_NOTIFY_ORDER = "notify_order"
    const val KEY_NOTIFY_CHAT = "notify_chat"
    const val KEY_DARK_MODE = "dark_mode"

    val PAYMENT_LABELS = listOf(
        "เก็บเงินปลายทาง (COD)", "โอนผ่านธนาคาร", "บัตรเครดิต/เดบิต", "พร้อมเพย์ / e-Wallet"
    )
    val PAYMENT_SHORT_LABELS = listOf("COD", "โอนธนาคาร", "บัตร", "พร้อมเพย์")

    fun getBoolean(context: Context, key: String, default: Boolean): Boolean =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getBoolean(key, default)

    fun setBoolean(context: Context, key: String, value: Boolean) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().putBoolean(key, value).apply()
    }

    fun isDarkMode(context: Context) = getBoolean(context, KEY_DARK_MODE, false)

    fun getDefaultPayment(context: Context): Int =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getInt(KEY_DEFAULT_PAYMENT, 0)

    fun setDefaultPayment(context: Context, index: Int) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
            .putInt(KEY_DEFAULT_PAYMENT, index).apply()
    }
}