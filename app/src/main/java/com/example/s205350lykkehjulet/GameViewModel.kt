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

    private var wordsList = mutableListOf<String>()
    private lateinit var currentWordToBeGuessed: String
    private var playerGuessedCharacters = mutableListOf<Char>()

    private lateinit var _shownWordToBeGuessed: String
    val shownWordToBeGuessed: String
        get() = _shownWordToBeGuessed

    private lateinit var _wheelResult: String
    val wheelResult: String
        get() = _wheelResult

    init {
        getNextWord()
        spinWheel()
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

    fun spinWheel(){
        //22 Different fields in total
        val random = (1..22).random()
        if (random<=2)          _wheelResult="100"         //2 x 100
        else if (random<=3)     _wheelResult="300"         //1 x 300
        else if (random<=9)     _wheelResult="500"         //6 x 500
        else if (random<=11)    _wheelResult="600"         //2 x 600
        else if (random<=16)    _wheelResult="800"         //5 x 800
        else if (random<=18)    _wheelResult="1000"        //2 x 1000
        else if (random<=19)    _wheelResult="1500"        //1 x 1500
        else if (random<=20)    _wheelResult="Bankrupt"    //1 x Bankrupt
        else if (random<=21)    _wheelResult="Extra Turn"  //1 x Extra Turn
        else if (random<=22)    _wheelResult="Miss Turn"   //1 x Lost Turn
        else throw Exception("Random generator not generating a number from 1 to 22")
    }

    //TODO: måske nemmere med char?
    fun isUserImputMatch(playerInputLetter: Char): Boolean {
            var tempWordSoFar = ""
            if (currentWordToBeGuessed.contains(playerInputLetter)
                && !playerGuessedCharacters.contains(playerInputLetter)) {
                playerGuessedCharacters.add(playerInputLetter)

                for (i in currentWordToBeGuessed.indices) {
                    if (playerGuessedCharacters.contains(currentWordToBeGuessed[i])){
                        tempWordSoFar += currentWordToBeGuessed[i]
                    }
                    else tempWordSoFar += "_"
                }
                doWheelAction(currentWordToBeGuessed.filter { it == playerInputLetter }.count())
                _shownWordToBeGuessed = insertSpacesBetweenLetters(tempWordSoFar)
                return true
            }
        return false
    }

    private fun doWheelAction(occurrencesOfPlayerInputLetter: Int) {
            if (_wheelResult.isDigitsOnly()){
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