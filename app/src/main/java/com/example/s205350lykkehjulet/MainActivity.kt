package com.example.s205350lykkehjulet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val gameFragment = GameFragment()
        val winningScreen = WinningScreen()
        val losingScreen = LosingScreen()

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.gameFragment, gameFragment)
            commit()
        }

    }
}