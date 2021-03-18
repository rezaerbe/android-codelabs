package com.erbe.reply.ui.nav

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.recyclerview.widget.DiffUtil
import com.erbe.reply.data.EmailFolder
import com.erbe.reply.data.EmailFolderDiff
import com.erbe.reply.ui.home.Mailbox

/**
 * A sealed class which encapsulates all objects [NavigationAdapter] is able to display.
 */
sealed class NavigationModelItem {

    /**
     * A class which represents a checkable, navigation destination such as 'Inbox' or 'Sent'.
     */
    data class NavMenuItem(
        val id: Int,
        @DrawableRes val icon: Int,
        @StringRes val titleRes: Int,
        val mailbox: Mailbox,
        var checked: Boolean
    ) : NavigationModelItem()

    /**
     * A class which is used to show a section divider (a subtitle and underline) between
     * sections of different NavigationModelItem types.
     */
    data class NavDivider(val title: String) : NavigationModelItem()

    /**
     * A class which is used to show an [EmailFolder] in the [NavigationAdapter].
     */
    data class NavEmailFolder(val emailFolder: EmailFolder) : NavigationModelItem()

    object NavModelItemDiff : DiffUtil.ItemCallback<NavigationModelItem>() {
        override fun areItemsTheSame(
            oldItem: NavigationModelItem,
            newItem: NavigationModelItem
        ): Boolean {
            return when {
                oldItem is NavMenuItem && newItem is NavMenuItem ->
                    oldItem.id == newItem.id
                oldItem is NavEmailFolder && newItem is NavEmailFolder ->
                    EmailFolderDiff.areItemsTheSame(oldItem.emailFolder, newItem.emailFolder)
                else -> oldItem == newItem
            }
        }

        override fun areContentsTheSame(
            oldItem: NavigationModelItem,
            newItem: NavigationModelItem
        ): Boolean {
            return when {
                 oldItem is NavMenuItem && newItem is NavMenuItem ->
                     oldItem.icon == newItem.icon &&
                     oldItem.titleRes == newItem.titleRes &&
                     oldItem.checked == newItem.checked
                oldItem is NavEmailFolder && newItem is NavEmailFolder ->
                    EmailFolderDiff.areContentsTheSame(oldItem.emailFolder, newItem.emailFolder)
                else -> false
            }
        }
    }
}