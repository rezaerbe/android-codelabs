package com.erbe.reply.data

import androidx.recyclerview.widget.DiffUtil

/**
 * Alias to represent a folder (a String title) into which emails can be placed.
 */
typealias EmailFolder = String

object EmailFolderDiff : DiffUtil.ItemCallback<EmailFolder>() {
    override fun areItemsTheSame(oldItem: EmailFolder, newItem: EmailFolder) = oldItem == newItem
    override fun areContentsTheSame(oldItem: EmailFolder, newItem: EmailFolder) = oldItem == newItem
}
