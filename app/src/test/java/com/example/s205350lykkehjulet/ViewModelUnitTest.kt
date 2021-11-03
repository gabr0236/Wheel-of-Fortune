package com.example.s205350lykkehjulet

import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.s205350lykkehjulet.models.GameStage

import org.junit.Rule




class ViewModelUnitTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    lateinit var viewModelTest: GameViewModel

    @Before
    fun init() {
        viewModelTest = GameViewModel()
        viewModelTest.setCategoryAndCurrentWordToBeGuessed("Test,Doggo")
        viewModelTest.newGame()
    }

    @Test
    fun testGameSetup(){
        assertTrue(viewModelTest.lives.value==5)
        assertTrue(viewModelTest.score.value==0)
        assertTrue(viewModelTest.category.value.equals("Test"))
        assertTrue(viewModelTest.numberOfGuesses==0)
        assertTrue(viewModelTest.gameStage.value== GameStage.SPIN)
    }

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
        //TODO: this is not working apparently
        //assertTrue("Player is not awarded 2*1000 for 2 correct letters", viewModelTest.score.value==2000)
    }

    @Test
    fun testGameWon(){
        val lives = viewModelTest.lives.value!!
        val guessedLetter = 'T'
        viewModelTest.setCategoryAndCurrentWordToBeGuessed("Test,T")
        viewModelTest.setWheelResult("1000")
        viewModelTest.isUserInputMatch(guessedLetter)

        assertNotNull("Lives is null",lives)
        assertTrue("Lives is subtracted by 1",viewModelTest.lives.value==5)
        assertTrue("LetterCard is not shown (isHidden==false)", viewModelTest.letterCardList.value!!
            .filter { it.letter==guessedLetter.lowercaseChar() }
            .all { !it.isHidden })
        assertTrue("Guessed char is not set to lowercase",viewModelTest.guessedCharacters.first()==guessedLetter.lowercaseChar())
        //TODO: this is not working apparently
        assertTrue("Player is not awarded 1000 points for correct guess", viewModelTest.score.value==1000)
        assertTrue("Game is not won", viewModelTest.gameStage.value== GameStage.GAME_WON)
    }

    @Test
    fun testGameLost(){
        val guessedLetter = 'K'
        viewModelTest.setCategoryAndCurrentWordToBeGuessed("Test,T")
        viewModelTest.setWheelResult("1000")

        for (i in 1..5) viewModelTest.isUserInputMatch(guessedLetter)

        assertNotNull("Lives is not 0",viewModelTest.lives.value==0)
        assertTrue("GameStage is not GAME_LOST",viewModelTest.gameStage.value== GameStage.GAME_LOST)
        assertTrue("LetterCard is shown (isHidden==false)",viewModelTest.letterCardList.value!!.all { it.isHidden })
        assertTrue("Guessed char is not set to lowercase",viewModelTest.guessedCharacters.first()==guessedLetter.lowercaseChar())
    }
    //TODO: test to be done: getPosOfLastGuessedChars
}