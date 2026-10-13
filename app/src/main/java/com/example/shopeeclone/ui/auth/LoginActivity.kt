package com.example.shopeeclone.ui.auth

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.shopeeclone.MainActivity
import com.example.shopeeclone.databinding.ActivityLoginBinding
import com.example.shopeeclone.repository.UserRepository
import com.example.shopeeclone.util.Validators
import com.google.android.material.textfield.TextInputEditText

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ถ้ามี session ค้างอยู่ ข้ามไปหน้าหลักได้เลย
        if (UserRepository.isLoggedIn(this)) {
            goToMain()
            return
        }

        clearErrorOnType(binding.etPhone) { binding.tilPhone.error = null }
        clearErrorOnType(binding.etPassword) { binding.tilPassword.error = null }

        binding.btnLogin.setOnClickListener { attemptLogin() }

        binding.tvGoToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.tvGuestMode.setOnClickListener { goToMain() }

        binding.tvForgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
    }

    private fun attemptLogin() {
        val phone = binding.etPhone.text?.toString().orEmpty()
        val password = binding.etPassword.text?.toString().orEmpty()

        val phoneError = Validators.phoneError(phone)
        val passwordError = Validators.passwordError(password)

        binding.tilPhone.error = phoneError
        binding.tilPassword.error = passwordError

        if (phoneError != null || passwordError != null) return

        val user = UserRepository.login(this, phone, password)
        if (user == null) {
            Toast.makeText(this, "เบอร์โทรศัพท์หรือรหัสผ่านไม่ถูกต้อง", Toast.LENGTH_SHORT).show()
            return
        }

        Toast.makeText(this, "ยินดีต้อนรับ ${user.name}", Toast.LENGTH_SHORT).show()
        goToMain()
    }

    private fun goToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private inline fun clearErrorOnType(
        editText: TextInputEditText,
        crossinline onType: () -> Unit
    ) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) { onType() }
        })
    }
}