package com.example.shopeeclone.ui.profile

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.example.shopeeclone.databinding.ActivityEditProfileBinding
import com.example.shopeeclone.repository.UserRepository
import com.example.shopeeclone.util.Validators

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.setNavigationOnClickListener { finish() }

        val user = UserRepository.getCurrentUser(this)
        if (user == null) {
            Toast.makeText(this, "กรุณาเข้าสู่ระบบก่อนแก้ไขโปรไฟล์", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.etName.setText(user.name)
        binding.etPhone.setText(user.phone)
        binding.etEmail.setText(user.email)

        binding.etName.doAfterTextChanged { binding.tilName.error = null }
        binding.etEmail.doAfterTextChanged { binding.tilEmail.error = null }

        binding.btnSave.setOnClickListener {
            val name = binding.etName.text?.toString().orEmpty().trim()
            val email = binding.etEmail.text?.toString().orEmpty().trim()
            val nameError = Validators.nameError(name)
            val emailError = Validators.emailError(email)
            binding.tilName.error = nameError
            binding.tilEmail.error = emailError
            if (nameError != null || emailError != null) return@setOnClickListener

            UserRepository.updateProfile(this, name, email)
            Toast.makeText(this, "บันทึกข้อมูลโปรไฟล์แล้ว", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}