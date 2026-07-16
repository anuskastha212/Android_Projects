package com.example.esewa_project.data.source

import com.example.esewa_project.R
import com.example.esewa_project.data.model.Category

class CategoryData {

    fun getCategoryData(): List<Category> {
        val categoryList = listOf(
            Category("Mobile", R.drawable.c_mobile),
            Category("Home and Lifestyle", R.drawable.c_home_and_lifestyle),
            Category("Electronic Devices", R.drawable.c_electronic_devices),
            Category("Fashion", R.drawable.c_fashion),
            Category("Grocery", R.drawable.c_grocery),
            Category("Automotive", R.drawable.c_automotive),
            Category("Baby Care", R.drawable.c_baby_care)
        )

        return categoryList
    }

}