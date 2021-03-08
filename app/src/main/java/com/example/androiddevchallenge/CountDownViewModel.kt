/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import android.os.CountDownTimer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class CountDownViewModel : ViewModel() {
    private lateinit var countDown: CountDownTimer
    var countDownStarted by mutableStateOf(false)
    var buttonExpanded by mutableStateOf(false)
    var countDownMin by mutableStateOf(0)
    var countDownSec by mutableStateOf(0)

    private fun numberCheck(sec: String): Boolean {
        try {
            val curSec = sec.toInt()

            if (curSec > 60) {
                return false
            }

            countDownSec = curSec
            return true
        } catch (e: NumberFormatException) {
            return false
        }
    }

    fun setMin(min: Int) {
        countDownMin = min
        setButtonExpanded()
    }

    fun setSec(sec: Int) {
        countDownSec = sec
        setButtonExpanded()
    }

    private fun setButtonExpanded() {
        buttonExpanded = countDownMin > 0 || countDownSec > 0
    }

    fun startCountDown() {
        countDownStarted = true
        countDownSec += 1

        var totalSeconds = (countDownMin * 60000)
        totalSeconds += (countDownSec * 1000)

        countDown = object : CountDownTimer(totalSeconds!!.toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                if (countDownSec == 0 && countDownMin > 0) {
                    countDownMin--
                    countDownSec = 60
                }

                countDownSec--
            }
            override fun onFinish() {
                countDownStarted = false
            }
        }
        countDown.start()
    }
    fun stopCountDown() {
        countDown.onFinish()
        countDown.cancel()
    }
}
