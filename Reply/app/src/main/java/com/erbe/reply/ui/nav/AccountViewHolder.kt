package com.erbe.reply.ui.nav

import androidx.recyclerview.widget.RecyclerView
import com.erbe.reply.data.Account
import com.erbe.reply.databinding.AccountItemLayoutBinding

/**
 * ViewHolder for [AccountAdapter]. Holds a single account which can be selected.
 */
class AccountViewHolder(
    val binding: AccountItemLayoutBinding,
    val listener: AccountAdapter.AccountAdapterListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(accnt: Account) {
        binding.run {
            account = accnt
            accountListener = listener
            executePendingBindings()
        }
    }
}