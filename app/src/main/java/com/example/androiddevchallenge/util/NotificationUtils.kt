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
package com.example.androiddevchallenge.util

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import com.example.androiddevchallenge.MainActivity
import com.example.androiddevchallenge.R

private val NOTIFICATION_ID = 0

fun NotificationManager.sendNotification(messageBody: String, applicationContext: Context) {
    val contentIntent = Intent(applicationContext, MainActivity::class.java)

    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_CANCEL_CURRENT
    )

    val countDownTimerImage = BitmapFactory.decodeResource(
        applicationContext.resources,
        R.drawable.countdown_timer
    )
    val bigPicStyle = NotificationCompat.BigPictureStyle()
        .bigPicture(countDownTimerImage)
        .bigLargeIcon(null)

    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.countdown_timer_notification_channel_id)
    )
        .setSmallIcon(R.drawable.countdown_timer)
        .setContentTitle(applicationContext.getString(R.string.notification_title))
        .setContentText(messageBody)
        .setContentIntent(contentPendingIntent)
        .setAutoCancel(true)
        .setStyle(bigPicStyle)
        .setLargeIcon(countDownTimerImage)
        .setPriority(NotificationCompat.PRIORITY_HIGH)

    notify(NOTIFICATION_ID, builder.build())
}

fun NotificationManager.cancelNotifications() {
    cancelAll()
}
