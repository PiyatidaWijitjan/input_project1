package com.example.shopeeclone.ui.auth

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.shopeeclone.MainActivity
import com.example.shopeeclone.databinding.ActivityRegisterBinding
import com.example.shopeeclone.model.User
import com.example.shopeeclone.repository.UserRepository
import com.example.shopeeclone.util.Validators
import com.google.android.material.textfield.TextInputEditText

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }

        clearErrorOnType(binding.etName) { binding.tilName.error = null }
        clearErrorOnType(binding.etPhone) { binding.tilPhone.error = null }
        clearErrorOnType(binding.etEmail) { binding.tilEmail.error = null }
        clearErrorOnType(binding.etPassword) { binding.tilPassword.error = null }
        clearErrorOnType(binding.etConfirmPassword) { binding.tilConfirmPassword.error = null }
        binding.cbAcceptTerms.setOnCheckedChangeListener { _, _ ->
            binding.tvTermsError.visibility = View.GONE
        }

        binding.btnRegister.setOnClickListener { attemptRegister() }
        binding.tvGoToLogin.setOnClickListener { finish() }
    }

    private fun attemptRegister() {
        val name = binding.etName.text?.toString().orEmpty().trim()
        val phone = binding.etPhone.text?.toString().orEmpty()
        val email = binding.etEmail.text?.toString().orEmpty().trim()
        val password = binding.etPassword.text?.toString().orEmpty()
        val confirmPassword = binding.etConfirmPassword.text?.toString().orEmpty()

        val nameError = Validators.nameError(name)
        val phoneError = Validators.phoneError(phone)
        val emailError = Validators.emailError(email)
        val passwordError = Validators.passwordError(password)
        val confirmError = Validators.confirmPasswordError(password, confirmPassword)
        val termsAccepted = binding.cbAcceptTerms.isChecked

        binding.tilName.error = nameError
        binding.tilPhone.error = phoneError
        binding.tilEmail.error = emailError
        binding.tilPassword.error = passwordError
        binding.tilConfirmPassword.error = confirmError
        binding.tvTermsError.visibility = if (termsAccepted) View.GONE else View.VISIBLE

        val hasError = listOf(nameError, phoneError, emailError, passwordError, confirmError).any { it != null }
        if (hasError || !termsAccepted) return

        when (UserRepository.register(this, User(name, phone, email, password))) {
            is UserRepository.RegisterResult.PhoneAlreadyExists -> {
                binding.tilPhone.error = "เบอร์โทรศัพท์นี้ถูกใช้สมัครสมาชิกไปแล้ว"
            }
            is UserRepository.RegisterResult.Success -> {
                Toast.makeText(this, "สมัครสมาชิกสำเร็จ ยินดีต้อนรับ $name", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finishAffinity()
            }
        }
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