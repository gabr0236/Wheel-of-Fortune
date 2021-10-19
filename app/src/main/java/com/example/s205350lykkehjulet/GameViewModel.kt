package com.example.s205350lykkehjulet

import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {

    private var _score = 0
    //Use of Backing Properties to return immutable object
    val score: Int
        get() = _score

    private var _lives = 5
    val lives: Int
        get() = _lives
    private var timesOfLuckyWheelSpins = 0
    private var wordsList = mutableListOf<String>()
    private lateinit var currentWordToBeGuessed: String
    private var playerGuessedCharacters = mutableListOf<Char>()
    val lastGuessedChar: Char
        get() = playerGuessedCharacters.last()

    private lateinit var _shownWordToBeGuessed: String
    val shownWordToBeGuessed: String
        get() = _shownWordToBeGuessed

    private lateinit var _wheelResult: String
    val wheelResult: String
        get() = _wheelResult

    init {
        getNextWord()
        spinLuckyWheel()
    }

    private fun getNextWord() {
        //TODO: add catagory vv
        this.currentWordToBeGuessed = allWordsList.random()
        _shownWordToBeGuessed = ""

        for (i in 0..this.currentWordToBeGuessed.length) _shownWordToBeGuessed += "_"
        _shownWordToBeGuessed = insertSpacesBetweenLetters(_shownWordToBeGuessed)

        if (wordsList.contains(this.currentWordToBeGuessed)) {
            //TODO: this should eventually end the game
            if (wordsList.count() == allWordsList.count()) throw Exception("All words have been guessed")
            else getNextWord()

        } else {
            wordsList.add(this.currentWordToBeGuessed)
        }
    }

    private fun spinLuckyWheel(){
        //22 Different fields in total
        val random = (1..22).random()
        _wheelResult = when (random) {
            in 1..2 ->   "100"           //2 x 100
            3 ->         "300"           //1 x 300
            in 4..9 ->   "500"           //6 x 500
            in 10..11 -> "600"           //2 x 600
            in 12..16 -> "800"           //5 x 800
            in 17..18 -> "1000"          //2 x 1000
            19 ->        "1500"          //1 x 1500
            20 ->        "Bankrupt"      //1 x Bankrupt
            21 ->        "Extra Turn"    //1 x Extra Turn
            22 ->        "Miss Turn"     //1 x Lost Turn
            else -> throw Exception("Random generator not generating a number from 1 to 22")
        }
        timesOfLuckyWheelSpins++
        //Avoid getting bankrupt when player is already bankrupt (eg. at game start)
        if ((_score==0 && _wheelResult=="Bankrupt")
            //Avoid Extra Turn or Miss Turn when game is just started TODO: should the game play like this??
            || ((timesOfLuckyWheelSpins==1)
                    && (_wheelResult=="Extra Turn" || _wheelResult=="Miss Turn")))
                        spinLuckyWheel()
    }

    //TODO: måske nemmere med char?
    fun isUserImputMatch(playerInputLetter: Char): Boolean {
        val playerInputLetterLC = playerInputLetter.lowercaseChar()
            var tempWordSoFar = ""
            if (currentWordToBeGuessed.contains(playerInputLetterLC)
                && !playerGuessedCharacters.contains(playerInputLetterLC)) {
                playerGuessedCharacters.add(playerInputLetterLC)

                for (i in currentWordToBeGuessed.indices) {
                    if (playerGuessedCharacters.contains(currentWordToBeGuessed[i])){
                        tempWordSoFar += currentWordToBeGuessed[i]
                    }
                    else tempWordSoFar += "_"
                }
                doWheelAction(currentWordToBeGuessed.filter { it == playerInputLetterLC }.count())
                _shownWordToBeGuessed = insertSpacesBetweenLetters(tempWordSoFar)
                spinLuckyWheel()
                return true
            }
        return false
    }

    private fun doWheelAction(occurrencesOfPlayerInputLetter: Int) {
            if (wheelResult.isDigitsOnly()){
                val wheelValue = _wheelResult.toInt()
                _score+= (wheelValue * occurrencesOfPlayerInputLetter)
            }
            else if (wheelResult == "Bankrupt") _score = 0
            else if (wheelResult == "Miss Turn") loseLife()
            else if (wheelResult == "Extra Turn") _lives++
            else throw Exception("_wheelResult is behaving strangely")
    }

    private fun loseLife() {
        //TODO: endgame skal ske i fragment måske?
        TODO("Not yet implemented")
    }

    private fun insertSpacesBetweenLetters(s: String): String{
        return s.replace(".(?!$)".toRegex(), "$0 ")
    }
}