package com.example.s205350lykkehjulet

import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ViewModelUnitTest {
    lateinit var viewModel: GameViewModel
    @Before
    fun init() {
        viewModel = GameViewModel()
    }

    @Test
    fun loseLifeTest(){
        val lives = viewModel.lives
        val char: Char = 'ë'
        viewModel.isUserInputMatch((char))
        assertTrue(viewModel.lives==lives-1)
    }



}