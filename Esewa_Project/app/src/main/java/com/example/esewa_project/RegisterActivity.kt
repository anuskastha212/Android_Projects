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

        ViewCompat.setOnApplyWindowInsetsListener(binding.registerTopbar){ view, insets ->
            val top = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top

            view.layoutParams.height =
                view.context.resources.getDimensionPixelSize(
                    com.google.android.material.R.dimen.mtrl_toolbar_default_height
                ) + top

            view.setPadding(
                view.paddingLeft,
                top,
                view.paddingRight,
                view.paddingBottom
            )
            insets

        }

        binding.registerTopbar.setNavigationOnClickListener {
            this.onBackPressedDispatcher.onBackPressed()
        }

        binding.submitRegisterButton.setOnClickListener {
            val email = binding.inputEmail.text.toString().trim()
            val name = binding.inputFullName.text.toString().trim()
            val phone = binding.inputPhone.text.toString().trim()
            val password = binding.inputPassword.text.toString().trim()
            val confirmPassword = binding.inputConfirmPassword.text.toString().trim()


            binding.fullName.error = null
            binding.phone.error = null
            binding.email.error = null
            binding.password.error = null
            binding.confirmPassword.error = null

            if (name.isEmpty()) {
                binding.fullName.error = "Full name is required"
                return@setOnClickListener
            }
            if (phone.isEmpty()) {
                binding.phone.error = "Phone number is required"
                return@setOnClickListener
            }
            if (email.isEmpty()) {
                binding.email.error = "Email address is required"
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                binding.password.error = "Password is required"
                return@setOnClickListener
            }
            if (confirmPassword.isEmpty()) {
                binding.confirmPassword.error = "Please confirm your password"
                return@setOnClickListener
            }
            if (password != confirmPassword) {
                binding.confirmPassword.error = "Passwords do not match"
                return@setOnClickListener
            }

            viewModel.register(email, password, name, phone)
        }

        viewModel.authResult.observe(this) { result ->
            result?.let {
                if (it.isSuccess) {
                    Toast.makeText(
                        this,
                        "Registration Successful!",
                        Toast.LENGTH_SHORT).show()
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