package com.example.esewa_project

import android.content.Context
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat

class BannerIndicator {

    fun setupIndicator(context: Context, count: Int, layoutDots: LinearLayout)
    : Array<ImageView?> {

        val dots = arrayOfNulls<ImageView>(count)

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { setMargins(8, 0, 8, 0) }

        for (i in 0 until count) {

            dots[i] = ImageView(context)
            dots[i]?.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.indicator_inactive
                )
            )
            layoutDots.addView(dots[i], params)
        }

        return dots
    }

    fun setCurrentIndicator(context: Context, index: Int, layoutDots: LinearLayout) {

        for (i in 0 until layoutDots.childCount) {

            val imageView =
                layoutDots.getChildAt(i) as ImageView

            imageView.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    if (i == index)
                        R.drawable.indicator_active
                    else
                        R.drawable.indicator_inactive
                )
            )
        }
    }
}