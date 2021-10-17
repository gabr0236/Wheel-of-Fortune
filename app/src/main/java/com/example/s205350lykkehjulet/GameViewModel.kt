package com.example.s205350lykkehjulet

import android.text.Spannable
import android.text.SpannableString
import android.text.style.TtsSpan
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {

    private val _score = MutableLiveData(0)
    val score: LiveData<Int>
        get() = _score

    private val _lives = MutableLiveData(0)
    val lives: LiveData<Int>
        get() = _lives

    private val _currentWordToBeGuessed = MutableLiveData<String>()

}