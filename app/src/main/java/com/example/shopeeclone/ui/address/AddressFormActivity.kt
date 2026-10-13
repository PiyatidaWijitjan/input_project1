package com.example.shopeeclone.ui.address

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.example.shopeeclone.databinding.ActivityAddressFormBinding
import com.example.shopeeclone.model.SavedAddress
import com.example.shopeeclone.repository.AddressBookRepository
import com.example.shopeeclone.repository.UserRepository
import com.example.shopeeclone.util.CascadingAddressPicker
import com.example.shopeeclone.util.Validators

class AddressFormActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_ADDRESS_ID = "extra_address_id"
    }

    private lateinit var binding: ActivityAddressFormBinding
    private lateinit var picker: CascadingAddressPicker
    private var editing: SavedAddress? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddressFormBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.setNavigationOnClickListener { finish() }

        picker = CascadingAddressPicker(
            this, binding.tilDistrict, binding.tilSubDistrict,
            binding.actProvince, binding.actDistrict, binding.actSubDistrict, binding.etZipcode
        )

        editing = intent.getStringExtra(EXTRA_ADDRESS_ID)?.let { AddressBookRepository.get(this, it) }
        val existing = editing
        if (existing != null) {
            binding.toolbar.title = "แก้ไขที่อยู่"
            binding.etRecipientName.setText(existing.recipientName)
            binding.etRecipientPhone.setText(existing.phone)
            binding.etDetail.setText(existing.detail)
            binding.cbDefault.isChecked = existing.isDefault
            picker.select(existing.province, existing.district, existing.subDistrict)
        } else {
            UserRepository.getCurrentUser(this)?.let {
                binding.etRecipientName.setText(it.name)
                binding.etRecipientPhone.setText(it.phone)
            }
        }

        binding.etRecipientName.doAfterTextChanged { binding.tilRecipientName.error = null }
        binding.etRecipientPhone.doAfterTextChanged { binding.tilRecipientPhone.error = null }
        binding.etDetail.doAfterTextChanged { binding.tilDetail.error = null }

        binding.btnSave.setOnClickListener { save() }
    }

    private fun save() {
        val name = binding.etRecipientName.text?.toString().orEmpty().trim()
        val phone = binding.etRecipientPhone.text?.toString().orEmpty()
        val detail = binding.etDetail.text?.toString().orEmpty().trim()

        val nameError = Validators.nameError(name)
        val phoneError = Validators.phoneError(phone)
        val detailError = if (detail.isBlank()) "กรุณากรอกบ้านเลขที่ / หมู่บ้าน / ถนน" else null

        binding.tilRecipientName.error = nameError
        binding.tilRecipientPhone.error = phoneError
        binding.tilDetail.error = detailError
        if (nameError != null || phoneError != null || detailError != null) return

        val province = picker.province
        val district = picker.district
        val subDistrict = picker.subDistrict
        if (province == null || district == null || subDistrict == null) {
            Toast.makeText(this, "กรุณาเลือกจังหวัด อำเภอ และตำบลให้ครบ", Toast.LENGTH_SHORT).show()
            return
        }

        AddressBookRepository.save(
            this,
            SavedAddress(
                id = editing?.id ?: System.currentTimeMillis().toString(),
                recipientName = name,
                phone = phone,
                detail = detail,
                province = province.name,
                district = district.name,
                subDistrict = subDistrict.name,
                zipcode = subDistrict.zipcode,
                isDefault = binding.cbDefault.isChecked
            )
        )
        Toast.makeText(this, "บันทึกที่อยู่แล้ว", Toast.LENGTH_SHORT).show()
        finish()
    }
}