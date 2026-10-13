package com.example.shopeeclone.repository

import android.content.Context
import com.example.shopeeclone.model.District
import com.example.shopeeclone.model.Province
import com.example.shopeeclone.model.SubDistrict
import org.json.JSONArray

/**
 * โหลดข้อมูลที่อยู่ (จังหวัด/อำเภอ/ตำบล/รหัสไปรษณีย์) จากไฟล์ assets/thai_address.json
 * ในโปรเจกต์จริงสามารถเปลี่ยนมาดึงจาก API เช่น
 * https://raw.githubusercontent.com/kongvut/thai-province-data ได้เช่นกัน
 * โดยยังคงโครงสร้าง Province -> District -> SubDistrict เดิม
 */
object AddressRepository {

    private var cache: List<Province>? = null

    fun loadProvinces(context: Context): List<Province> {
        cache?.let { return it }

        val jsonText = context.assets.open("thai_address.json")
            .bufferedReader(Charsets.UTF_8)
            .use { it.readText() }

        val provincesArray = JSONArray(jsonText)
        val provinces = mutableListOf<Province>()

        for (i in 0 until provincesArray.length()) {
            val provinceObj = provincesArray.getJSONObject(i)
            val provinceName = provinceObj.getString("province")

            val districtsArray = provinceObj.getJSONArray("districts")
            val districts = mutableListOf<District>()

            for (j in 0 until districtsArray.length()) {
                val districtObj = districtsArray.getJSONObject(j)
                val districtName = districtObj.getString("name")

                val subArray = districtObj.getJSONArray("subdistricts")
                val subDistricts = mutableListOf<SubDistrict>()

                for (k in 0 until subArray.length()) {
                    val subObj = subArray.getJSONObject(k)
                    subDistricts.add(
                        SubDistrict(
                            name = subObj.getString("name"),
                            zipcode = subObj.getString("zipcode")
                        )
                    )
                }
                districts.add(District(districtName, subDistricts))
            }
            provinces.add(Province(provinceName, districts))
        }

        cache = provinces
        return provinces
    }
}
