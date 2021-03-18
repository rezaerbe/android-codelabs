package com.erbe.reply.data

import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.DiffUtil
import com.erbe.reply.R

/**
 * An object which represents an account which can belong to a user. A single user can have
 * multiple accounts.
 */
data class Account(
    val id: Long,
    val uid: Long,
    val firstName: String,
    val lastName: String,
    val email: String,
    val altEmail: String,
    @DrawableRes val avatar: Int,
    var isCurrentAccount: Boolean = false
) {
    val fullName: String = "$firstName $lastName"
    @DrawableRes val checkedIcon: Int = if (isCurrentAccount) R.drawable.ic_done else 0
}

object AccountDiffCallback : DiffUtil.ItemCallback<Account>() {
    override fun areItemsTheSame(oldItem: Account, newItem: Account) =
        oldItem.email == newItem.email
    override fun areContentsTheSame(oldItem: Account, newItem: Account) =
        oldItem.firstName == newItem.firstName
            && oldItem.lastName == newItem.lastName
            && oldItem.avatar == newItem.avatar
            && oldItem.isCurrentAccount == newItem.isCurrentAccount
}
