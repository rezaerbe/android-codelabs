package com.erbe.people.ui.main

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.erbe.people.R
import com.erbe.people.databinding.MainFragmentBinding
import com.erbe.people.util.getNavigationController
import com.erbe.people.ui.viewBindings

/**
 * The main chat list screen.
 */
class MainFragment : Fragment(R.layout.main_fragment) {

    private val binding by viewBindings(MainFragmentBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exitTransition = TransitionInflater.from(context).inflateTransition(R.transition.slide_top)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val navigationController = getNavigationController()
        navigationController.updateAppBar(false)
        val viewModel: MainViewModel by viewModels()

        val contactAdapter = ContactAdapter { id ->
            navigationController.openChat(id, null)
        }
        viewModel.contacts.observe(viewLifecycleOwner, Observer { contacts ->
            contactAdapter.submitList(contacts)
        })
        binding.contacts.run {
            layoutManager = LinearLayoutManager(view.context)
            setHasFixedSize(true)
            adapter = contactAdapter
        }
    }
}