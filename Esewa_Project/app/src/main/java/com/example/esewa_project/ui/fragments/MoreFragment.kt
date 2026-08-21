package com.example.esewa_project.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat.enableEdgeToEdge
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.esewa_project.LoginActivity
import com.example.esewa_project.MainActivity
import com.example.esewa_project.ProductDetailActivity
import com.example.esewa_project.R
import com.example.esewa_project.RegisterActivity
import com.example.esewa_project.databinding.FragmentMoreBinding
import com.example.esewa_project.ui.viewmodel.AuthViewModel

class MoreFragment : Fragment() {

    private var _binding: FragmentMoreBinding? = null
    private val binding get() = _binding!!
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbarMore.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        val menu = binding.toolbarMore.menu
        menu.findItem(R.id.action_cart)?.isVisible = false
        menu.findItem(R.id.action_notifications)?.isVisible = false
        menu.findItem(R.id.action_options)?.isVisible = false

        authViewModel.userData.observe(viewLifecycleOwner) { data ->
            if (data != null) {
                binding.userName.text = data["name"]?.toString() ?: "User"
                binding.userPhone.text = data["phone"]?.toString() ?: "No Phone"
            }
        }

        binding.loginButton.setOnClickListener {
            startActivity(Intent(requireContext(), LoginActivity::class.java))
        }

        binding.registerButton.setOnClickListener {
            startActivity(Intent(requireContext(), RegisterActivity::class.java))
        }

        binding.logoutButton.setOnClickListener {
            authViewModel.logout()
            Toast.makeText(
                requireContext(),
                "Logged out successfully",
                Toast.LENGTH_SHORT
            ).show()
        }

        updateUI()
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    private fun updateUI() {
        val isLoggedIn = authViewModel.isUserLoggedIn()

        if (isLoggedIn) {
            binding.layoutGuest.visibility = View.GONE
            binding.layoutLoggedIn.visibility = View.VISIBLE
            binding.logoutButton.visibility = View.VISIBLE
            authViewModel.fetchUserDetails()
        } else {
            binding.layoutGuest.visibility = View.VISIBLE
            binding.layoutLoggedIn.visibility = View.GONE
            binding.logoutButton.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}