package com.erbe.reply.data

import com.erbe.reply.R

/**
 * A static data store of [SearchSuggestion]s.
 */
object SearchSuggestionStore {

    val YESTERDAY_SUGGESTIONS = listOf(
        SearchSuggestion(
            R.drawable.ic_schedule,
            "481 Van Brunt Street",
            "Brooklyn, NY"
        ),
        SearchSuggestion(
            R.drawable.ic_home,
            "Home",
            "199 Pacific Street, Brooklyn, NY"
        )
    )

    val THIS_WEEK_SUGGESTIONS = listOf(
        SearchSuggestion(
            R.drawable.ic_schedule,
            "BEP GA",
            "Forsyth Street, New York, NY"
        ),
        SearchSuggestion(
            R.drawable.ic_schedule,
            "Sushi Nakazawa",
            "Commerce Street, New York, NY"
        ),
        SearchSuggestion(
            R.drawable.ic_schedule,
            "IFC Center",
            "6th Avenue, New York, NY"
        )
    )
}