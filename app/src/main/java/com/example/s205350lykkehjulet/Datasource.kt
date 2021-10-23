package com.example.s205350lykkehjulet

import android.content.Context

class Datasource(private val context: Context) {
    fun getCategoriesAndWords(): Array<String> {
        return context.resources.getStringArray(R.array.category_and_words)
    }
}