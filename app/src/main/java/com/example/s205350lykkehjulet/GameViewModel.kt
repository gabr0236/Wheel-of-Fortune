package com.example.s205350lykkehjulet

import android.util.Log
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.s205350lykkehjulet.Data.LetterCard

//TODO resources
const val BANKRUPT = "Bankrupt"
const val MISS_TURN = "Miss Turn"
const val EXTRA_TURN = "Extra Turn"

enum class GameStage {
    IS_SPIN, IS_GUESS;
}

class GameViewModel : ViewModel() {

    private var _gameStage = MutableLiveData<GameStage>()
    val gameStage: LiveData<GameStage>
        get() = _gameStage

    private val _score = MutableLiveData<Int>()
    val score: LiveData<Int> = _score

    private val _lives = MutableLiveData<Int>()
    val lives: LiveData<Int> = _lives

    private var _isWon = false
    val isWon: Boolean
        get() = _isWon

    private val _category = MutableLiveData<String>()
    val category: LiveData<String> = _category

    //TODO: er der nogen grund til at det her skal bruge binding?
    private val _letterCardList = MutableLiveData<MutableList<LetterCard>>()
    val letterCardList: LiveData<MutableList<LetterCard>> = _letterCardList

    private val _wheelResult = MutableLiveData<String>()
    val wheelResult: LiveData<String> = _wheelResult

    private var timesOfLuckyWheelSpins = 0
    private var _guessedCharacters = mutableListOf<Char>()
    val guessedCharacters: List<Char>
        get() = _guessedCharacters

    private val _guessedCharacterString = MutableLiveData("Letters\n")
    val guessedCharacterString: LiveData<String> = _guessedCharacterString

    private var _gameQuote = MutableLiveData<String>()
    val gameQuote: LiveData<String>
        get() = _gameQuote
    fun setGameQuote(newGameQuote: String){
        _gameQuote.value=newGameQuote
    }

    private lateinit var _currentWordToBeGuessed: String
    //Only return currentWordToBeGuessed if the game is over
    val currentWordToBeGuessed: String
        get() {
            return if (isWon || lives.value!! <= 0) _currentWordToBeGuessed
            else "It's a secret!"
        }

    val numberOfGuesses: Int
    get() = _guessedCharacters.size


    init {
        Log.d(TAG, "ViewModel initialized")
    }

    fun getPosOfLastGuessedChars(): List<Int> {
        var positions = mutableListOf<Int>()
        for (i in _currentWordToBeGuessed.indices) {
            if (_currentWordToBeGuessed[i].lowercaseChar() == _guessedCharacters.last()
                    .lowercaseChar()
            )
                positions.add(i)
        }
        return positions
    }

    /**
     * Sets _wheelResult to random wheel property
     * This method doesn't allow Joker values ("Bankrupt", "Miss Turn" or "Extra Turn") on the first roll
     */
    fun spinLuckyWheel() {
        val random = (1..22).random()
        _wheelResult.value = when (random) {
            //TODO skal de her tal være const?
            in 1..2 -> "100"           //2 x 100
            3 -> "300"                 //1 x 300
            in 4..9 -> "500"           //6 x 500
            in 10..11 -> "600"         //2 x 600
            in 12..16 -> "800"         //5 x 800
            in 17..18 -> "1000"        //2 x 1000
            19 -> "1500"               //1 x 1500
            20 -> BANKRUPT             //1 x Bankrupt
            21 -> EXTRA_TURN           //1 x Extra Turn
            22 -> MISS_TURN            //1 x Lost Turn
            else -> throw Exception("Random generator not generating a number from 1 to 22")
        }
        timesOfLuckyWheelSpins++
        //TODO test
        //if (timesOfLuckyWheelSpins>2) _wheelResult.value = EXTRA_TURN
        //Switch to guessing stage if result is digits else spin again
        _gameStage.value = if (wheelResult.value?.isDigitsOnly() == true) {
            GameStage.IS_GUESS
        } else GameStage.IS_SPIN

        //Avoid getting bankrupt when player is already bankrupt (eg. at game start)
        //This is not part of the original game but a design decision for a better user experience
        if ((score.value == 0 && wheelResult.value == BANKRUPT)
            //Avoid Extra Turn or Miss Turn when game is just started
            || ((timesOfLuckyWheelSpins == 1)
                    && (wheelResult.value == EXTRA_TURN || wheelResult.value == MISS_TURN))
        ) {
            timesOfLuckyWheelSpins = 0
            spinLuckyWheel()
        }
    }

