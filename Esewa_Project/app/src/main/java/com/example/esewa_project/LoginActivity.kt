package com.example.esewa_project

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.esewa_project.databinding.ActivityLoginBinding
import com.example.esewa_project.ui.viewmodel.AuthViewModel

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.loginTopbar){ view, insets ->
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

        binding.loginTopbar.setNavigationOnClickListener {
            this.onBackPressedDispatcher.onBackPressed()
        }

        binding.submitLoginButton.setOnClickListener {
            val email = binding.inputLoginEmail.text.toString().trim()
            val password = binding.inputLoginPassword.text.toString().trim()

            binding.loginEmail.error = null
            binding.loginPassword.error = null

            if (email.isEmpty()) {
                binding.loginEmail.error = "Email address is required"
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                binding.loginPassword.error = "Password is required"
                return@setOnClickListener
            }

            viewModel.login(email, password)
        }

        binding.register.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }

        viewModel.authResult.observe(this){result ->
            result?.let{
                if(it.isSuccess){
                    Toast.makeText(
                        this,
                        "Login Successful",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                } else {
                    Toast.makeText(
                        this,
                        it.exceptionOrNull()?.message ?: "Login Failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                viewModel.resetResult()
            }
        }
    }
}