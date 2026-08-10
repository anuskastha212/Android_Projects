package com.example.esewa_project

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.esewa_project.ui.viewmodel.AuthViewModel
import com.example.esewa_project.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: AuthViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.registerFooter){ view, insets ->
            val navigationBarHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom

            view.setPadding(
                view.paddingLeft,
                view.paddingTop,
                view.paddingRight,
                navigationBarHeight )
            insets
        }

        binding.submitRegisterButton.setOnClickListener {
            val email = binding.inputRegisterEmail.text.toString().trim()
            val name = binding.inputRegisterFullName.text.toString().trim()
            val phone = binding.inputRegisterPhone.text.toString().trim()
            val password = binding.inputRegisterPassword.text.toString().trim()

            binding.registerFullName.error = null
            binding.registerPhone.error = null
            binding.registerEmail.error = null
            binding.registerPassword.error = null

            if (!binding.termsCheckBox.isChecked) {
                Toast.makeText(
                    this,
                    "Please agree to the Terms & Conditions",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (name.isEmpty()) {
                binding.registerFullName.error = "Full name is required"
                return@setOnClickListener
            }
            if (phone.isEmpty()) {
                binding.registerPhone.error = "Phone number is required"
                return@setOnClickListener
            }
            if (email.isEmpty()) {
                binding.registerEmail.error = "Email address is required"
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                binding.registerPassword.error = "Password is required"
                return@setOnClickListener
            }

            if (password.length < 6) {
                Toast.makeText(
                    this,
                    "Password must be at least 6 characters",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            viewModel.register(email, password, name, phone)
        }

        viewModel.authResult.observe(this) { result ->
            result?.let {
                if (it.isSuccess) {
                    Toast.makeText(
                        this,
                        "Succussful Registration",
                        Toast.LENGTH_LONG).show()
                    val intent = Intent (this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(
                        this,
                        it.exceptionOrNull()?.message ?: "Registration Failed",
                        Toast.LENGTH_LONG).show()
                }
                viewModel.resetResult()
            }
        }

        binding.login.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}