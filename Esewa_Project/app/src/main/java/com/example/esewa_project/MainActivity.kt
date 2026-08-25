package com.example.esewa_project

import android.content.res.ColorStateList
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.activity.viewModels
import com.example.esewa_project.databinding.ActivityMainBinding
import com.example.esewa_project.ui.fragments.CartFragment
import com.example.esewa_project.ui.fragments.FavouriteFragment
import com.example.esewa_project.ui.fragments.HomeFragment
import com.example.esewa_project.ui.fragments.MoreFragment
import com.example.esewa_project.ui.viewmodel.CartViewModel
import com.example.esewa_project.ui.viewmodel.FavouriteViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val cartViewModel: CartViewModel by viewModels()
    private val favouriteViewModel: FavouriteViewModel by viewModels()
    private var selectedTab = 1

    private val tagHome = "home"
    private val tagCart = "cart"
    private val tagFav = "favourite"
    private val tagMore = "more"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            insets
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    cartViewModel.cartCount.collectLatest { count ->
                        binding.bottomNav.apply {
                            if (count > 0) {
                                cartBadge.text = count.toString()
                                cartBadge.visibility = View.VISIBLE
                            } else {
                                cartBadge.visibility = View.GONE
                            }
                        }
                    }
                }
                launch {
                    favouriteViewModel.favouriteCount.collectLatest { count ->
                        binding.bottomNav.apply {
                            if (count > 0) {
                                favouriteBadge.text = count.toString()
                                favouriteBadge.visibility = View.VISIBLE
                            } else {
                                favouriteBadge.visibility = View.GONE
                            }
                        }
                    }
                }
            }
        }

        supportFragmentManager.addOnBackStackChangedListener {
            syncBottomNavSelection()
        }

        if (savedInstanceState == null) {
            loadFragment(HomeFragment(), tagHome)
            updateBottomNavUI(1)
        }
        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        binding.bottomNav.navItemShop.setOnClickListener {
            if (selectedTab != 1) {
                loadFragment(HomeFragment(), tagHome)
                updateBottomNavUI(1)
            }
        }

        binding.bottomNav.navItemCart.setOnClickListener {
            if (selectedTab != 2) {
                loadFragment(CartFragment(), tagCart)
                updateBottomNavUI(2)
            }
        }

        binding.bottomNav.navItemFavourite.setOnClickListener {
            if (selectedTab != 3) {
                loadFragment(FavouriteFragment(), tagFav)
                updateBottomNavUI(3)
            }
        }

        binding.bottomNav.navItemMore.setOnClickListener {
            if (selectedTab != 4) {
                loadFragment(MoreFragment(), tagMore)
                updateBottomNavUI(4)
            }
        }
    }

    private fun loadFragment(fragment: Fragment, tag: String) {
        val transaction = supportFragmentManager.beginTransaction()

        val currentFragment = supportFragmentManager.fragments.find { it.isVisible }
        if (currentFragment != null) {
            transaction.hide(currentFragment)
        }

        var targetFragment = supportFragmentManager.findFragmentByTag(tag)
        if (targetFragment == null) {
            targetFragment = fragment
            transaction.add(binding.fragmentContainer.id, targetFragment, tag)
        } else {
            transaction.show(targetFragment)
        }

        if (tag != tagHome && currentFragment?.tag != tag) {
            transaction.addToBackStack(tag)
        }

        transaction.commit()
    }

    private fun updateBottomNavUI(tabIndex: Int) {
        selectedTab = tabIndex
        val nav = binding.bottomNav

        onDeselect(nav.navItemShop, nav.textShop, nav.iconShop)
        onDeselect(nav.navItemCart, nav.textCart, nav.iconCart)
        onDeselect(nav.navItemFavourite, nav.textFavourite, nav.iconFavourite)
        onDeselect(nav.navItemMore, nav.textMore, nav.iconMore)

        when (tabIndex) {
            1 -> onSelect(nav.navItemShop, nav.textShop, nav.iconShop)
            2 -> onSelect(nav.navItemCart, nav.textCart, nav.iconCart)
            3 -> onSelect(nav.navItemFavourite, nav.textFavourite, nav.iconFavourite)
            4 -> onSelect(nav.navItemMore, nav.textMore, nav.iconMore)
        }
    }

    private fun syncBottomNavSelection() {
        val currentFragment = supportFragmentManager.fragments.find { it.isVisible }
        when (currentFragment) {
            is HomeFragment -> updateBottomNavUI(1)
            is CartFragment -> updateBottomNavUI(2)
            is FavouriteFragment -> updateBottomNavUI(3)
            is MoreFragment -> updateBottomNavUI(4)
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun onSelect(layout: LinearLayout, text: TextView, icon: ImageView) {
        text.visibility = View.VISIBLE
        icon.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.green))
        layout.setBackgroundResource(R.drawable.bg_bottom_nav)
        layout.animate().scaleX(1.1f).scaleY(1.1f).setDuration(100).withEndAction {
            layout.animate().scaleX(1f).scaleY(1f).setDuration(100)
        }
    }

    private fun onDeselect(layout: LinearLayout, text: TextView, icon: ImageView) {
        text.visibility = View.GONE
        icon.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.black))
        layout.setBackgroundResource(android.R.color.transparent)
    }
}