package com.erbe.people.util

import android.app.RemoteInput
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.erbe.people.data.ChatRepository
import com.erbe.people.data.DefaultChatRepository

/**
 * Handles the "Reply" action in the chat notification.
 */
class ReplyReceiver : BroadcastReceiver() {

    companion object {
        const val KEY_TEXT_REPLY = "reply"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val repository: ChatRepository = DefaultChatRepository.getInstance(context)

        val results = RemoteInput.getResultsFromIntent(intent) ?: return
        // The message typed in the notification reply.
        val input = results.getCharSequence(KEY_TEXT_REPLY)?.toString()
        val uri = intent.data ?: return
        val chatId = uri.lastPathSegment?.toLong() ?: return

        if (chatId > 0 && !input.isNullOrBlank()) {
            repository.sendMessage(chatId, input.toString(), null, null)
            // We should update the notification so that the user can see that the reply has been
            // sent.
            repository.updateNotification(chatId)
        }
    }
}