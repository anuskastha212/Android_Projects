package com.example.esewa_project.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.esewa_project.LoginActivity
import com.example.esewa_project.R
import com.example.esewa_project.RegisterActivity
import com.example.esewa_project.databinding.FragmentMoreBinding
import com.example.esewa_project.ui.viewmodel.AuthViewModel
import com.example.esewa_project.ui.viewmodel.UserSessionViewModel

class MoreFragment : Fragment() {

    private var _binding: FragmentMoreBinding? = null
    private val binding get() = _binding!!
    private val authViewModel: AuthViewModel by viewModels()
    private val userSessionViewModel: UserSessionViewModel by activityViewModels()

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

        userSessionViewModel.userProfile.observe(viewLifecycleOwner) { profile ->
            if (profile.uid.isNotEmpty()) {
                binding.userName.text = profile.name
                binding.userPhone.text = profile.phone

                binding.layoutGuest.visibility = View.GONE
                binding.layoutLoggedIn.visibility = View.VISIBLE
                binding.logoutButton.visibility = View.VISIBLE
            } else {
                binding.layoutGuest.visibility = View.VISIBLE
                binding.layoutLoggedIn.visibility = View.GONE
                binding.logoutButton.visibility = View.GONE
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}