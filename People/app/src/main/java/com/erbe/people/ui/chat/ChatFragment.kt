package com.erbe.people.ui.chat

import android.content.Intent
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.erbe.people.R
import com.erbe.people.VoiceCallActivity
import com.erbe.people.databinding.ChatFragmentBinding
import com.erbe.people.util.getNavigationController
import com.erbe.people.ui.viewBindings

/**
 * The chat screen. This is used in the full app (MainActivity) as well as in the expanded Bubble
 * (BubbleActivity).
 */
class ChatFragment : Fragment(R.layout.chat_fragment) {

    companion object {
        private const val ARG_ID = "id"
        private const val ARG_FOREGROUND = "foreground"
        private const val ARG_PREPOPULATE_TEXT = "prepopulate_text"

        fun newInstance(id: Long, foreground: Boolean, prepopulateText: String? = null) =
            ChatFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_ID, id)
                    putBoolean(ARG_FOREGROUND, foreground)
                    putString(ARG_PREPOPULATE_TEXT, prepopulateText)
                }
            }
    }

    private val viewModel: ChatViewModel by viewModels()
    private val binding by viewBindings(ChatFragmentBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        enterTransition =
            TransitionInflater.from(context).inflateTransition(R.transition.slide_bottom)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val id = arguments?.getLong(ARG_ID)
        if (id == null) {
            parentFragmentManager.popBackStack()
            return
        }
        val prepopulateText = arguments?.getString(ARG_PREPOPULATE_TEXT)
        val navigationController = getNavigationController()

        viewModel.setChatId(id)

        val messageAdapter = MessageAdapter(view.context) { uri ->
            navigationController.openPhoto(uri)
        }
        val linearLayoutManager = LinearLayoutManager(view.context).apply {
            stackFromEnd = true
        }
        binding.messages.run {
            layoutManager = linearLayoutManager
            adapter = messageAdapter
        }

        viewModel.contact.observe(viewLifecycleOwner) { contact ->
            if (contact == null) {
                Toast.makeText(view.context, "Contact not found", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            } else {
                // requireActivity().setLocusContext(LocusId(contact.shortcutId), null)
                navigationController.updateAppBar { name, icon ->
                    name.text = contact.name
                    // icon.setImageIcon(Icon.createWithAdaptiveBitmapContentUri(contact.iconUri))
                    Glide.with(this).load(contact.iconUri).circleCrop().into(icon)
                    startPostponedEnterTransition()
                }
            }
        }

        viewModel.messages.observe(viewLifecycleOwner) { messages ->
            messageAdapter.submitList(messages)
            linearLayoutManager.scrollToPosition(messages.size - 1)
        }

        if (prepopulateText != null) {
            binding.input.setText(prepopulateText)
        }

        binding.input.setOnImageAddedListener { contentUri, mimeType, label ->
            viewModel.setPhoto(contentUri, mimeType)
            if (binding.input.text.isNullOrBlank()) {
                binding.input.setText(label)
            }
        }

        viewModel.photo.observe(viewLifecycleOwner) { uri ->
            if (uri == null) {
                binding.photo.visibility = View.GONE
            } else {
                binding.photo.visibility = View.VISIBLE
                Glide.with(binding.photo).load(uri).into(binding.photo)
            }
        }

        binding.voiceCall.setOnClickListener {
            voiceCall()
        }
        binding.send.setOnClickListener {
            send()
        }
        binding.input.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                send()
                true
            } else {
                false
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val foreground = arguments?.getBoolean(ARG_FOREGROUND) == true
        viewModel.foreground = foreground
    }

    override fun onStop() {
        super.onStop()
        viewModel.foreground = false
    }

    private fun voiceCall() {
        val contact = viewModel.contact.value ?: return
        startActivity(
            Intent(requireActivity(), VoiceCallActivity::class.java)
                .putExtra(VoiceCallActivity.EXTRA_NAME, contact.name)
                .putExtra(VoiceCallActivity.EXTRA_ICON, contact.iconUri.toString())
        )
    }

    private fun send() {
        binding.input.text?.let { text ->
            if (text.isNotEmpty()) {
                viewModel.send(text.toString())
                text.clear()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.chat, menu)
        menu.findItem(R.id.action_show_as_bubble)?.let { item ->
            viewModel.showAsBubbleVisible.observe(viewLifecycleOwner) { visible ->
                item.isVisible = visible
            }
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_show_as_bubble -> {
                viewModel.showAsBubble()
                if (isAdded) {
                    parentFragmentManager.popBackStack()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}