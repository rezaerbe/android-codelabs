package com.erbe.people.ui.photo

import android.net.Uri
import android.os.Bundle
import android.transition.Fade
import android.view.View
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.erbe.people.R
import com.erbe.people.databinding.PhotoFragmentBinding
import com.erbe.people.util.getNavigationController
import com.erbe.people.ui.viewBindings

/**
 * Shows the specified [DrawableRes] as a full-screen photo.
 */
class PhotoFragment : Fragment(R.layout.photo_fragment) {

    companion object {
        private const val ARG_PHOTO = "photo"

        fun newInstance(photo: Uri) = PhotoFragment().apply {
            arguments = Bundle().apply {
                putParcelable(ARG_PHOTO, photo)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = Fade()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val photo = arguments?.getParcelable<Uri>(ARG_PHOTO)
        if (photo == null) {
            if (isAdded) {
                parentFragmentManager.popBackStack()
            }
            return
        }
        getNavigationController().updateAppBar(hidden = true)
        val binding by viewBindings(PhotoFragmentBinding::bind)
        Glide.with(this).load(photo).into(binding.photo)
    }
}