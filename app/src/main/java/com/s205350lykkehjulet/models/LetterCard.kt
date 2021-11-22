package com.s205350lykkehjulet.models

/**
 * Dataclass for storing the letter and visibility of a LetterCard
 */
data class LetterCard(
    val letter: Char
) {
    var isHidden = true
}