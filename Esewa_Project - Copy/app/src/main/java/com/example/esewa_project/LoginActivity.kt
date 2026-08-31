package com.example.esewa_project

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
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

        ViewCompat.setOnApplyWindowInsetsListener(binding.loginFooter){ view, insets ->
            val navigationBarHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom

            view.setPadding(
                view.paddingLeft,
                view.paddingTop,
                view.paddingRight,
                navigationBarHeight )
            insets
        }

        binding.submitLoginButton.setOnClickListener {
            val email = binding.inputLoginEmail.text.toString().trim()
            val password = binding.inputLoginPassword.text.toString().trim()

            binding.loginEmailPhone.error = null
            binding.loginPassword.error = null

            if (email.isEmpty()) {
                binding.loginEmailPhone.error = "Email address is required"
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                binding.loginPassword.error = "Password is required"
                return@setOnClickListener
            }

            viewModel.login(email, password)
        }

        binding.forgotPassword.setOnClickListener {
            val email = binding.inputLoginEmail.text.toString().trim()
            if (email.isEmpty() || !email.contains("@")) {
                binding.loginEmailPhone.error = "Enter valid email to reset password"
                Toast.makeText(
                    this,
                    "Please enter your email in the box above",
                    Toast.LENGTH_SHORT).show()
            } else {
                viewModel.sendResetEmail(email)
                Toast.makeText(
                    this,
                    "Sending reset link...",
                    Toast.LENGTH_SHORT).show()
            }
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

        viewModel.resetPassword.observe(this){result ->
            result?.let{
                if(it.isSuccess){
                    Toast.makeText(
                        this,
                        "Success! Check your inbox",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                } else {
                    Toast.makeText(
                        this,
                        it.exceptionOrNull()?.message ?: "Password Reset Failed",
                        Toast.LENGTH_LONG).show()
                }
                viewModel.resetPasswordResult()
            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action == MotionEvent.ACTION_DOWN){
            val v=currentFocus
            if(v is EditText){
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())){
                    v.clearFocus()
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken,0)
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }
}