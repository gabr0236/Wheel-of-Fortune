package com.example.s205350lykkehjulet

import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageView
import kotlin.random.Random

class LuckyWheel(
    private val luckyWheelImage: ImageView,
    private val gameFragment: GameFragment
) {
    private var degree = 0

    fun spinWheelImage() {
        val degreeOld = degree % 360

        //calculate random angle for rotation of our wheel + amount of wheel round trips
        degree = Random.nextInt(360) + 1080

        //Rotation effect on the center of the wheel
        val rotateAnim = RotateAnimation(
            degreeOld.toFloat(), degree.toFloat(),
            RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f
        )
        rotateAnim.duration = 3600

        //Don't reset wheel after spin
        rotateAnim.fillAfter = true

        //Decelerate spin speed at end of spin
        rotateAnim.interpolator = DecelerateInterpolator()
        rotateAnim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
            }

            override fun onAnimationEnd(animation: Animation) {
                //Pass the random wheel sector value on to the GameFragment
                getSector(360 - degree % 360)?.let { gameFragment.continueGameAfterWheelSpin(it) }
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        //Start the animation
        luckyWheelImage.startAnimation(rotateAnim)
    }

    private fun getSector(degrees: Int): String? {
        var i = 0
        var text: String? = null
        do {
            // start and end of each sector on the wheel
            val start = WHEEL_SECTOR_SIZE * i
            val end = WHEEL_SECTOR_SIZE * (i + 1)
            if (degrees >= start && degrees < end) {
                // degrees is in [start;end[
                // so text is equals to sectors[i];
                text = WHEEL_SECTORS[i]
            }
            i++
        } while (text == null && i < WHEEL_SECTORS.size)
        return text
    }


    companion object {
        // We have 22 sectors on the wheel, we divide 360 by this value to have angle for each sector
        private const val WHEEL_SECTOR_SIZE = 360f / 22f
        private val WHEEL_SECTORS = arrayOf(
            "Miss Turn", "600", "500", "800", "500",
            "Bankrupt", "1500", "800", "100", "500",
            "600", "500", "Extra Turn", "800", "500",
            "800", "1000", "100", "300", "800", "1000",
            "500"
        )
    }
}