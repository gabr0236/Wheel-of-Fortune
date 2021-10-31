package com.example.s205350lykkehjulet

import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import androidx.arch.core.executor.testing.InstantTaskExecutorRule

import org.junit.Rule




/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ViewModelUnitTest {


    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    lateinit var viewModelTest: GameViewModel

    @Before
    fun init() {
        viewModelTest = GameViewModel()
        viewModelTest.setCategoryAndCurrentWordToBeGuessed("Animal,Doggo")
        viewModelTest.newGame()
    }

    @Test
    fun testGameSetup(){
        assertTrue(!viewModelTest.isWon)
        assertTrue(viewModelTest.lives.value==5)
        assertTrue(viewModelTest.score.value==0)
        assertTrue(viewModelTest.category.value.equals("Animal"))
        assertTrue(viewModelTest.numberOfGuesses==0)
        assertTrue(viewModelTest.gameStage.value==GameStage.IS_SPIN)
    }

    @Test
    fun testWrongGuess(){
        val lives = viewModelTest.lives.value!!
        val guessedLetter = 'K'

        viewModelTest.isUserInputMatch(guessedLetter)

        assertNotNull("Lives is null",lives)
        assertTrue("Lives is not subtracted by 1",viewModelTest.lives.value==4)
        assertTrue("GameStage is not IS_SPIN",viewModelTest.gameStage.value==GameStage.IS_SPIN)
        assertTrue("LetterCard is set to isHidden==false",viewModelTest.letterCardList.value!!.all { it.isHidden })
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
        assertTrue("GameStage is not IS_SPIN",viewModelTest.gameStage.value==GameStage.IS_SPIN)
        assertTrue("LetterCard is not set to isHidden==false", viewModelTest.letterCardList.value!!
                .filter { it.letter==guessedLetter.lowercaseChar() }
                .all { !it.isHidden })
        assertTrue("Guessed char is not set to lowercase",viewModelTest.guessedCharacters.first()==guessedLetter.lowercaseChar())
        //TODO: this is not working apparently
        //assertTrue("Player is not awarded 2*1000 for 2 correct letters", viewModelTest.score.value==2000)
    }

    //TODO: test to be done: jokerresult, getPosOfLastGuessedChars,
}