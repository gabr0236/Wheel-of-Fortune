package com.example.s205350lykkehjulet

import android.content.Context

/**
 * Class for accessing the strings.xml string-array containing categories and words
 */
class Datasource(private val context: Context) {
    fun getRandomCategoryAndWord(): String {
        return context.resources.getStringArray(R.array.category_and_words).random()
    }
}