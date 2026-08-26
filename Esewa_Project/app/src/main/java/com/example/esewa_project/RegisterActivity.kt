package com.example.esewa_project

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Color
import androidx.core.content.ContextCompat
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

        ViewCompat.setOnApplyWindowInsetsListener(binding.registerFooter) { view, insets ->
            val bottomInsets = insets.getInsets(
                WindowInsetsCompat.Type.navigationBars() or WindowInsetsCompat.Type.ime()
            ).bottom
            view.setPadding(
                view.paddingLeft,
                view.paddingTop,
                view.paddingRight,
                bottomInsets
            )
            insets
        }

        val fullText = getString(R.string.i_agree_to_the_esewa_market)
        val term = "Terms & Conditions"
        val spannable = SpannableString(fullText)

        val start = fullText.indexOf(term)
        val end = start + term.length

        val clickableSpan = object : ClickableSpan(){
            override fun onClick(view: View) {
                Toast.makeText(
                    this@RegisterActivity,
                    "Terms & Conditions",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = ContextCompat.getColor(this@RegisterActivity, R.color.green)
            }
        }
        spannable.setSpan(clickableSpan, start,end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.tvTerms.apply {
            text = spannable
            movementMethod = LinkMovementMethod.getInstance()
            highlightColor = Color.TRANSPARENT
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
                        "Successful Registration",
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