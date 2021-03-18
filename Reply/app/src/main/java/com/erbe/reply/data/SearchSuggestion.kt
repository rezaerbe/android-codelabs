package com.erbe.reply.data

import androidx.annotation.DrawableRes

/**
 * An object which represents a search suggestion.
 */
data class SearchSuggestion(
  @DrawableRes val iconResId: Int,
  val title: String,
  val subtitle: String
)
