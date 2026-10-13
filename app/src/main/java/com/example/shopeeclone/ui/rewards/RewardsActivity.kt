package com.example.shopeeclone.ui.rewards

import android.os.Bundle
import android.widget.SimpleAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.shopeeclone.databinding.ActivityRewardsBinding
import com.example.shopeeclone.model.OrderStatus
import com.example.shopeeclone.repository.OrderRepository
import com.example.shopeeclone.repository.RewardRepository

class RewardsActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_MODE = "extra_mode"
        const val MODE_COINS = "coins"
        const val MODE_VOUCHERS = "vouchers"
    }

    private lateinit var binding: ActivityRewardsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRewardsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.setNavigationOnClickListener { finish() }

        if (intent.getStringExtra(EXTRA_MODE) == MODE_VOUCHERS) showVouchers() else showCoins()
    }

    private fun showCoins() {
        binding.toolbar.title = "Shopee Coins"
        binding.tvHeader.text = "${RewardRepository.coinBalance(this)} เหรียญ"
        binding.tvSubHeader.text =
            "ได้รับ 1 เหรียญทุก ฿10 เมื่อคำสั่งซื้อสำเร็จ • ใช้เป็นส่วนลดได้ 1 เหรียญ = ฿1 ที่หน้าชำระเงิน"

        val rows = mutableListOf(row("+${RewardRepository.WELCOME_COINS} เหรียญ", "โบนัสต้อนรับสมาชิกใหม่"))
        OrderRepository.getOrders(this)
            .filter { it.status != OrderStatus.CANCELLED }
            .forEach { o ->
                if (o.coinsUsed > 0) {
                    rows.add(row("-${o.coinsUsed} เหรียญ", "ใช้เป็นส่วนลดกับคำสั่งซื้อ #${o.id}"))
                }
                if (o.coinsEarned > 0) {
                    if (o.status == OrderStatus.COMPLETED) {
                        rows.add(row("+${o.coinsEarned} เหรียญ", "ได้รับจากคำสั่งซื้อ #${o.id}"))
                    } else {
                        rows.add(row("รอรับ +${o.coinsEarned} เหรียญ", "จะได้รับเมื่อคำสั่งซื้อ #${o.id} สำเร็จ"))
                    }
                }
            }
        setList(rows)
    }

    private fun showVouchers() {
        binding.toolbar.title = "คูปองส่วนลดของฉัน"
        binding.tvHeader.text = "คูปองส่วนลด"
        binding.tvSubHeader.text = "เลือกใช้ได้ที่หน้าชำระเงิน (ใช้ได้ครั้งเดียวต่อคูปอง)"

        val used = RewardRepository.usedVoucherCodes(this)
        setList(RewardRepository.vouchers.map { v ->
            val state = if (v.code in used) "ใช้แล้ว" else "ใช้ได้"
            row("${v.title} • รหัส ${v.code}", "ขั้นต่ำ ฿${v.minSpend.toInt()} • $state")
        })
    }

    private fun row(title: String, subtitle: String): Map<String, String> =
        mapOf("title" to title, "subtitle" to subtitle)

    private fun setList(rows: List<Map<String, String>>) {
        binding.lvItems.adapter = SimpleAdapter(
            this, rows, android.R.layout.simple_list_item_2,
            arrayOf("title", "subtitle"),
            intArrayOf(android.R.id.text1, android.R.id.text2)
        )
    }
}