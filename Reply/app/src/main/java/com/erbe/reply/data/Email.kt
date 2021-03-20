package com.erbe.reply.data

import androidx.recyclerview.widget.DiffUtil
import com.erbe.reply.ui.home.Mailbox

/**
 * A simple data class to represent an Email.
 */
data class Email(
    val id: Long,
    val sender: Account,
    val recipients: List<Account> = emptyList(),
    val subject: String = "",
    val body: String = "",
    val attachments: List<EmailAttachment> = emptyList(),
    var isImportant: Boolean = false,
    var isStarred: Boolean = false,
    var mailbox: Mailbox = Mailbox.INBOX
) {
    val senderPreview: String = "${sender.fullName} - 4 hrs ago"
    val hasBody: Boolean = body.isNotBlank()
    val hasAttachments: Boolean = attachments.isNotEmpty()
    val recipientsPreview: String = recipients
        .map { it.firstName }
        .fold("") { name, acc -> "$acc, $name" }
    val nonUserAccountRecipients = recipients
        .filterNot { AccountStore.isUserAccount(it.uid) }
}

object EmailDiffCallback : DiffUtil.ItemCallback<Email>() {
    override fun areItemsTheSame(oldItem: Email, newItem: Email) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Email, newItem: Email) = oldItem == newItem
}