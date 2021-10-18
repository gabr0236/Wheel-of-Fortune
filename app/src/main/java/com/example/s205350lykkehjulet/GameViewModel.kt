package com.example.s205350lykkehjulet

import android.util.Log
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {

    private val _score = 0

    //Use of Backing Properties to return immutable object
    val score: Int
        get() = _score

    private val _lives = 0
    val lives: Int
        get() = _lives

    private var wordsList = mutableListOf<String>()
    private lateinit var currentWordToBeGuessed: String
    private var playerGuessedCharacters = mutableListOf<Char>()

    private lateinit var _shownWordToBeGuessed: String
    val shownWordToBeGuessed: String
        get() = _shownWordToBeGuessed

    init {
        getNextWord()
    }

    private fun getNextWord() {
        //TODO: add catagory vv
        this.currentWordToBeGuessed = allWordsList.random()
        _shownWordToBeGuessed = ""
        //TODO lav smartere med separetor vv
        for (i in 0..this.currentWordToBeGuessed.length) _shownWordToBeGuessed += "_"
        _shownWordToBeGuessed = _shownWordToBeGuessed.replace(".(?!$)".toRegex(), "$0 ")

        if (wordsList.contains(this.currentWordToBeGuessed)) {
            //TODO: this should eventually end the game
            if (wordsList.count() == allWordsList.count()) throw Exception("All words have been guessed")
            else getNextWord()

        } else {
            wordsList.add(this.currentWordToBeGuessed)
        }
    }

    /**
     *
     */
    fun isUserImputMatch(playerInputLetter: String): Boolean {
        if (playerInputLetter.length == 1) {
            var tempWordSoFar = ""
            if (currentWordToBeGuessed.contains(playerInputLetter)
                && !playerGuessedCharacters.contains(playerInputLetter[0])) {
                playerGuessedCharacters.addAll(playerInputLetter.toMutableList())
                increacePlayerScore()

                for (i in currentWordToBeGuessed.indices) {
                    if (playerGuessedCharacters.contains(currentWordToBeGuessed[i])){
                        tempWordSoFar += currentWordToBeGuessed[i]
                    }
                    else tempWordSoFar += "_"
                }
                _shownWordToBeGuessed = tempWordSoFar
                return true
            }
        } else throw Exception("player input not 1 character")
        return false
    }

    private fun increacePlayerScore() {
        TODO("Not yet implemented")
    }
}