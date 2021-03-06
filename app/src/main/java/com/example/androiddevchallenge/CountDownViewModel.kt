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
    var wrongNumberExpanded by mutableStateOf(false)
    var countDownMin by mutableStateOf(0)
    var countDownSec by mutableStateOf(0)

    fun numberCheck(min: String, sec: String): Boolean {
        try {
            val curMin = min.toInt()
            val curSec = sec.toInt()

            if (curMin > 60) {
                return false
            }

            if (curSec > 60) {
                return false
            }

            countDownMin = curMin
            countDownSec = curSec
            return true
        } catch (e: NumberFormatException) {
            return false
        }
    }

    fun validateNumCheck(min: String, sec: String) {
        if (numberCheck(min, sec)) {
            buttonExpanded = true
            wrongNumberExpanded = false
        } else {
            wrongNumberExpanded = true
            buttonExpanded = false
        }
    }

    fun setWrongNumber() {
        wrongNumberExpanded = true
        buttonExpanded = false
    }

    fun onMinuteAndSecChanged(newMin: Int, newSec: Int) {
        countDownMin = newMin
        countDownSec = newSec + 1
    }

    fun startCountDown() {
        countDownStarted = true

        var totalSeconds = (countDownMin * 60000)
        if (totalSeconds != null) {
            totalSeconds += (countDownSec * 1000)
        }
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
