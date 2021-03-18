package com.erbe.people.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.erbe.people.data.ChatRepository
import com.erbe.people.data.DefaultChatRepository

class MainViewModel @JvmOverloads constructor(
    application: Application,
    repository: ChatRepository = DefaultChatRepository.getInstance(application)
) : AndroidViewModel(application) {

    /**
     * All the contacts.
     */
    val contacts = repository.getContacts()
}