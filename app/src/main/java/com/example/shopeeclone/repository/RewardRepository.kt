package com.example.shopeeclone.repository

import android.content.Context
import com.example.shopeeclone.model.OrderStatus

data class Voucher(
    val code: String,
    val title: String,
    val discount: Double,
    val minSpend: Double
)

/**
 * เหรียญและคูปองคำนวณจากประวัติคำสั่งซื้อ จึงไม่ต้องเก็บยอดแยก
 *  - เหรียญ = โบนัสต้อนรับ + เหรียญที่ได้จากออเดอร์ "สำเร็จ" - เหรียญที่ใช้ (ออเดอร์ที่ไม่ถูกยกเลิก)
 *  - คูปองใช้ได้ครั้งเดียว ยกเลิกออเดอร์แล้วคืนสิทธิ์ให้
 */
object RewardRepository {
    const val WELCOME_COINS = 100
    private const val BAHT_PER_COIN = 10

    val vouchers = listOf(
        Voucher("NEW30", "ส่วนลด ฿30", 30.0, 0.0),
        Voucher("WELCOME50", "ส่วนลด ฿50", 50.0, 300.0),
        Voucher("SAVE100", "ส่วนลด ฿100", 100.0, 800.0)
    )

    fun coinsFor(total: Double): Int = (total / BAHT_PER_COIN).toInt()

    fun usedVoucherCodes(context: Context): Set<String> =
        OrderRepository.getOrders(context)
            .filter { it.status != OrderStatus.CANCELLED && it.voucherCode.isNotEmpty() }
            .map { it.voucherCode }
            .toSet()

    fun availableVouchers(context: Context, spend: Double): List<Voucher> {
        val used = usedVoucherCodes(context)
        return vouchers.filter { it.code !in used && spend >= it.minSpend }
    }

    fun coinBalance(context: Context): Int {
        val orders = OrderRepository.getOrders(context).filter { it.status != OrderStatus.CANCELLED }
        val earned = orders.filter { it.status == OrderStatus.COMPLETED }.sumOf { it.coinsEarned }
        val used = orders.sumOf { it.coinsUsed }
        return WELCOME_COINS + earned - used
    }
}