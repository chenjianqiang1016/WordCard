package com.demo.wordcard.util

import android.animation.ValueAnimator
import android.widget.ImageView
import com.demo.wordcard.R

/**
 * 声音播放工具
 */
class VoicePlayAnimUtil {

    companion object {

        fun playVoice(voiceImg: ImageView) {
            val toValue = 3.0f
            val fromValue = 0.0f
            ValueAnimator.ofFloat(fromValue, toValue).apply {
                duration = 500
                repeatCount = 1
                addUpdateListener {
                    var currentValue = it.animatedValue as Float
                    if (currentValue >= 0 && currentValue < 1f) {
                        voiceImg.setImageResource(R.mipmap.ic_voice_0)
                    } else if (currentValue >= 1f && currentValue < 2f) {
                        voiceImg.setImageResource(R.mipmap.ic_voice_1)
                    } else {
                        voiceImg.setImageResource(R.mipmap.ic_voice_2)
                    }
                }
            }.start()

        }

    }


}