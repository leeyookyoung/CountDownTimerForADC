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

import android.app.AlarmManager
import android.app.Application
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.CountDownTimer
import android.os.SystemClock
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.AlarmManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.androiddevchallenge.notification.receiver.AlarmReceiver
import com.example.androiddevchallenge.util.cancelNotifications
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CountDownViewModel(private val app: Application) : AndroidViewModel(app) {

    private val REQUEST_CODE = 0
    private val TRIGGER_TIME = "TRIGGER_AT"

    private val minute: Long = 60_000L
    private val second: Long = 1_000L

    private lateinit var countDown: CountDownTimer
    var countDownStarted by mutableStateOf(false)
    var buttonExpanded by mutableStateOf(false)
    var elapsedTime by mutableStateOf(0L)

    private var _min: Int = 0
    private var _sec: Int = 0

    private val notifyPendingIntent: PendingIntent

    private val alarmManager = app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private var prefs =
        app.getSharedPreferences("com.example.androiddevchallenge", Context.MODE_PRIVATE)
    private val notifyIntent = Intent(app, AlarmReceiver::class.java)

    init {
        val isAlarmOn = PendingIntent.getBroadcast(
            getApplication(),
            REQUEST_CODE,
            notifyIntent,
            PendingIntent.FLAG_NO_CREATE
        ) != null

        notifyPendingIntent = PendingIntent.getBroadcast(
            getApplication(),
            REQUEST_CODE,
            notifyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        if (isAlarmOn) {
            viewModelScope.launch {
                val triggerTime = loadTime()
                if (triggerTime - SystemClock.elapsedRealtime() > 0) {
                    countDownStarted = true
                    buttonExpanded = true
                    createCountDownTimer()
                }
            }
        }
    }

    fun setMin(min: Int) {
        _min = min
        resetElapsedTime()
        setButtonExpanded()
    }

    fun setSec(sec: Int) {
        _sec = sec
        resetElapsedTime()
        setButtonExpanded()
    }

    private fun resetElapsedTime() {
        elapsedTime = _min * minute + _sec * second
    }

    private fun setButtonExpanded() {
        buttonExpanded = _sec > 0 || _min > 0
    }

    fun startCountDown() {
        countDownStarted = true

        // set notification cancel and call alarm
        val notificationManager =
            ContextCompat.getSystemService(
                app,
                NotificationManager::class.java
            ) as NotificationManager
        notificationManager.cancelNotifications()

        val triggerTime = SystemClock.elapsedRealtime() + elapsedTime
        AlarmManagerCompat.setExactAndAllowWhileIdle(
            alarmManager,
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            triggerTime,
            notifyPendingIntent
        )

        viewModelScope.launch {
            saveTime(triggerTime)
        }
        createCountDownTimer()
    }
    private fun createCountDownTimer() {
        viewModelScope.launch {
            val triggerTime = loadTime()
            countDown = object : CountDownTimer(triggerTime, second) {
                override fun onTick(millisUntilFinished: Long) {
                    elapsedTime = triggerTime - SystemClock.elapsedRealtime()
                    _min = (elapsedTime / minute).toInt()
                    _sec = ((elapsedTime % minute) / second).toInt()
                    if (elapsedTime <= 0) {
                        cancelCountDown()
                    }
                }
                override fun onFinish() {
                    countDownStarted = false
                }
            }
            countDown.start()
        }
    }

    fun stopCountDown() {
        alarmManager.cancel(notifyPendingIntent)
        cancelCountDown()
    }

    private fun cancelCountDown() {
        setButtonExpanded()
        countDown.onFinish()
        countDown.cancel()
    }

    private suspend fun saveTime(triggerTime: Long) =
        withContext(Dispatchers.IO) {
            prefs.edit().putLong(TRIGGER_TIME, triggerTime).apply()
        }

    private suspend fun loadTime(): Long =
        withContext(Dispatchers.IO) {
            prefs.getLong(TRIGGER_TIME, 0)
        }
}
