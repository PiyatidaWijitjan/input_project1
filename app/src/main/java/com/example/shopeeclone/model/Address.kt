package com.example.shopeeclone.model

/**
 * โครงสร้างข้อมูลที่อยู่แบบลำดับชั้น (Cascading)
 * Province -> District (อำเภอ/เขต) -> SubDistrict (ตำบล/แขวง) -> Zipcode
 *
 * ความสัมพันธ์แบบ "ขึ้นต่อกัน" (input dependency):
 *   - District list ขึ้นอยู่กับ Province ที่เลือก
 *   - SubDistrict list ขึ้นอยู่กับ District ที่เลือก
 *   - Zipcode ถูกกำหนดอัตโนมัติเมื่อเลือก SubDistrict (Smart Lookup)
 */
data class SubDistrict(
    val name: String,
    val zipcode: String
)

data class District(
    val name: String,
    val subDistricts: List<SubDistrict>
)

data class Province(
    val name: String,
    val districts: List<District>
)
