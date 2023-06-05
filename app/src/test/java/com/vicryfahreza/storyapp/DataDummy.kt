package com.vicryfahreza.storyapp

import com.vicryfahreza.storyapp.response.ListStoryItem

object DataDummy {

    fun generateDummyQuoteResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val stories = ListStoryItem(
                i.toString(),
                "author + $i",
                "name $i",
                "description $i",
                i.toDouble(),
                "id $i",
                i.toDouble()
            )
            items.add(stories)
        }
        return items
    }
}