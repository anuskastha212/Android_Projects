package com.example.esewa_project.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.esewa_project.MainActivity
import com.example.esewa_project.R
import com.example.esewa_project.ui.viewmodel.FavouriteViewModel

class FavouriteFragment: Fragment() {
    private val favViewModel: FavouriteViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                FavouriteScreen(
                    favouriteViewModel = favViewModel,
                    onBackClick = {
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                    },
                    onCartClick = {
                        val mainActivity =  requireActivity() as? MainActivity
                        mainActivity?. findViewById<View>(R.id.navItemCart)?.performClick()
                    }
                )
            }
        }
    }
}