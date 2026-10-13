package com.example.shopeeclone.repository

import android.content.Context
import com.example.shopeeclone.model.Order
import com.example.shopeeclone.model.OrderStatus
import org.json.JSONArray
import org.json.JSONObject

object OrderRepository {

    private const val PREFS_NAME = "shopee_clone_orders"

    /** เดโม: ออเดอร์ "รอจัดส่ง" จะเปลี่ยนเป็น "กำลังจัดส่ง" เองหลังผ่านไปเท่านี้ (มิลลิวินาที) แก้ค่าได้ */
    private const val SHIPPING_AFTER_MS = 60_000L

    private fun storageKey(context: Context): String {
        val phone = UserRepository.getCurrentUser(context)?.phone ?: "guest"
        return "orders_$phone"
    }

    fun displayStatus(order: Order): String {
        if (order.status != OrderStatus.TO_SHIP) return order.status
        val since = if (order.paidAt > 0) order.paidAt else order.createdAt
        return if (System.currentTimeMillis() - since >= SHIPPING_AFTER_MS) OrderStatus.SHIPPING
        else OrderStatus.TO_SHIP
    }

    fun addOrder(context: Context, order: Order) {
        val orders = getOrders(context).toMutableList()
        orders.add(0, order)
        save(context, orders)
    }

    fun updateOrder(context: Context, updated: Order) {
        save(context, getOrders(context).map { if (it.id == updated.id) updated else it })
    }

    fun getOrders(context: Context): List<Order> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(storageKey(context), null) ?: return emptyList()

        val array = JSONArray(json)
        return (0 until array.length()).map { i ->
            val o = array.getJSONObject(i)
            Order(
                id = o.getString("id"),
                productName = o.getString("productName"),
                productPrice = o.getDouble("productPrice"),
                shippingMethod = o.getString("shippingMethod"),
                shippingFee = o.getDouble("shippingFee"),
                paymentMethod = o.getString("paymentMethod"),
                address = o.getString("address"),
                total = o.getDouble("total"),
                createdAt = o.getLong("createdAt"),
                status = o.getString("status"),
                recipientName = o.optString("recipientName", ""),
                recipientPhone = o.optString("recipientPhone", ""),
                voucherCode = o.optString("voucherCode", ""),
                voucherDiscount = o.optDouble("voucherDiscount", 0.0),
                coinsUsed = o.optInt("coinsUsed", 0),
                coinsEarned = o.optInt("coinsEarned", 0),
                rating = o.optInt("rating", 0),
                review = o.optString("review", ""),
                paidAt = o.optLong("paidAt", 0L)
            )
        }
    }

    private fun save(context: Context, orders: List<Order>) {
        val array = JSONArray()
        orders.forEach { order ->
            array.put(JSONObject().apply {
                put("id", order.id)
                put("productName", order.productName)
                put("productPrice", order.productPrice)
                put("shippingMethod", order.shippingMethod)
                put("shippingFee", order.shippingFee)
                put("paymentMethod", order.paymentMethod)
                put("address", order.address)
                put("total", order.total)
                put("createdAt", order.createdAt)
                put("status", order.status)
                put("recipientName", order.recipientName)
                put("recipientPhone", order.recipientPhone)
                put("voucherCode", order.voucherCode)
                put("voucherDiscount", order.voucherDiscount)
                put("coinsUsed", order.coinsUsed)
                put("coinsEarned", order.coinsEarned)
                put("rating", order.rating)
                put("review", order.review)
                put("paidAt", order.paidAt)
            })
        }
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
            .putString(storageKey(context), array.toString()).apply()
    }
}