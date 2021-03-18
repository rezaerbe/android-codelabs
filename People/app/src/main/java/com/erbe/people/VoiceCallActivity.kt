package com.erbe.people

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.erbe.people.databinding.VoiceCallActivityBinding
import com.erbe.people.ui.viewBindings

/**
 * A dummy voice call screen. It only shows the icon and the name.
 */
class VoiceCallActivity : AppCompatActivity(R.layout.voice_call_activity) {

    companion object {
        const val EXTRA_NAME = "name"
        const val EXTRA_ICON = "icon"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val name = intent.getStringExtra(EXTRA_NAME)
        val icon = intent.getStringExtra(EXTRA_ICON)
        if (name == null || icon == null) {
            finish()
            return
        }
        val binding: VoiceCallActivityBinding by viewBindings(VoiceCallActivityBinding::bind)
        binding.name.text = name
        Glide.with(binding.icon)
            .load(icon)
            .apply(RequestOptions.circleCropTransform())
            .into(binding.icon)
    }
}