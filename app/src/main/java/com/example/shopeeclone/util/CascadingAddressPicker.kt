package com.example.shopeeclone.util

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import com.example.shopeeclone.model.District
import com.example.shopeeclone.model.Province
import com.example.shopeeclone.model.SubDistrict
import com.example.shopeeclone.repository.AddressRepository
import com.google.android.material.textfield.TextInputLayout

/**
 * จังหวัด -> อำเภอ -> ตำบล -> รหัสไปรษณีย์ (input ขึ้นต่อกัน)
 * เปลี่ยนระดับบน ระดับล่างจะถูกล้างและล็อกใหม่ทุกครั้ง
 */
class CascadingAddressPicker(
    private val context: Context,
    private val tilDistrict: TextInputLayout,
    private val tilSubDistrict: TextInputLayout,
    private val actProvince: AutoCompleteTextView,
    private val actDistrict: AutoCompleteTextView,
    private val actSubDistrict: AutoCompleteTextView,
    private val etZipcode: EditText
) {
    private val provinces: List<Province> = AddressRepository.loadProvinces(context)

    var province: Province? = null
        private set
    var district: District? = null
        private set
    var subDistrict: SubDistrict? = null
        private set

    init {
        actProvince.setAdapter(adapterOf(provinces.map { it.name }))
        actProvince.setOnItemClickListener { _, _, position, _ -> selectProvince(provinces[position]) }
        tilDistrict.isEnabled = false
        tilSubDistrict.isEnabled = false
    }

    fun isComplete(): Boolean = subDistrict != null

    /** ใช้เติมค่าจากที่อยู่ที่บันทึกไว้ (ผ่านทุกชั้นเหมือนผู้ใช้กดเลือกเอง) */
    fun select(provinceName: String, districtName: String, subDistrictName: String) {
        val p = provinces.find { it.name == provinceName } ?: return
        selectProvince(p)
        val d = p.districts.find { it.name == districtName } ?: return
        selectDistrict(d)
        val s = d.subDistricts.find { it.name == subDistrictName } ?: return
        selectSubDistrict(s)
    }

    private fun adapterOf(items: List<String>) =
        ArrayAdapter(context, android.R.layout.simple_list_item_1, items)

    private fun selectProvince(p: Province) {
        province = p
        actProvince.setText(p.name, false)
        resetDistrict()
        actDistrict.setAdapter(adapterOf(p.districts.map { it.name }))
        actDistrict.setOnItemClickListener { _, _, position, _ -> selectDistrict(p.districts[position]) }
        tilDistrict.isEnabled = true
    }

    private fun selectDistrict(d: District) {
        district = d
        actDistrict.setText(d.name, false)
        resetSubDistrict()
        actSubDistrict.setAdapter(adapterOf(d.subDistricts.map { it.name }))
        actSubDistrict.setOnItemClickListener { _, _, position, _ -> selectSubDistrict(d.subDistricts[position]) }
        tilSubDistrict.isEnabled = true
    }

    private fun selectSubDistrict(s: SubDistrict) {
        subDistrict = s
        actSubDistrict.setText(s.name, false)
        etZipcode.setText(s.zipcode)
    }

    private fun resetDistrict() {
        district = null
        actDistrict.setText("", false)
        actDistrict.setAdapter(null)
        tilDistrict.isEnabled = false
        resetSubDistrict()
    }

    private fun resetSubDistrict() {
        subDistrict = null
        actSubDistrict.setText("", false)
        actSubDistrict.setAdapter(null)
        tilSubDistrict.isEnabled = false
        etZipcode.setText("")
    }
}