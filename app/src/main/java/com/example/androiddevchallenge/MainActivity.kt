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

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.androiddevchallenge.ui.compose.CountDownScreen
import com.example.androiddevchallenge.ui.theme.MyTheme

class MainActivity : AppCompatActivity() {

    private val countDownViewModel by viewModels<CountDownViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createChannel(
            getString(R.string.countdown_timer_notification_channel_id),
            getString(R.string.countdown_timer_notification_channel_name)
        )
        setContent {
            MyTheme {
                MyApp(countDownViewModel)
            }
        }
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
                .apply {
                    setShowBadge(false)
                }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = android.graphics.Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Time for breakfast"

            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}

// Start building your app here!
@Composable
fun MyApp(countDownViewModel: CountDownViewModel) {
    CountDownScreen(
        elapsedTime = countDownViewModel.elapsedTime,
        countDownStarted = countDownViewModel.countDownStarted,
        buttonExpanded = countDownViewModel.buttonExpanded,
        setMin = countDownViewModel::setMin,
        setSec = countDownViewModel::setSec,
        startCountDown = countDownViewModel::startCountDown,
        stopCountDown = countDownViewModel::stopCountDown
    )
}

// @Preview("Light Theme", widthDp = 360, heightDp = 640)
// @Composable
// fun LightPreview() {
//    MyTheme {
//        val countDownViewModel = CountDownViewModel()
//        MyApp(countDownViewModel)
//    }
// }

// @Preview("Dark Theme", widthDp = 360, heightDp = 640)
// @Composable
// fun DarkPreview() {
//    MyTheme(darkTheme = true) {
// //        MyApp()
//    }
// }
