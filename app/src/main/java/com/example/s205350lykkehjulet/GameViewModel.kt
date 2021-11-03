package com.example.s205350lykkehjulet

import android.util.Log
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.s205350lykkehjulet.data.LetterCard

const val BANKRUPT = "Bankrupt"
const val MISS_TURN = "Miss Turn"
const val EXTRA_TURN = "Extra Turn"

/**
 * ViewModel to store and control game logic
 */
class GameViewModel : ViewModel() {

    private var _gameStage = MutableLiveData<GameStage>()
    val gameStage: LiveData<GameStage>
        get() = _gameStage

    private val _score = MutableLiveData<Int>()
    val score: LiveData<Int> = _score

    private val _lives = MutableLiveData<Int>()
    val lives: LiveData<Int> = _lives

    private val _category = MutableLiveData<String>()
    val category: LiveData<String> = _category

    private val _letterCardList = MutableLiveData<MutableList<LetterCard>>()
    val letterCardList: LiveData<MutableList<LetterCard>> = _letterCardList

    private val _wheelResult = MutableLiveData<String>()
    val wheelResult: LiveData<String> = _wheelResult

    private var _guessedCharacters = mutableListOf<Char>()
    val guessedCharacters: List<Char>
        get() = _guessedCharacters

    private val _guessedCharacterString = MutableLiveData("")
    val guessedCharacterString: LiveData<String> = _guessedCharacterString

    private var _gameQuote = MutableLiveData<String>()
    val gameQuote: LiveData<String>
        get() = _gameQuote

    fun setGameQuote(newGameQuote: String) {
        _gameQuote.value = newGameQuote
    }

    private lateinit var _currentWordToBeGuessed: String
    val currentWordToBeGuessed: String
        get() { //Only return currentWordToBeGuessed if the game is over
            return if (gameStage.value == GameStage.GAME_LOST
                || gameStage.value == GameStage.GAME_WON
            ) _currentWordToBeGuessed
            else "It's a secret!"
        }

    val numberOfGuesses: Int
        get() = _guessedCharacters.size

    init {
        Log.d(TAG, "ViewModel initialized")
    }

    /**
     * Used for updating the recyclerview
     *
     * @return a list of the positions of the guessed characters
     */
    fun getPosOfLastGuessedChars(): List<Int> {
        val positions = mutableListOf<Int>()
        for (i in _currentWordToBeGuessed.indices) {
            if (_currentWordToBeGuessed[i].lowercaseChar() == _guessedCharacters.last()
                    .lowercaseChar()
            )
                positions.add(i)
        }
        return positions
    }

    /**
     * A function for checking if the character input matches the currentWordToBeGuessed
     * Sets the gameStage to SPIN.
     * Passes the character input on to savedGuessedChar()
     *
     * If the character input matches check for game won and call doWheelAction()
     * else subtract a life from the player and checkGameLost()
     *
     * @return whether the currentWordToBeGuessed contains the player input
     */
    fun isUserInputMatch(playerInputLetter: Char): Boolean {
        _gameStage.value = GameStage.SPIN
        val playerInputLetterLC = playerInputLetter.lowercaseChar()
        return if (_currentWordToBeGuessed.contains(playerInputLetterLC, ignoreCase = true)
            && !_guessedCharacters.contains(playerInputLetterLC)
        ) {
            saveGuessedChar(playerInputLetterLC)
            _letterCardList.value?.filter {
                it.letter.lowercaseChar() == playerInputLetterLC //Ignore case
            }
                ?.forEach {
                    it.isHidden = false //Reveal letterCard(s)
                }
            if (_letterCardList.value?.all { !it.isHidden || it.letter == ' ' } == true) {
                _gameStage.value = GameStage.GAME_WON
            }
            doWheelResultAction()
            true
        } else {
            saveGuessedChar(playerInputLetterLC)
            _lives.value = _lives.value?.minus(1)
            checkGameLost()
            false
        }
    }

    /**
     * Adds the player input to _guessedCharacters and _guessedCharacterString
     * @param playerInputLetter the player input
     */
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
            //Case when the wheelResult is a digit
            wheelResult.value?.all { it in '0'..'9' } == true -> {
                val wheelValue = wheelResult.value?.toInt()
                if (wheelValue != null) {
                    val occurrences = //Occurrences of the last guessed char in the _currentWordToBeGuessed
                        _currentWordToBeGuessed.filter {
                            it.equals(_guessedCharacters.last(), ignoreCase = true)
                        }.count()
                    _score.value = _score.value?.plus(wheelValue * occurrences)
                }
            }
            wheelResult.value == BANKRUPT -> _score.value = 0
            wheelResult.value == MISS_TURN -> _lives.value = _lives.value?.minus(1)
            wheelResult.value == EXTRA_TURN -> _lives.value = _lives.value?.plus(1)
        }
        checkGameLost()
    }

    /**
     * Sets gameStage to GAME_LOST if players lives is 0 or lower
     */
    private fun checkGameLost() {
        lives.value?.let {
            if (it <= 0) {
                _gameStage.value = GameStage.GAME_LOST
            }
        }
    }

    /**
     * Resets all appropriate values for a fresh game to begin
     */
    fun newGame() {
        Log.d(TAG, "newGame")
        _lives.value = 5
        _score.value = 0
        _guessedCharacters = mutableListOf()
        _guessedCharacterString.value = ""
        _gameStage.value = GameStage.SPIN
    }

    /**
     * Sets the category and the currentWordToBeGuessed from randomCategoryAndWord
     * Creates a LetterCard for each letter and adds this to letterCardList
     *
     * @param randomCategoryAndWord from strings.xml
     */
    fun setCategoryAndCurrentWordToBeGuessed(randomCategoryAndWord: String) {
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

    /**
     * Allow for the result from LuckyWheel to be set in this class
     *
     * Sets GameStage to GUESS if the result is a digit else allow for second spin
     *
     * @param result the result of the LuckyWheel spin
     */
    fun setWheelResult(result: String) {
        _wheelResult.value = result
        _gameStage.value = if (this.wheelResult.value?.isDigitsOnly() == true) {
            GameStage.GUESS
        } else GameStage.SPIN
    }

    /**
     * Setter for gameStage
     *
     * GAME_WON and GAME_LOST is purposely not settable through this function
     *
     * @param stage the stage to be set
     */
    fun setGameStage(stage: GameStage) {
        when (stage) {
            GameStage.SPIN -> _gameStage.value = GameStage.SPIN
            GameStage.GUESS -> _gameStage.value = GameStage.GUESS
            GameStage.WAITING -> _gameStage.value = GameStage.WAITING
        }
    }

    companion object {
        private const val TAG = "GameViewModel"
    }
}