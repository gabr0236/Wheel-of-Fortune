package com.example.s205350lykkehjulet

import android.text.Spannable
import android.text.SpannableString
import android.text.style.TtsSpan
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {

    private val _score = MutableLiveData(0)
    //Use of Backing Properties to return immutable object
    val score: LiveData<Int>
        get() = _score

    private val _lives = MutableLiveData(0)
    val lives: LiveData<Int>
        get() = _lives

    private var wordsList: MutableList<String> = mutableListOf()
    private lateinit var currentWordToBeGuessed: String
    private lateinit var _currentScrambledWord: String

    private fun getNextWord() {
        currentWordToBeGuessed = allWordsList.random()
        val tempWord = currentWordToBeGuessed.toCharArray()
        tempWord.shuffle()

        while (tempWord.toString().equals(currentWordToBeGuessed, false)) {
            tempWord.shuffle()
        }
        if (wordsList.contains(currentWordToBeGuessed)) {
            getNextWord()
        } else {
            _currentScrambledWord = String(tempWord)

            wordsList.add(currentWordToBeGuessed)
        }
    }
}