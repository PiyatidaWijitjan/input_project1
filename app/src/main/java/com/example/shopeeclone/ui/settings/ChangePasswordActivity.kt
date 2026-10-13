package com.example.shopeeclone.ui.settings

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.example.shopeeclone.databinding.ActivityChangePasswordBinding
import com.example.shopeeclone.repository.UserRepository
import com.example.shopeeclone.repository.UserRepository.ChangePasswordResult
import com.example.shopeeclone.util.Validators

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangePasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.etCurrentPassword.doAfterTextChanged { binding.tilCurrentPassword.error = null }
        binding.etNewPassword.doAfterTextChanged { binding.tilNewPassword.error = null }
        binding.etConfirmPassword.doAfterTextChanged { binding.tilConfirmPassword.error = null }

        binding.btnSave.setOnClickListener { attemptChange() }
    }

    private fun attemptChange() {
        val current = binding.etCurrentPassword.text?.toString().orEmpty()
        val newPass = binding.etNewPassword.text?.toString().orEmpty()
        val confirm = binding.etConfirmPassword.text?.toString().orEmpty()

        val currentError = if (current.isBlank()) "กรุณากรอกรหัสผ่านปัจจุบัน" else null
        val newError = Validators.passwordError(newPass)
        val confirmError = Validators.confirmPasswordError(newPass, confirm)

        binding.tilCurrentPassword.error = currentError
        binding.tilNewPassword.error = newError
        binding.tilConfirmPassword.error = confirmError
        if (currentError != null || newError != null || confirmError != null) return

        when (UserRepository.changePassword(this, current, newPass)) {
            ChangePasswordResult.SUCCESS -> {
                Toast.makeText(this, "เปลี่ยนรหัสผ่านสำเร็จ", Toast.LENGTH_SHORT).show()
                finish()
            }
            ChangePasswordResult.WRONG_CURRENT ->
                binding.tilCurrentPassword.error = "รหัสผ่านปัจจุบันไม่ถูกต้อง"
            ChangePasswordResult.SAME_AS_OLD ->
                binding.tilNewPassword.error = "รหัสผ่านใหม่ต้องไม่ซ้ำกับรหัสผ่านเดิม"
            ChangePasswordResult.NOT_LOGGED_IN -> {
                Toast.makeText(this, "กรุณาเข้าสู่ระบบก่อน", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}