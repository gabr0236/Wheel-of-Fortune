package com.example.s205350lykkehjulet

import android.util.Log
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {

    private var _score = 0
    val score: Int //Use of Backing Properties to return immutable object
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
    private lateinit var currentWordToBeGuessed: String
    private var playerGuessedCharacters = mutableListOf<Char>()
    private lateinit var currentCategoryAndWord: Array<String>

    init {
        //newGame()
        Log.d("initTest", "Init viewmodel called")
    }

    private fun getNextWord() {
        //TODO SLET metode
        createHiddenWordForDisplay()
    }

    private fun createHiddenWordForDisplay() {
        var tempString =""
        for (i in currentWordToBeGuessed.indices) {
            tempString += if (currentWordToBeGuessed[i] == ' ') {
                " "
            } else "_"
        }
        _shownWordToBeGuessedAsArray = tempString.toCharArray()
    }

    fun spinLuckyWheel() {
        //22 Different fields in total
        val random = (1..22).random()
        _wheelResult = when (random) {
            in 1..2 -> "100"           //2 x 100
            3 -> "300"           //1 x 300
            in 4..9 -> "500"           //6 x 500
            in 10..11 -> "600"           //2 x 600
            in 12..16 -> "800"           //5 x 800
            in 17..18 -> "1000"          //2 x 1000
            19 -> "1500"          //1 x 1500
            20 -> "Bankrupt"      //1 x Bankrupt
            21 -> "Extra Turn"    //1 x Extra Turn
            22 -> "Miss Turn"     //1 x Lost Turn
            else -> throw Exception("Random generator not generating a number from 1 to 22")
        }
        timesOfLuckyWheelSpins++

        //TODO: til testvvv
        //if (timesOfLuckyWheelSpins>1) _wheelResult = "Miss Turn"
        //if (timesOfLuckyWheelSpins>2) _wheelResult = "Bankrupt"


        //Avoid getting bankrupt when player is already bankrupt (eg. at game start)
        if ((score == 0 && wheelResult == "Bankrupt")
            //Avoid Extra Turn or Miss Turn when game is just started TODO: should the game play like this??
            || ((timesOfLuckyWheelSpins == 1)
                    && (wheelResult == "Extra Turn" || wheelResult == "Miss Turn"))
        ) {
            spinLuckyWheel()
        }
    }

    fun isUserInputMatch(playerInputLetter: Char): Boolean {
        val playerInputLetterLC = playerInputLetter.lowercaseChar()
        return if (currentWordToBeGuessed.contains(playerInputLetterLC, ignoreCase = true)
            && !playerGuessedCharacters.contains(playerInputLetterLC)
        ) {
            playerGuessedCharacters.add(playerInputLetterLC)
            _shownWordToBeGuessedAsArray = updateHiddenWordForDisplay()
            if (!shownWordToBeGuessedAsArray.contains('_')) {
                _isWon = true
            }
            true
        } else {
            //TODO: udenfor if-else vv ??
            playerGuessedCharacters.add(playerInputLetterLC)
            loseLife()
            false
        }
    }

    private fun updateHiddenWordForDisplay(): CharArray {
        var updatedHiddenWord = ""
        for (i in currentWordToBeGuessed.indices) {
            if (playerGuessedCharacters.contains(currentWordToBeGuessed[i].lowercaseChar())
                || currentWordToBeGuessed[i].toString() == " "
            ) {
                updatedHiddenWord += currentWordToBeGuessed[i]
            } else updatedHiddenWord += "_"
        }
        return updatedHiddenWord.toCharArray()
    }

    fun doWheelAction() {
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
            wheelResult == "Miss Turn" -> loseLife()
            wheelResult == "Extra Turn" -> _lives++
        }
    }

    private fun loseLife() {
        _lives--
    }

    //private fun insertSpacesBetweenLetters(s: String): String {
    //    return s.replace(".(?!$)".toRegex(), "$0 ")
    //}

    fun newGame() {
        _lives = 5
        _score = 0
        _isWon = false
        timesOfLuckyWheelSpins = 0
        playerGuessedCharacters = mutableListOf()
        if (playerGuessedCharacters.isNotEmpty()) throw Exception("PlayerGuessedCharacter array doesnt reset")
        getNextWord()
        spinLuckyWheel()
    }

    fun setRandomCategoryAndWord(array: Array<String>) {
        //TODO NÅET HERTIL, har lige lavet dether og nu skal det videre i gamefragment til hvordan ordet læses
        //TODO måske fucker det her noget op siden det her plejede at ske i getNextWord?
        currentCategoryAndWord= array[(array.indices).random()].split(",").toTypedArray()
        _category = currentCategoryAndWord[0]
        currentWordToBeGuessed = currentCategoryAndWord[1]
    }
}