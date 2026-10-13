package com.example.shopeeclone.repository

import android.content.Context

object WishlistRepository {
    private const val PREFS_NAME = "shopee_clone_wishlist"

    private fun key(context: Context) =
        "wishlist_" + (UserRepository.getCurrentUser(context)?.phone ?: "guest")

    fun getIds(context: Context): Set<Int> =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getStringSet(key(context), emptySet())
            .orEmpty()
            .mapNotNull { it.toIntOrNull() }
            .toSet()

    fun isWishlisted(context: Context, productId: Int) = productId in getIds(context)

    /** คืนค่า true ถ้าหลังกดแล้วสินค้าอยู่ในรายการถูกใจ */
    fun toggle(context: Context, productId: Int): Boolean {
        val ids = getIds(context).toMutableSet()
        val nowWishlisted = ids.add(productId)
        if (!nowWishlisted) ids.remove(productId)
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
            .putStringSet(key(context), ids.map { it.toString() }.toSet())
            .apply()
        return nowWishlisted
    }
}