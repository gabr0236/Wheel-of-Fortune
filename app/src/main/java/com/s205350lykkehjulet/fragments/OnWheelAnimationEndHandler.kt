package com.s205350lykkehjulet.fragments

/**
 * Interface to limit coupling between LuckyWheel and GameFragment,
 *  since LuckyWheel would otherwise need GameFragment as reference
 *  to pass on wheel result in Animation.onAnimationEnd() to GameFragment.
 *
 *  With GameFragment implementing this interface it only needs to pass
 *   itself onto LuckyWheel as a OnWheelAnimationEndHandler.
 */
interface OnWheelAnimationEndHandler {
    fun onWheelAnimationEnd(wheelResult: String)
}