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
    private var degreeOld = 0

    private val wheelSectors = arrayOf(
        "Miss Turn", "600", "500", "800", "500",
        "Bankrupt", "1500", "800", "100", "500",
        "600", "500", "Extra Turn", "800", "500",
        "800", "1000", "100", "300", "800", "1000",
        "500"
    )

    fun spinWheelImage() {
        degreeOld = degree % 360
        // we calculate random angle for rotation of our wheel
        degree = Random.nextInt(360) + 720
        // rotation effect on the center of the wheel
        val rotateAnim = RotateAnimation(
            degreeOld.toFloat(), degree.toFloat(),
            RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f
        )
        rotateAnim.duration = 3600

        //Dont reset wheel after spin
        rotateAnim.fillAfter = true

        //Decelerate spin speed at end of spin
        rotateAnim.interpolator = DecelerateInterpolator()
        rotateAnim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
            }

            override fun onAnimationEnd(animation: Animation) {
                // we display the correct sector pointed by the triangle at the end of the rotate animation
                getSector(360 - degree % 360)?.let { gameFragment.continueGameAfterWheelSpin(it) }
            }
            override fun onAnimationRepeat(animation: Animation) {}
        })
        // we start the animation
        luckyWheelImage.startAnimation(rotateAnim)
    }

    private fun getSector(degrees: Int): String? {
        var i = 0
        var text: String? = null
        do {
            // start and end of each sector on the wheel
            val start = WHEEL_SECTOR_DEGREES * i
            val end = WHEEL_SECTOR_DEGREES * (i + 1)
            i++
            if (degrees >= start && degrees < end) {
                // degrees is in [start;end[
                // so text is equals to sectors[i];
                text = wheelSectors[i]
            }
        } while (text == null && i < wheelSectors.size)
        return text
    }


    companion object {
        // We have 22 sectors on the wheel, we divide 360 by this value to have angle for each sector
        private const val WHEEL_SECTOR_DEGREES = 360f / 22f
    }
}