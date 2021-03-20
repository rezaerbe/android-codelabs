package com.erbe.reply.util

import android.content.Context
import android.os.Build
import android.widget.TextView

@Suppress("DEPRECATION")
fun TextView.setTextAppearanceCompat(context: Context, resId: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        setTextAppearance(resId)
    } else {
        setTextAppearance(context, resId)
    }
}