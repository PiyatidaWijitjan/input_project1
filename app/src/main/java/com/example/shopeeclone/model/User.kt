package com.example.shopeeclone.model

import java.io.Serializable

// หมายเหตุ: เก็บรหัสผ่านแบบ plain text เพื่อความง่ายของตัวอย่างเท่านั้น
// แอปจริงต้องเข้ารหัส (bcrypt/argon2) และยืนยันตัวตนผ่านเซิร์ฟเวอร์
data class User(
    val name: String,
    val phone: String,
    val email: String,
    val password: String
) : Serializable