package com.example.shopeeclone.ui.checkout

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.example.shopeeclone.R
import com.example.shopeeclone.databinding.ActivityCheckoutBinding
import com.example.shopeeclone.model.Order
import com.example.shopeeclone.model.OrderStatus
import com.example.shopeeclone.model.Product
import com.example.shopeeclone.model.SavedAddress
import com.example.shopeeclone.repository.AddressBookRepository
import com.example.shopeeclone.repository.OrderRepository
import com.example.shopeeclone.repository.RewardRepository
import com.example.shopeeclone.repository.SettingsRepository
import com.example.shopeeclone.repository.UserRepository
import com.example.shopeeclone.repository.Voucher
import com.example.shopeeclone.ui.address.AddressBookActivity
import com.example.shopeeclone.ui.orders.OrderHistoryActivity
import com.example.shopeeclone.util.CascadingAddressPicker
import com.example.shopeeclone.util.Validators
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.floor

/**
 * หน้าจ่ายเงิน
 *  - ที่อยู่: จังหวัด -> อำเภอ -> ตำบล -> รหัสไปรษณีย์ (CascadingAddressPicker) หรือเลือกจากสมุดที่อยู่
 *  - คูปอง + เหรียญ หักเป็นส่วนลด
 */
class CheckoutActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_PRODUCT = "extra_product"
        private const val SHIPPING_EXPRESS_FEE = 50.0
    }

    private data class Pricing(
        val shipping: Double,
        val voucherDiscount: Double,
        val coinsUsed: Int,
        val total: Double
    )

    private lateinit var binding: ActivityCheckoutBinding
    private lateinit var picker: CascadingAddressPicker
    private lateinit var product: Product

    private var availableVouchers: List<Voucher> = emptyList()
    private var selectedVoucher: Voucher? = null
    private var coinBalance = 0
    private val money = NumberFormat.getNumberInstance(Locale("th", "TH"))

    private val pickAddress = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val id = result.data?.getStringExtra(AddressBookActivity.RESULT_ADDRESS_ID)
            val saved = id?.let { AddressBookRepository.get(this, it) }
            if (saved != null) applySavedAddress(saved)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.setNavigationOnClickListener { finish() }

        product = getProductExtra() ?: run {
            finish()
            return
        }

        picker = CascadingAddressPicker(
            this, binding.tilDistrict, binding.tilSubDistrict,
            binding.actProvince, binding.actDistrict, binding.actSubDistrict, binding.etZipcode
        )
        coinBalance = RewardRepository.coinBalance(this)

        prefillDefaults()
        setupVoucherDropdown()
        setupCoinsSwitch()

        binding.tvPickAddress.setOnClickListener {
            pickAddress.launch(
                Intent(this, AddressBookActivity::class.java)
                    .putExtra(AddressBookActivity.EXTRA_PICK_MODE, true)
            )
        }
        binding.rgShipping.setOnCheckedChangeListener { _, _ -> updateSummary() }
        binding.switchCoins.setOnCheckedChangeListener { _, _ -> updateSummary() }

        binding.etRecipientName.doAfterTextChanged { binding.tilRecipientName.error = null }
        binding.etRecipientPhone.doAfterTextChanged { binding.tilRecipientPhone.error = null }
        binding.etFullAddress.doAfterTextChanged { binding.tilStreet.error = null }

        binding.btnPlaceOrder.setOnClickListener { attemptPlaceOrder() }
        updateSummary()
    }

    /** เติมที่อยู่เริ่มต้น (หรือชื่อ/เบอร์จากโปรไฟล์) และวิธีชำระเงินเริ่มต้นจากหน้าตั้งค่า */
    private fun prefillDefaults() {
        val saved = AddressBookRepository.getDefault(this)
        if (saved != null) {
            applySavedAddress(saved)
        } else {
            UserRepository.getCurrentUser(this)?.let {
                binding.etRecipientName.setText(it.name)
                binding.etRecipientPhone.setText(it.phone)
            }
        }
        val paymentIds = listOf(R.id.rbCod, R.id.rbBankTransfer, R.id.rbCreditCard, R.id.rbEWallet)
        binding.rgPayment.check(paymentIds[SettingsRepository.getDefaultPayment(this).coerceIn(0, 3)])
    }

    private fun applySavedAddress(a: SavedAddress) {
        binding.etRecipientName.setText(a.recipientName)
        binding.etRecipientPhone.setText(a.phone)
        binding.etFullAddress.setText(a.detail)
        picker.select(a.province, a.district, a.subDistrict)
        binding.cbSaveAddress.isChecked = false
    }

    private fun setupVoucherDropdown() {
        availableVouchers = RewardRepository.availableVouchers(this, product.price)
        val labels = listOf("ไม่ใช้คูปอง") + availableVouchers.map { "${it.title} (${it.code})" }
        binding.actVoucher.setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, labels))
        binding.actVoucher.setText(labels[0], false)
        binding.actVoucher.setOnItemClickListener { _, _, position, _ ->
            selectedVoucher = if (position == 0) null else availableVouchers[position - 1]
            updateSummary()
        }
    }

    private fun setupCoinsSwitch() {
        binding.switchCoins.text = "ใช้เหรียญเป็นส่วนลด (มี $coinBalance เหรียญ = ฿$coinBalance)"
        binding.switchCoins.isEnabled = coinBalance > 0
    }

    private fun calculate(): Pricing {
        val shipping =
            if (binding.rgShipping.checkedRadioButtonId == R.id.rbShippingExpress) SHIPPING_EXPRESS_FEE else 0.0
        val base = product.price + shipping
        val voucherDiscount = minOf(selectedVoucher?.discount ?: 0.0, base)
        val afterVoucher = base - voucherDiscount
        val coinsUsed = if (binding.switchCoins.isChecked)
            minOf(coinBalance.toDouble(), floor(afterVoucher)).toInt() else 0
        return Pricing(shipping, voucherDiscount, coinsUsed, afterVoucher - coinsUsed)
    }

    private fun updateSummary() {
        val p = calculate()
        val lines = mutableListOf("${product.name} x1   ฿${money.format(product.price)}")
        lines.add("ค่าจัดส่ง   " + if (p.shipping > 0) "฿${money.format(p.shipping)}" else "ฟรี")
        if (p.voucherDiscount > 0) lines.add("ส่วนลดคูปอง   -฿${money.format(p.voucherDiscount)}")
        if (p.coinsUsed > 0) lines.add("ใช้เหรียญ ${p.coinsUsed} เหรียญ   -฿${p.coinsUsed}")
        binding.tvOrderSummary.text = lines.joinToString("\n")
        binding.tvOrderTotal.text = "ยอดรวมสุทธิ: ฿${money.format(p.total)}"
    }

    private fun attemptPlaceOrder() {
        val name = binding.etRecipientName.text?.toString().orEmpty().trim()
        val phone = binding.etRecipientPhone.text?.toString().orEmpty()
        val street = binding.etFullAddress.text?.toString().orEmpty().trim()

        val nameError = Validators.nameError(name)
        val phoneError = Validators.phoneError(phone)
        val streetError = if (street.isBlank()) "กรุณากรอกบ้านเลขที่ / หมู่บ้าน / ถนน" else null

        binding.tilRecipientName.error = nameError
        binding.tilRecipientPhone.error = phoneError
        binding.tilStreet.error = streetError
        if (nameError != null || phoneError != null || streetError != null) return

        val province = picker.province
        val district = picker.district
        val subDistrict = picker.subDistrict
        if (province == null) {
            Toast.makeText(this, "กรุณาเลือกจังหวัด", Toast.LENGTH_SHORT).show()
            return
        }
        if (district == null) {
            Toast.makeText(this, "กรุณาเลือกอำเภอ/เขต", Toast.LENGTH_SHORT).show()
            return
        }
        if (subDistrict == null) {
            Toast.makeText(this, "กรุณาเลือกตำบล/แขวง", Toast.LENGTH_SHORT).show()
            return
        }

        val isCod = binding.rgPayment.checkedRadioButtonId == R.id.rbCod
        val paymentMethod = when (binding.rgPayment.checkedRadioButtonId) {
            R.id.rbCod -> "เก็บเงินปลายทาง (COD)"
            R.id.rbBankTransfer -> "โอนผ่านธนาคาร"
            R.id.rbCreditCard -> "บัตรเครดิต/เดบิต"
            else -> "พร้อมเพย์ / e-Wallet"
        }
        val isExpress = binding.rgShipping.checkedRadioButtonId == R.id.rbShippingExpress
        val pricing = calculate()
        val now = System.currentTimeMillis()

        OrderRepository.addOrder(
            this,
            Order(
                id = "ORD" + now.toString().takeLast(8),
                productName = product.name,
                productPrice = product.price,
                shippingMethod = if (isExpress) "จัดส่งด่วน" else "จัดส่งมาตรฐาน",
                shippingFee = pricing.shipping,
                paymentMethod = paymentMethod,
                address = "$street ต.${subDistrict.name} อ.${district.name} จ.${province.name} ${subDistrict.zipcode}",
                total = pricing.total,
                createdAt = now,
                status = if (isCod) OrderStatus.TO_SHIP else OrderStatus.PENDING_PAYMENT,
                recipientName = name,
                recipientPhone = phone,
                voucherCode = selectedVoucher?.code ?: "",
                voucherDiscount = pricing.voucherDiscount,
                coinsUsed = pricing.coinsUsed,
                coinsEarned = RewardRepository.coinsFor(pricing.total)
            )
        )

        val alreadySaved = AddressBookRepository.getAll(this).any {
            it.detail == street && it.subDistrict == subDistrict.name && it.phone == phone
        }
        if (binding.cbSaveAddress.isChecked && !alreadySaved) {
            AddressBookRepository.save(
                this,
                SavedAddress(
                    id = now.toString(),
                    recipientName = name,
                    phone = phone,
                    detail = street,
                    province = province.name,
                    district = district.name,
                    subDistrict = subDistrict.name,
                    zipcode = subDistrict.zipcode,
                    isDefault = false
                )
            )
        }

        Toast.makeText(this, "สั่งซื้อสำเร็จ", Toast.LENGTH_SHORT).show()
        startActivity(
            Intent(this, OrderHistoryActivity::class.java)
                .putExtra(OrderHistoryActivity.EXTRA_FROM_CHECKOUT, true)
        )
        finish()
    }

    @Suppress("DEPRECATION")
    private fun getProductExtra(): Product? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(EXTRA_PRODUCT, Product::class.java)
        } else {
            intent.getSerializableExtra(EXTRA_PRODUCT) as? Product
        }
    }
}