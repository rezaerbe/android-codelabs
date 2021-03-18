package com.erbe.databinding.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.erbe.databinding.R
import com.erbe.databinding.data.SimpleViewModelSolution
import com.erbe.databinding.databinding.SolutionBinding

/**
 * Final codelab solution.
 */
class SolutionActivity : AppCompatActivity() {

    // Obtain ViewModel from ViewModelProviders
    private val viewModel by lazy {
        ViewModelProvider(this).get(SimpleViewModelSolution::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: SolutionBinding =
            DataBindingUtil.setContentView(this, R.layout.solution)

        binding.lifecycleOwner = this  // use Fragment.viewLifecycleOwner for fragments

        binding.viewmodel = viewModel
    }
}