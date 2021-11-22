package com.s205350lykkehjulet.models

/**
 * Class for storing the letter and visibility of a LetterCard
 */
data class LetterCard(
    val letter: Char
) {
    var isHidden = true
}