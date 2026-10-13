package com.example.shopeeclone.util

import android.util.Patterns

object Validators {

    private const val PHONE_LENGTH = 10
    private const val PASSWORD_MIN_LENGTH = 6
    private const val PASSWORD_MAX_LENGTH = 20
    private const val NAME_MIN_LENGTH = 2

    private val THAI_PHONE_REGEX = Regex("^0[0-9]{${PHONE_LENGTH - 1}}$")

    fun phoneError(phone: String): String? {
        return when {
            phone.isBlank() -> "กรุณากรอกเบอร์โทรศัพท์"
            phone.length != PHONE_LENGTH -> "เบอร์โทรศัพท์ต้องมี $PHONE_LENGTH หลัก (กรอกแล้ว ${phone.length} หลัก)"
            !THAI_PHONE_REGEX.matches(phone) -> "เบอร์โทรศัพท์ไม่ถูกต้อง ต้องขึ้นต้นด้วย 0 และเป็นตัวเลขเท่านั้น"
            else -> null
        }
    }

    fun nameError(name: String): String? {
        return when {
            name.isBlank() -> "กรุณากรอกชื่อ-นามสกุล"
            name.trim().length < NAME_MIN_LENGTH -> "ชื่อ-นามสกุลต้องมีอย่างน้อย $NAME_MIN_LENGTH ตัวอักษร"
            else -> null
        }
    }

    fun emailError(email: String): String? {
        return when {
            email.isBlank() -> "กรุณากรอกอีเมล"
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "รูปแบบอีเมลไม่ถูกต้อง"
            else -> null
        }
    }

    fun passwordError(password: String): String? {
        return when {
            password.isBlank() -> "กรุณากรอกรหัสผ่าน"
            password.length < PASSWORD_MIN_LENGTH -> "รหัสผ่านต้องมีอย่างน้อย $PASSWORD_MIN_LENGTH ตัวอักษร"
            password.length > PASSWORD_MAX_LENGTH -> "รหัสผ่านต้องไม่เกิน $PASSWORD_MAX_LENGTH ตัวอักษร"
            else -> null
        }
    }

    fun confirmPasswordError(password: String, confirmPassword: String): String? {
        return when {
            confirmPassword.isBlank() -> "กรุณายืนยันรหัสผ่าน"
            confirmPassword != password -> "รหัสผ่านไม่ตรงกัน"
            else -> null
        }
    }
}