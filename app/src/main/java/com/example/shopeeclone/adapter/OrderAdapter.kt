package com.example.shopeeclone.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.example.shopeeclone.databinding.ItemOrderBinding
import com.example.shopeeclone.model.Order
import com.example.shopeeclone.model.OrderStatus
import com.example.shopeeclone.repository.OrderRepository
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class OrderAction { PAY, CANCEL, RECEIVED, REVIEW }

class OrderAdapter(
    private val orders: List<Order>,
    private val onAction: (Order, OrderAction) -> Unit
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    inner class OrderViewHolder(val binding: ItemOrderBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun getItemCount(): Int = orders.size

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        val status = OrderRepository.displayStatus(order)
        val money = NumberFormat.getNumberInstance(Locale("th", "TH"))
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("th", "TH"))

        with(holder.binding) {
            tvOrderId.text = "คำสั่งซื้อ #${order.id}"
            tvOrderStatus.text = status
            tvOrderDate.text = dateFormat.format(Date(order.createdAt))
            tvProductName.text = "${order.productName} x1"
            tvShippingPayment.text = "${order.shippingMethod} | ${order.paymentMethod}"

            tvRecipient.visibility = if (order.recipientName.isEmpty()) View.GONE else View.VISIBLE
            tvRecipient.text = "ผู้รับ: ${order.recipientName} (${order.recipientPhone})"
            tvAddress.text = "ที่อยู่: ${order.address}"

            val discounts = mutableListOf<String>()
            if (order.voucherDiscount > 0) discounts.add("คูปอง -฿${money.format(order.voucherDiscount)}")
            if (order.coinsUsed > 0) discounts.add("เหรียญ -฿${order.coinsUsed}")
            tvDiscount.visibility = if (discounts.isEmpty()) View.GONE else View.VISIBLE
            tvDiscount.text = "ส่วนลด: " + discounts.joinToString(", ")

            tvTotal.text = "ยอดรวม ฿${money.format(order.total)}"

            if (status == OrderStatus.COMPLETED && order.rating > 0) {
                tvReview.visibility = View.VISIBLE
                tvReview.text = "★".repeat(order.rating) + "☆".repeat(5 - order.rating) +
                        if (order.review.isNotBlank()) "\n${order.review}" else ""
            } else {
                tvReview.visibility = View.GONE
            }

            val primary: Pair<String, OrderAction>? = when (status) {
                OrderStatus.PENDING_PAYMENT -> "ชำระเงิน" to OrderAction.PAY
                OrderStatus.SHIPPING -> "ได้รับสินค้าแล้ว" to OrderAction.RECEIVED
                OrderStatus.COMPLETED -> if (order.rating == 0) "ให้คะแนน" to OrderAction.REVIEW else null
                else -> null
            }
            val secondary: Pair<String, OrderAction>? =
                if (status == OrderStatus.PENDING_PAYMENT || status == OrderStatus.TO_SHIP)
                    "ยกเลิกคำสั่งซื้อ" to OrderAction.CANCEL else null

            bindButton(btnPrimary, primary, order)
            bindButton(btnSecondary, secondary, order)
            llActions.visibility = if (primary == null && secondary == null) View.GONE else View.VISIBLE
        }
    }

    private fun bindButton(button: Button, spec: Pair<String, OrderAction>?, order: Order) {
        if (spec == null) {
            button.visibility = View.GONE
        } else {
            button.visibility = View.VISIBLE
            button.text = spec.first
            button.setOnClickListener { onAction(order, spec.second) }
        }
    }
}