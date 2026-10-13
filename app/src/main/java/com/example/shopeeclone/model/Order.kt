package com.example.shopeeclone.model

data class Order(
    val id: String,
    val productName: String,
    val productPrice: Double,
    val shippingMethod: String,
    val shippingFee: Double,
    val paymentMethod: String,
    val address: String,
    val total: Double,
    val createdAt: Long,
    val status: String,
    val recipientName: String = "",
    val recipientPhone: String = "",
    val voucherCode: String = "",
    val voucherDiscount: Double = 0.0,
    val coinsUsed: Int = 0,
    val coinsEarned: Int = 0,
    val rating: Int = 0,
    val review: String = "",
    val paidAt: Long = 0L
)

object OrderStatus {
    const val PENDING_PAYMENT = "รอชำระเงิน"
    const val TO_SHIP = "รอจัดส่ง"
    const val SHIPPING = "กำลังจัดส่ง"   // สถานะที่คำนวณจากเวลา ไม่ถูกเก็บลงเครื่อง
    const val COMPLETED = "สำเร็จ"
    const val CANCELLED = "ยกเลิกแล้ว"
}