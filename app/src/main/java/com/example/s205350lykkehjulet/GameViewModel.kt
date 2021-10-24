package com.example.s205350lykkehjulet

import android.util.Log
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {

    //Use of Backing Properties to return immutable values
    private var _score = 0
    val score: Int
        get() = _score

    private var _lives = 5
    val lives: Int
        get() = _lives

    private var _isWon = false
    val isWon: Boolean
        get() = _isWon

    private lateinit var _category: String
    val category: String
        get() = _category

    private lateinit var _shownWordToBeGuessedAsArray: CharArray
    val shownWordToBeGuessedAsArray: CharArray
        get() = _shownWordToBeGuessedAsArray

    private val lastGuessedChar: Char
        get() = playerGuessedCharacters.last()

    private lateinit var _wheelResult: String
    val wheelResult: String
        get() = _wheelResult

    private var timesOfLuckyWheelSpins = 0
    private var playerGuessedCharacters = mutableListOf<Char>()
    private lateinit var currentWordToBeGuessed: String

    init {
        Log.d("GameViewModel", "ViewModel initialized")
    }

    /**
     * Creates a char array of currentWordToBeGuessed where all chars are substituted by '_'
     * This is used for the initial creation of the RecyclerView
     */
    private fun createHiddenWordForDisplay() {
        var tempString =""
        for (i in currentWordToBeGuessed.indices) {
            tempString += if (currentWordToBeGuessed[i] == ' ') {
                " "
            } else "_"
        }
        _shownWordToBeGuessedAsArray = tempString.toCharArray()
    }

    /**
     * Sets _wheelResult to random wheel property
     * This method doesn't allow Joker values ("Bankrupt", "Miss Turn" or "Extra Turn") on the first roll
     *
     */
    fun spinLuckyWheel() {
        val random = (1..22).random()
        _wheelResult = when (random) {
            in 1..2 -> "100"           //2 x 100
            3 -> "300"                 //1 x 300
            in 4..9 -> "500"           //6 x 500
            in 10..11 -> "600"         //2 x 600
            in 12..16 -> "800"         //5 x 800
            in 17..18 -> "1000"        //2 x 1000
            19 -> "1500"               //1 x 1500
            20 -> "Bankrupt"           //1 x Bankrupt
            21 -> "Extra Turn"         //1 x Extra Turn
            22 -> "Miss Turn"          //1 x Lost Turn
            else -> throw Exception("Random generator not generating a number from 1 to 22")
        }
        timesOfLuckyWheelSpins++

        //Avoid getting bankrupt when player is already bankrupt (eg. at game start)
        if ((score == 0 && wheelResult == "Bankrupt")
            //Avoid Extra Turn or Miss Turn when game is just started
            || ((timesOfLuckyWheelSpins == 1)
                    && (wheelResult == "Extra Turn" || wheelResult == "Miss Turn"))
        ) {
            timesOfLuckyWheelSpins=0
            spinLuckyWheel()
        }
    }

    /**
     * Return whether the currentWordToBeGuessed contains the player input
     */
    fun isUserInputMatch(playerInputLetter: Char): Boolean {
        val playerInputLetterLC = playerInputLetter.lowercaseChar()
        return if (currentWordToBeGuessed.contains(playerInputLetterLC, ignoreCase = true)
            && !playerGuessedCharacters.contains(playerInputLetterLC)
        ) {
            playerGuessedCharacters.add(playerInputLetterLC)
            _shownWordToBeGuessedAsArray = updateShownWordToBeGuessedForDisplay()
            if (!shownWordToBeGuessedAsArray.contains('_')) {
                _isWon = true
            }
            true
        } else {
            playerGuessedCharacters.add(playerInputLetterLC)
            _lives--
            false
        }
    }

    /**
     * Updates and returns the _shownWordToBeGuessedAsArray
     */
    private fun updateShownWordToBeGuessedForDisplay(): CharArray {
        var tempUpdatedShownWord = ""
        for (i in currentWordToBeGuessed.indices) {
            if (playerGuessedCharacters.contains(currentWordToBeGuessed[i].lowercaseChar())
                || currentWordToBeGuessed[i].toString() == " "
            ) {
                tempUpdatedShownWord += currentWordToBeGuessed[i]
            } else tempUpdatedShownWord += "_"
        }
        _shownWordToBeGuessedAsArray = tempUpdatedShownWord.toCharArray()
        return _shownWordToBeGuessedAsArray
    }

    /**
     * Updates the player score or lives depending on the wheelResult
     */
    fun doWheelResultAction() {
        when {
            wheelResult.isDigitsOnly() -> {
                val wheelValue = wheelResult.toInt()
                _score += (wheelValue * currentWordToBeGuessed.filter {
                    it.equals(
                        lastGuessedChar,
                        ignoreCase = true
                    )
                }.count())
            }
            wheelResult == "Bankrupt" -> _score = 0
            wheelResult == "Miss Turn" -> _lives--
            wheelResult == "Extra Turn" -> _lives++
        }
    }


    /**
     * Resets all appropriate values for a fresh game
     */
    fun newGame() {
        Log.d("GameViewModel", "newGame")

        _lives = 5
        _score = 0
        _isWon = false
        timesOfLuckyWheelSpins = 0
        playerGuessedCharacters = mutableListOf()
        if (playerGuessedCharacters.isNotEmpty()) throw Exception("PlayerGuessedCharacter array doesnt reset")
        createHiddenWordForDisplay()
        spinLuckyWheel()
    }

    /**
     * Sets the _category and the currentWordToBeGuessed from randomCategoryAndWord
     */
    fun setRandomCategoryAndWord(randomCategoryAndWord: String) {
        val tempArray = randomCategoryAndWord.split(",").toTypedArray()
        _category = tempArray[0]
        currentWordToBeGuessed = tempArray[1]
    }
}