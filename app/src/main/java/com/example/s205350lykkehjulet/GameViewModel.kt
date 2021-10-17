package com.example.s205350lykkehjulet

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {

    init {
        getNextWord()
    }
    private val _score = MutableLiveData(0)
    //Use of Backing Properties to return immutable object
    val score: LiveData<Int>
        get() = _score

    private val _lives = MutableLiveData(0)
    val lives: LiveData<Int>
        get() = _lives

    private var wordsList: MutableList<String> = mutableListOf()
    private lateinit var currentWordToBeGuessed: String
    private lateinit var shownWordToBeGuessed: String

    private fun getNextWord() {
        //TODO: add catagory
        currentWordToBeGuessed = allWordsList.random()
        shownWordToBeGuessed = ""
        for (i in 0..currentWordToBeGuessed.length) shownWordToBeGuessed+="_"

        if (wordsList.contains(currentWordToBeGuessed)) {
            getNextWord()
        } else {
            wordsList.add(currentWordToBeGuessed)
        }
    }


    fun isUserImputMatch(playerInputLetter: String): Boolean {
        if (shownWordToBeGuessed.contains(playerInputLetter)){
            //TODO replace with letter
            //shownWordToBeGuessed.replace()
        }
        return true
    }
}