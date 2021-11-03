package com.example.s205350lykkehjulet

import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageView
import com.example.s205350lykkehjulet.fragments.GameFragment
import kotlin.random.Random

/**
 * Responsible for spinning the luckyWheel Image and returning the appropriate random wheelResult
 *
 * @property luckyWheelImage the image to be spun
 * @property gameFragment the current fragment
 */
class LuckyWheel(
    private val luckyWheelImage: ImageView,
    private val gameFragment: GameFragment
) {
    private var degree = 0

    /**
     * Responsible for creating animation for rotating the luckyWheelImage
     */
    fun spinWheelImage() {
        val degreeOld = degree % 360

        //Random angle for rotation of our wheel + amount of wheel round trips in degrees
        degree = Random.nextInt(360) + 1080

        //Rotation effect on the center of the wheel
        val rotateAnimation = RotateAnimation(
            degreeOld.toFloat(), degree.toFloat(),
            RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f
        )
        rotateAnimation.duration = 3600 //Duration
        rotateAnimation.fillAfter = true //Don't reset wheel after spin
        rotateAnimation.interpolator = DecelerateInterpolator()//Decelerate spin speed at end of spin

        rotateAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationRepeat(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                //Pass the random wheel sector value on to the GameFragment
                getSector(360 - degree % 360)?.let { gameFragment.continueGameAfterWheelSpin(it) }
            }
        })
        //Start the animation
        luckyWheelImage.startAnimation(rotateAnimation)
    }

    /**
     * Gets the WHEEL_SECTORS value at the degree of the wheel spin
     *
     * @param degrees the degree of the wheel
     * @return the corresponding value of the degree
     */
    private fun getSector(degrees: Int): String? {
        var i = 0
        var result: String? = null
        do {
            // start and end of each sector on the wheel
            val start = WHEEL_SECTOR_SIZE * i
            val end = WHEEL_SECTOR_SIZE * (i + 1)
            if (degrees >= start && degrees < end) {
                // degrees is in [start;end[
                // so result is equals to sectors[i];
                result = WHEEL_SECTORS[i]
            }
            i++
        } while (result == null && i < WHEEL_SECTORS.size)
        return result
    }

    companion object {
        //There are 22 sectors on the wheel, divide 360 by this value to have degree for each sector
        private const val WHEEL_SECTOR_SIZE = 360f / 22f

        //Wheel values
        private val WHEEL_SECTORS = arrayOf(
            "Miss Turn", "600", "500", "800", "500",
            "Bankrupt", "1500", "800", "100", "500",
            "600", "500", "Extra Turn", "800", "500",
            "800", "1000", "100", "300", "800", "1000",
            "500"
        )
    }
}