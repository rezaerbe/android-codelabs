package com.erbe.reply.ui.email

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.erbe.reply.R
import com.erbe.reply.data.EmailStore
import com.erbe.reply.databinding.FragmentEmailBinding
import com.erbe.reply.util.themeColor
import com.google.android.material.transition.MaterialContainerTransform
import kotlin.LazyThreadSafetyMode.NONE

private const val MAX_GRID_SPANS = 3

/**
 * A [Fragment] which displays a single, full email.
 */
class EmailFragment : Fragment() {

    private val args: EmailFragmentArgs by navArgs()
    private val emailId: Long by lazy(NONE) { args.emailId }

    private lateinit var binding: FragmentEmailBinding
    private val attachmentAdapter = EmailAttachmentGridAdapter(MAX_GRID_SPANS)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedElementEnterTransition = MaterialContainerTransform().apply {
            // Scope the transition to a view in the hierarchy so we know it will be added under
            // the bottom app bar but over the elevation scale of the exiting HomeFragment.
            drawingViewId = R.id.nav_host_fragment
            duration = resources.getInteger(R.integer.reply_motion_duration_large).toLong()
            scrimColor = Color.TRANSPARENT
            setAllContainerColors(requireContext().themeColor(R.attr.colorSurface))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEmailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.navigationIcon.setOnClickListener {
            findNavController().navigateUp()
        }

        val email = EmailStore.get(emailId)
        if (email == null) {
            showError()
            return
        }

        binding.run {
            this.email = email

            // Set up the staggered/masonry grid recycler
            attachmentRecyclerView.layoutManager = GridLayoutManager(
                requireContext(),
                MAX_GRID_SPANS
            ).apply {
                spanSizeLookup = attachmentAdapter.variableSpanSizeLookup
            }
            attachmentRecyclerView.adapter = attachmentAdapter
            attachmentAdapter.submitList(email.attachments)
        }
    }

    private fun showError() {
        // Do nothing
    }
}