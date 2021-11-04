package com.s205350lykkehjulet

import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.s205350lykkehjulet.models.GameStage
import com.s205350lykkehjulet.models.GameViewModel
import org.junit.Rule

class ViewModelUnitTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    lateinit var viewModelTest: GameViewModel

    @Before
    fun init() {
        viewModelTest = GameViewModel()
        viewModelTest.setCategoryAndCurrentWordToBeGuessed("Test,Doggo",4)
        viewModelTest.newGame()
    }

    /**
     * Test of newGame() method
     */
    @Test
    fun testGameSetup(){
        assertTrue(viewModelTest.lives.value==5)
        assertTrue(viewModelTest.score.value==0)
        assertTrue(viewModelTest.category.value.equals("Test"))
        assertTrue(viewModelTest.numberOfGuesses==0)
        assertTrue(viewModelTest.gameStage.value== GameStage.SPIN)
    }

    /**
     * Test of wrong guess.
     */
    @Test
    fun testWrongGuess(){
        val lives = viewModelTest.lives.value!!
        val guessedLetter = 'K'

        viewModelTest.isUserInputMatch(guessedLetter)

        assertNotNull("Lives is null",lives)
        assertTrue("Lives is not subtracted by 1",viewModelTest.lives.value==4)
        assertTrue("GameStage is not SPIN",viewModelTest.gameStage.value== GameStage.SPIN)
        assertTrue("LetterCard is shown (isHidden==false)",viewModelTest.letterCardList.value!!.all { it.isHidden })
        assertTrue("Guessed char is not set to lowercase",viewModelTest.guessedCharacters.first()==guessedLetter.lowercaseChar())
    }

    /**
     * Test of correct guess
     */
    @Test
    fun testCorrectGuess(){
        val lives = viewModelTest.lives.value!!
        val guessedLetter = 'G'
        viewModelTest.setWheelResult("1000")
        viewModelTest.isUserInputMatch(guessedLetter)

        assertNotNull("Lives is null",lives)
        assertTrue("Lives is subtracted by 1",viewModelTest.lives.value==5)
        assertTrue("GameStage is not SPIN",viewModelTest.gameStage.value== GameStage.SPIN)
        assertTrue("LetterCard is not shown (isHidden==false)", viewModelTest.letterCardList.value!!
                .filter { it.letter==guessedLetter.lowercaseChar() }
                .all { !it.isHidden })
        assertTrue("Guessed char is not set to lowercase",viewModelTest.guessedCharacters.first()==guessedLetter.lowercaseChar())
        assertTrue("Player is not awarded 2*1000 for 2 correct letters", viewModelTest.score.value==2000)
    }

    /**
     * Test of game won
     */
    @Test
    fun testGameWon(){
        val lives = viewModelTest.lives.value!!
        val guessedLetter = 'T'
        viewModelTest.setCategoryAndCurrentWordToBeGuessed("Test,T",5)
        viewModelTest.setWheelResult("1000")

        assertEquals("currentWordToBeGuessed shouldn't be returned before the game is over"
            ,viewModelTest.currentWordToBeGuessed,"It's a secret!")

        viewModelTest.isUserInputMatch(guessedLetter)

        assertNotNull("Lives is null",lives)
        assertTrue("Lives is subtracted by 1",viewModelTest.lives.value==5)
        assertTrue("LetterCard is not shown (isHidden==false)", viewModelTest.letterCardList.value!!
            .filter { it.letter==guessedLetter.lowercaseChar() }
            .all { !it.isHidden })
        assertTrue("Guessed char is not set to lowercase",viewModelTest.guessedCharacters.first()==guessedLetter.lowercaseChar())
        assertTrue("Player is not awarded 1000 points for correct guess", viewModelTest.score.value==1000)
        assertTrue("Game is not won", viewModelTest.gameStage.value== GameStage.GAME_WON)
        assertEquals("currentWordToBeGuessed should be returned since the game is over",viewModelTest.currentWordToBeGuessed,"T")

    }

    /**
     * Test of game lost
     */
    @Test
    fun testGameLost(){
        val guessedLetter = 'K'
        viewModelTest.setCategoryAndCurrentWordToBeGuessed("Test,T",5)
        viewModelTest.setWheelResult("1000")

        assertEquals("currentWordToBeGuessed shouldn't be returned before the game is over"
            ,viewModelTest.currentWordToBeGuessed,"It's a secret!")

        for (i in 1..5) viewModelTest.isUserInputMatch(guessedLetter)

        assertNotNull("Lives is not 0",viewModelTest.lives.value==0)
        assertTrue("GameStage is not GAME_LOST",viewModelTest.gameStage.value== GameStage.GAME_LOST)
        assertTrue("LetterCard is shown (isHidden==false)",viewModelTest.letterCardList.value!!.all { it.isHidden })
        assertTrue("Guessed char is not set to lowercase",viewModelTest.guessedCharacters.first()==guessedLetter.lowercaseChar())
        assertEquals("currentWordToBeGuessed should be returned since the game is over",viewModelTest.currentWordToBeGuessed,"T")
    }

    /**
     * Test of getPosOfLastGuessedChars()
     */
    @Test
    fun testGetPosOfLastGuessedChars(){
        val guessedLetter = 'T'
        viewModelTest.setCategoryAndCurrentWordToBeGuessed("Test,AATATAAATAAAT",5)
        viewModelTest.setWheelResult("1000")
        viewModelTest.isUserInputMatch(guessedLetter)

        assertTrue("The correct index of last guessed character is not returned", viewModelTest.getPosOfLastGuessedChars().equals(
            listOf(2,4,8,12)))
    }

    /**
     * Test of setGameStage()
     * The viewModel.gameStage shouldn't be settable to GAME_WON or GAME_LOST
     */
    @Test
    fun testSetGameStage(){
        assertEquals("The game should start in GameStage.SPIN", viewModelTest.gameStage.value,GameStage.SPIN)
        viewModelTest.setGameStage(GameStage.GUESS)
        assertEquals("The game should GameStage.GUESS", viewModelTest.gameStage.value,GameStage.GUESS)
        viewModelTest.setGameStage(GameStage.SPIN)
        assertEquals("The game should GameStage.SPIN", viewModelTest.gameStage.value,GameStage.SPIN)
        viewModelTest.setGameStage(GameStage.GAME_LOST)
        assertEquals("The game should GameStage.SPIN", viewModelTest.gameStage.value,GameStage.SPIN)
        viewModelTest.setGameStage(GameStage.GAME_WON)
        assertEquals("The game should GameStage.SPIN", viewModelTest.gameStage.value,GameStage.SPIN)
    }

    /**
     * Test of setWheelResult()
     * Setting the wheelResult should set viewModel.gameStage to GUESS if the result is digits.
     * Else the gameStage should be set to SPIN.
     */
    @Test
    fun testSetWheelResult(){
        assertEquals("The game should start in GameStage.SPIN", viewModelTest.gameStage.value,GameStage.SPIN)
        viewModelTest.setWheelResult("1000")
        assertEquals("The gameStage should be GUESS",viewModelTest.gameStage.value,GameStage.GUESS)
        viewModelTest.setWheelResult("Bankrupt")
        assertEquals("The gameStage should be SPIN",viewModelTest.gameStage.value,GameStage.SPIN)
    }

    @Test
    fun testSetCategoryAndCurrentWordToBeGuessed(){

    }
}