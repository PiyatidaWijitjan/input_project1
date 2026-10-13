package com.example.shopeeclone.ui.orders

import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RatingBar
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shopeeclone.MainActivity
import com.example.shopeeclone.adapter.OrderAction
import com.example.shopeeclone.adapter.OrderAdapter
import com.example.shopeeclone.databinding.ActivityOrderHistoryBinding
import com.example.shopeeclone.model.Order
import com.example.shopeeclone.model.OrderStatus
import com.example.shopeeclone.repository.OrderRepository
import com.google.android.material.tabs.TabLayout
import java.text.NumberFormat
import java.util.Locale

class OrderHistoryActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_FROM_CHECKOUT = "extra_from_checkout"
        const val EXTRA_TAB = "extra_tab"
        const val TAB_ALL = 0
        const val TAB_TO_PAY = 1
        const val TAB_TO_SHIP = 2
        const val TAB_SHIPPING = 3
        const val TAB_TO_REVIEW = 4
    }

    private val tabTitles = listOf("ทั้งหมด", "รอชำระเงิน", "รอจัดส่ง", "กำลังจัดส่ง", "ให้คะแนน")

    private lateinit var binding: ActivityOrderHistoryBinding
    private var fromCheckout = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fromCheckout = intent.getBooleanExtra(EXTRA_FROM_CHECKOUT, false)
        binding.toolbar.setNavigationOnClickListener { exitScreen() }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() = exitScreen()
        })

        binding.rvOrders.layoutManager = LinearLayoutManager(this)

        tabTitles.forEach { binding.tabLayout.addTab(binding.tabLayout.newTab().setText(it)) }
        binding.tabLayout.getTabAt(intent.getIntExtra(EXTRA_TAB, TAB_ALL))?.select()
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) = refresh()
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    private fun refresh() {
        val tab = binding.tabLayout.selectedTabPosition
        val filtered = OrderRepository.getOrders(this).filter { matches(it, tab) }
        binding.tvEmpty.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
        binding.rvOrders.adapter = OrderAdapter(filtered) { order, action -> handleAction(order, action) }
    }

    private fun matches(order: Order, tab: Int): Boolean {
        val status = OrderRepository.displayStatus(order)
        return when (tab) {
            TAB_ALL -> true
            TAB_TO_PAY -> status == OrderStatus.PENDING_PAYMENT
            TAB_TO_SHIP -> status == OrderStatus.TO_SHIP
            TAB_SHIPPING -> status == OrderStatus.SHIPPING
            TAB_TO_REVIEW -> status == OrderStatus.COMPLETED && order.rating == 0
            else -> false
        }
    }

    private fun handleAction(order: Order, action: OrderAction) {
        when (action) {
            OrderAction.PAY -> {
                val total = NumberFormat.getNumberInstance(Locale("th", "TH")).format(order.total)
                AlertDialog.Builder(this)
                    .setTitle("ชำระเงิน")
                    .setMessage("ยอดชำระ ฿$total (${order.paymentMethod})\n\nนี่คือการจำลองการชำระเงิน ยังไม่ได้เชื่อมต่อระบบชำระเงินจริง")
                    .setPositiveButton("ยืนยันการชำระเงิน") { _, _ ->
                        update(order.copy(status = OrderStatus.TO_SHIP, paidAt = System.currentTimeMillis()))
                    }
                    .setNegativeButton("ยกเลิก", null)
                    .show()
            }
            OrderAction.CANCEL -> {
                AlertDialog.Builder(this)
                    .setTitle("ยกเลิกคำสั่งซื้อ")
                    .setMessage("ต้องการยกเลิกคำสั่งซื้อ #${order.id} ใช่หรือไม่? คูปองและเหรียญที่ใช้จะถูกคืนให้")
                    .setPositiveButton("ยกเลิกคำสั่งซื้อ") { _, _ ->
                        update(order.copy(status = OrderStatus.CANCELLED))
                    }
                    .setNegativeButton("ไม่ยกเลิก", null)
                    .show()
            }
            OrderAction.RECEIVED -> update(order.copy(status = OrderStatus.COMPLETED))
            OrderAction.REVIEW -> showReviewDialog(order)
        }
    }

    private fun showReviewDialog(order: Order) {
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 24, 48, 0)
        }
        val ratingBar = RatingBar(this).apply {
            numStars = 5
            stepSize = 1f
            rating = 5f
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
        val input = EditText(this).apply {
            hint = "เขียนรีวิว (ไม่บังคับ)"
            filters = arrayOf(InputFilter.LengthFilter(200))
        }
        container.addView(ratingBar)
        container.addView(input)

        AlertDialog.Builder(this)
            .setTitle("ให้คะแนน ${order.productName}")
            .setView(container)
            .setPositiveButton("ส่งรีวิว") { _, _ ->
                val stars = ratingBar.rating.toInt().coerceIn(1, 5)
                update(order.copy(rating = stars, review = input.text.toString().trim()))
            }
            .setNegativeButton("ยกเลิก", null)
            .show()
    }

    private fun update(order: Order) {
        OrderRepository.updateOrder(this, order)
        refresh()
    }

    /** ถ้ามาจากหน้าจ่ายเงิน ให้ย้อนกลับไปหน้าหลัก (ไม่ย้อนกลับไปหน้าจ่ายเงินซ้ำ) */
    private fun exitScreen() {
        if (fromCheckout) {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
        }
        finish()
    }
}