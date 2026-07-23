package com.example.esewa_project

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.esewa_project.databinding.ActivityMainBinding
import com.example.esewa_project.ui.fragments.CartFragment
import com.example.esewa_project.ui.fragments.FavouriteFragment
import com.example.esewa_project.ui.fragments.HomeFragment
import com.example.esewa_project.ui.fragments.MoreFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var selectedTab = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }

        if (savedInstanceState == null) {

            loadFragment(HomeFragment())

            onSelect(
                binding.bottomNav.navItemShop,
                binding.bottomNav.textShop,
                binding.bottomNav.iconShop
            )

            onDeselect(
                binding.bottomNav.navItemCart,
                binding.bottomNav.textCart,
                binding.bottomNav.iconCart
            )

            onDeselect(
                binding.bottomNav.navItemFavourite,
                binding.bottomNav.textFavourite,
                binding.bottomNav.iconFavourite
            )

            onDeselect(
                binding.bottomNav.navItemMore,
                binding.bottomNav.textMore,
                binding.bottomNav.iconMore
            )
        }

        binding.bottomNav.navItemShop.setOnClickListener {

            if (selectedTab != 1) {

                loadFragment(HomeFragment())

                onSelect(
                    binding.bottomNav.navItemShop,
                    binding.bottomNav.textShop,
                    binding.bottomNav.iconShop
                )

                onDeselect(
                    binding.bottomNav.navItemCart,
                    binding.bottomNav.textCart,
                    binding.bottomNav.iconCart
                )

                onDeselect(
                    binding.bottomNav.navItemFavourite,
                    binding.bottomNav.textFavourite,
                    binding.bottomNav.iconFavourite
                )

                onDeselect(
                    binding.bottomNav.navItemMore,
                    binding.bottomNav.textMore,
                    binding.bottomNav.iconMore
                )

                selectedTab = 1
            }
        }

        binding.bottomNav.navItemCart.setOnClickListener {

            if (selectedTab != 2) {

                loadFragment(CartFragment())

                onSelect(
                    binding.bottomNav.navItemCart,
                    binding.bottomNav.textCart,
                    binding.bottomNav.iconCart
                )

                onDeselect(
                    binding.bottomNav.navItemShop,
                    binding.bottomNav.textShop,
                    binding.bottomNav.iconShop
                )

                onDeselect(
                    binding.bottomNav.navItemFavourite,
                    binding.bottomNav.textFavourite,
                    binding.bottomNav.iconFavourite
                )

                onDeselect(
                    binding.bottomNav.navItemMore,
                    binding.bottomNav.textMore,
                    binding.bottomNav.iconMore
                )

                selectedTab = 2
            }
        }

        binding.bottomNav.navItemFavourite.setOnClickListener {

            if (selectedTab != 3) {

                loadFragment(FavouriteFragment())

                onSelect(
                    binding.bottomNav.navItemFavourite,
                    binding.bottomNav.textFavourite,
                    binding.bottomNav.iconFavourite
                )

                onDeselect(
                    binding.bottomNav.navItemShop,
                    binding.bottomNav.textShop,
                    binding.bottomNav.iconShop
                )

                onDeselect(
                    binding.bottomNav.navItemCart,
                    binding.bottomNav.textCart,
                    binding.bottomNav.iconCart
                )

                onDeselect(
                    binding.bottomNav.navItemMore,
                    binding.bottomNav.textMore,
                    binding.bottomNav.iconMore
                )

                selectedTab = 3
            }
        }

        binding.bottomNav.navItemMore.setOnClickListener {

            if (selectedTab != 4) {

                loadFragment(MoreFragment())

                onSelect(
                    binding.bottomNav.navItemMore,
                    binding.bottomNav.textMore,
                    binding.bottomNav.iconMore
                )

                onDeselect(
                    binding.bottomNav.navItemShop,
                    binding.bottomNav.textShop,
                    binding.bottomNav.iconShop
                )

                onDeselect(
                    binding.bottomNav.navItemCart,
                    binding.bottomNav.textCart,
                    binding.bottomNav.iconCart
                )

                onDeselect(
                    binding.bottomNav.navItemFavourite,
                    binding.bottomNav.textFavourite,
                    binding.bottomNav.iconFavourite
                )

                selectedTab = 4
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {

        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, fragment)
            .commit()
    }

    private fun onSelect(
        layout: LinearLayout,
        text: TextView,
        icon: ImageView
    ) {

        text.visibility = View.VISIBLE

        icon.imageTintList =
            ColorStateList.valueOf(
                ContextCompat.getColor(this, R.color.green)
            )

        layout.setBackgroundResource(R.drawable.bg_bottom_nav)

        layout.animate()
            .scaleX(1.1f)
            .scaleY(1.1f)
            .setDuration(100)
            .withEndAction {

                layout.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(100)

            }
    }

    private fun onDeselect(
        layout: LinearLayout,
        text: TextView,
        icon: ImageView
    ) {

        text.visibility = View.GONE

        icon.imageTintList =
            ColorStateList.valueOf(
                ContextCompat.getColor(this, R.color.black)
            )

        layout.setBackgroundResource(android.R.color.transparent)
    }
}