    /**
     * Return whether the currentWordToBeGuessed contains the player input
     */
    fun isUserInputMatch(playerInputLetter: Char): Boolean {
        _gameStage.value = GameStage.IS_SPIN
        val playerInputLetterLC = playerInputLetter.lowercaseChar()
        return if (_currentWordToBeGuessed.contains(playerInputLetterLC, ignoreCase = true)
            && !_guessedCharacters.contains(playerInputLetterLC)
        ) {
            saveGuessedChar(playerInputLetterLC)
            _letterCardList.value?.filter {
                it.letter.lowercaseChar() == playerInputLetterLC
            }
                ?.forEach {
                    it.isHidden = false
                }
            if (_letterCardList.value?.all { !it.isHidden || it.letter == ' ' } == true) {
                _isWon = true
            }
            doWheelResultAction()
            true
        } else {
            saveGuessedChar(playerInputLetterLC)
            _lives.value = _lives.value?.minus(1)
            false
        }
    }

    private fun saveGuessedChar(playerInputLetter: Char) {
        _guessedCharacters.add(playerInputLetter)
        _guessedCharacterString.value =
            _guessedCharacterString.value.plus(playerInputLetter.toString() + "\n")
    }

    /**
     * Updates the player score or lives depending on the wheelResult
     */
    fun doWheelResultAction() {
        when {
            wheelResult.value?.isDigitsOnly() == true -> {
                val wheelValue = wheelResult.value?.toInt()
                if (wheelValue != null) {
                    val multiplier =
                        _currentWordToBeGuessed.filter {
                            it.equals(_guessedCharacters.last(), ignoreCase = true)
                        }.count()

                    _score.value = _score.value?.plus(wheelValue * multiplier)
                }
            }
            wheelResult.value == BANKRUPT -> _score.value = 0
            wheelResult.value == MISS_TURN -> _lives.value = _lives.value?.minus(1)
            wheelResult.value == EXTRA_TURN -> _lives.value = _lives.value?.plus(1)
        }
    }

    /**
     * Resets all appropriate values for a fresh game
     */
    fun newGame() {
        Log.d(TAG, "newGame")
        _lives.value = 5
        _score.value = 0
        _isWon = false
        timesOfLuckyWheelSpins = 0
        _guessedCharacters = mutableListOf()
        if (_guessedCharacters.isNotEmpty()) throw Exception("PlayerGuessedCharacter array doesn't reset")
        _guessedCharacterString.value = "Letters\n"
        _gameStage.value = GameStage.IS_SPIN
    }

    /**
     * Sets the _category and the currentWordToBeGuessed from randomCategoryAndWord
     */
    fun setRandomCategoryAndWord(randomCategoryAndWord: String) {
        val tempArray = randomCategoryAndWord.split(",").toTypedArray()
        _category.value = tempArray[0]
        _currentWordToBeGuessed = tempArray[1]

        //TODO kortere
        val tempLetterCardList = mutableListOf<LetterCard>()
        for (i in _currentWordToBeGuessed.indices) {
            tempLetterCardList.add(LetterCard(_currentWordToBeGuessed[i]))
        }
        _letterCardList.value = tempLetterCardList
    }



    companion object {
        private const val TAG = "GameViewModel"
    }
}