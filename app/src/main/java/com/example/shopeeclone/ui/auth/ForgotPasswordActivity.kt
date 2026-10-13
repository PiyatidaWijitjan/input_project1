package com.example.shopeeclone.ui.auth

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.example.shopeeclone.databinding.ActivityForgotPasswordBinding
import com.example.shopeeclone.repository.UserRepository
import com.example.shopeeclone.util.Validators

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.etPhone.doAfterTextChanged { binding.tilPhone.error = null }
        binding.etEmail.doAfterTextChanged { binding.tilEmail.error = null }
        binding.etNewPassword.doAfterTextChanged { binding.tilNewPassword.error = null }
        binding.etConfirmPassword.doAfterTextChanged { binding.tilConfirmPassword.error = null }

        binding.btnReset.setOnClickListener { attemptReset() }
    }

    private fun attemptReset() {
        val phone = binding.etPhone.text?.toString().orEmpty()
        val email = binding.etEmail.text?.toString().orEmpty().trim()
        val newPass = binding.etNewPassword.text?.toString().orEmpty()
        val confirm = binding.etConfirmPassword.text?.toString().orEmpty()

        val phoneError = Validators.phoneError(phone)
        val emailError = Validators.emailError(email)
        val newError = Validators.passwordError(newPass)
        val confirmError = Validators.confirmPasswordError(newPass, confirm)

        binding.tilPhone.error = phoneError
        binding.tilEmail.error = emailError
        binding.tilNewPassword.error = newError
        binding.tilConfirmPassword.error = confirmError
        if (listOf(phoneError, emailError, newError, confirmError).any { it != null }) return

        if (UserRepository.resetPassword(this, phone, email, newPass)) {
            Toast.makeText(this, "ตั้งรหัสผ่านใหม่สำเร็จ กรุณาเข้าสู่ระบบอีกครั้ง", Toast.LENGTH_LONG).show()
            finish()
        } else {
            Toast.makeText(this, "ไม่พบบัญชีที่ตรงกับเบอร์โทรและอีเมลนี้", Toast.LENGTH_SHORT).show()
        }
    }
}