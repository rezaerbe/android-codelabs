package com.erbe.databinding.data

import androidx.lifecycle.ViewModel

/**
 * A simple VM for [com.erbe.databinding.ui.PlainOldActivity].
 */
class SimpleViewModel : ViewModel() {
    val name = "Grace"
    val lastName = "Hopper"
    var likes = 0
        private set // This is to prevent external modification of the variable.

    /**
     * Increments the number of likes.
     */
    fun onLike() {
        likes++
    }

    /**
     * Returns popularity in buckets: [Popularity.NORMAL], [Popularity.POPULAR] or [Popularity.STAR]
     */
    val popularity: Popularity
        get() {
            return when {
                likes > 9 -> Popularity.STAR
                likes > 4 -> Popularity.POPULAR
                else -> Popularity.NORMAL
            }
        }
}

enum class Popularity {
    NORMAL,
    POPULAR,
    STAR
}