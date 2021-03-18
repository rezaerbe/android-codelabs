package com.erbe.people.ui.main

import android.graphics.drawable.Icon
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.erbe.people.R
import com.erbe.people.data.Contact
import com.erbe.people.databinding.ChatItemBinding

class ContactAdapter(
    private val onChatClicked: (id: Long) -> Unit
) : ListAdapter<Contact, ContactViewHolder>(DIFF_CALLBACK) {

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        return getItem(position).id
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val holder = ContactViewHolder(parent)
        holder.itemView.setOnClickListener {
            onChatClicked(holder.itemId)
        }
        return holder
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact: Contact = getItem(position)
        // holder.binding.icon.setImageIcon(Icon.createWithAdaptiveBitmapContentUri(contact.iconUri))
        Glide.with(holder.binding.root).load(contact.iconUri).circleCrop().into(holder.binding.icon)
        holder.binding.name.text = contact.name
    }
}

private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Contact>() {
    override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean {
        return oldItem == newItem
    }
}

class ContactViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.chat_item, parent, false)
) {
    val binding: ChatItemBinding = ChatItemBinding.bind(itemView)
}