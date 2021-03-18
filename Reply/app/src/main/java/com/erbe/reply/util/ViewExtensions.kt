package com.erbe.reply.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.IdRes
import androidx.annotation.Px
import androidx.core.graphics.applyCanvas
import androidx.core.view.ViewCompat
import androidx.core.view.forEach
import com.erbe.reply.R

@Suppress("DEPRECATION")
fun TextView.setTextAppearanceCompat(context: Context, resId: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        setTextAppearance(resId)
    } else {
        setTextAppearance(context, resId)
    }
}
