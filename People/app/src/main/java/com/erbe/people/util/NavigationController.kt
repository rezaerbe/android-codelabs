package com.erbe.people.util

import android.net.Uri
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment

/**
 * Common interface between [MainActivity] and [BubbleActivity].
 */
interface NavigationController {

    fun openChat(id: Long, prepopulateText: String?)

    fun openPhoto(photo: Uri)

    /**
     * Updates the appearance and functionality of the app bar.
     *
     * @param showContact Whether to show contact information instead the screen title.
     * @param hidden Whether to hide the app bar.
     * @param body Provide this function to update the content of the app bar.
     */
    fun updateAppBar(
        showContact: Boolean = true,
        hidden: Boolean = false,
        body: (name: TextView, icon: ImageView) -> Unit = { _, _ -> }
    )
}

fun Fragment.getNavigationController() = requireActivity() as NavigationController