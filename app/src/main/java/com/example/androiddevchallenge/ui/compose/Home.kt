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
package com.example.androiddevchallenge.ui.compose

import android.graphics.Paint
import android.text.format.DateUtils
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androiddevchallenge.ui.theme.red300
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CountDownScreen(
    elapsedTime: Long,
    countDownStarted: Boolean,
    buttonExpanded: Boolean,
    setMin: (Int) -> Unit,
    setSec: (Int) -> Unit,
    startCountDown: () -> Unit,
    stopCountDown: () -> Unit

) {
    val minDropDownMenuExpanded = remember { mutableStateOf(false) }
    val secDropDownMenuExpanded = remember { mutableStateOf(false) }
    val inputSecEnabled = remember { mutableStateOf(true) }
    val selectedMinIndex = remember { mutableStateOf(0) }
    val selectedSecIndex = remember { mutableStateOf(0) }
    selectedMinIndex.value = (elapsedTime / 60_000).toInt()
    selectedSecIndex.value = ((elapsedTime % 60_000) / 1_000).toInt()

    Surface(color = MaterialTheme.colors.background) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "CountDown Timer!",
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            AnimatedVisibility(!countDownStarted) {
                Row {
                    Spacer(modifier = Modifier.weight(0.3f))
                    Box(
                        modifier = Modifier
                            .weight(0.2f)
                            .clickable(onClick = { minDropDownMenuExpanded.value = true })
                            .background(MaterialTheme.colors.primary)
                    ) {
                        Text(
                            text = (0..60).toList()[selectedMinIndex.value].toString(),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.Center)
                        )
                        DropdownMenu(
                            expanded = minDropDownMenuExpanded.value,
                            onDismissRequest = { minDropDownMenuExpanded.value = false },
                            modifier = Modifier.background(MaterialTheme.colors.primaryVariant)
                        ) {
                            for (i in 0..60) {
                                DropdownMenuItem(
                                    onClick = {
                                        selectedMinIndex.value = i
                                        minDropDownMenuExpanded.value = false
                                        setMin(i)
                                        // 60 minutes is the last limited value for countdown time
                                        if (i == 60) {
//                                        setSec(0)
                                            selectedSecIndex.value = 0
                                            inputSecEnabled.value = false
                                        } else {
                                            inputSecEnabled.value = true
                                        }
                                    }
                                ) {
                                    DropDownMenuTextAndDivider(i)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Box(
                        modifier = Modifier
                            .weight(0.2f)
                            .clickable(
                                onClick = {
                                    if (inputSecEnabled.value) {
                                        secDropDownMenuExpanded.value = true
                                    }
                                }
                            )
                            .background(MaterialTheme.colors.primary)
                    ) {
                        Text(
                            text = (0..60).toList()[selectedSecIndex.value].toString(),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.Center)
                        )
                        DropdownMenu(
                            expanded = secDropDownMenuExpanded.value && inputSecEnabled.value,
                            onDismissRequest = { secDropDownMenuExpanded.value = false },
                            modifier = Modifier.background(MaterialTheme.colors.primaryVariant)
                        ) {
                            for (i in 0..60) {
                                DropdownMenuItem(
                                    onClick = {
                                        selectedSecIndex.value = i
                                        secDropDownMenuExpanded.value = false
                                        setSec(i)
                                    }
                                ) {
                                    DropDownMenuTextAndDivider(i)
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.weight(0.3f))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            AnimatedVisibility(buttonExpanded) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            if (!countDownStarted) {
                                setMin(selectedMinIndex.value)
                                setSec(selectedSecIndex.value)
                                startCountDown()
                            } else {
                                stopCountDown()
                            }
                        }
                    ) {
                        Text(if (!countDownStarted) "Start" else "Stop")
                    }
                }
            }
            Spacer(modifier = Modifier.height(30.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = getCurrentCountDownSec(elapsedTime),
                    fontSize = 32.sp
                )
                CounterClockCanvas(elapsedTime)
            }
        }
    }
}

@Composable
fun DropDownMenuTextAndDivider(i: Int) {
    Column {
        Text(
            text = i.toString(),
            fontSize = 10.sp
        )
        if (i != 0 && i % 10 == 0) {
            Divider(
                color = MaterialTheme.colors.secondary,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun CounterClockCanvas(elapsedTime: Long) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val canvasWidth = size.width
//        val canvasHeight = size.height

        val arcWidth = canvasWidth / 1.5f
        val arcCenter = arcWidth / 3.5f
        val curSec = elapsedTime / 1000
        drawArc(
            startAngle = 270f,
            sweepAngle = curSec * -0.1f,
            useCenter = true,
            color = red300,
            size = Size(width = arcWidth, arcWidth),
            topLeft = Offset(arcCenter, arcCenter)
        )

        val lineHeight = size.minDimension / 32
//        val halfLineHeight = lineHeight/2
        val radius = arcWidth / 2

        val zeroCoordinate = arcCenter + radius
//        val offsetList = mutableListOf<Offset>()

        val paint = Paint()
        paint.textSize = 48f
        paint.color = 0xff000000.toInt()
        val timeList = listOf<Int>(40, 35, 30, 25, 20, 15, 10, 5, 0, 55, 50, 45)
        val addOffsetList = listOf<Pair<Int, Int>>(
            Pair(5, 30), Pair(-20, 45), Pair(-20, 45),
            Pair(-40, 50), Pair(-40, 40), Pair(-55, 20), Pair(-60, 10), Pair(-20, -10), Pair(-10, -5), Pair(0, 0), Pair(0, 0), Pair(0, 20)
        )

        for (i in 1..12) {
            val nextX = (cos(Math.toRadians(i * 30.0)) * radius).toFloat()
            val nextY = (sin(Math.toRadians(i * 30.0)) * radius).toFloat()
            val x = zeroCoordinate + nextX
            val y = zeroCoordinate + nextY

            var path = Path()
            path.moveTo(zeroCoordinate, zeroCoordinate)
            path.lineTo(x, y)

            drawPath(
                path = path,
                color = Color.Black,
                style = Stroke(1f)
            )

            drawIntoCanvas {
                val pair = addOffsetList[i - 1]
                val offsetX = x + pair.first
                val offsetY = y + pair.second

                it.nativeCanvas.drawText("${timeList[i - 1]}", offsetX, offsetY, paint)
            }
        }

//                        drawPoints(
//                            points = offsetList,
//                            pointMode = PointMode.Points,
//                            cap = StrokeCap.Round,
//                            strokeWidth = 15f,
//                            color = Color.Black
//                        )

        val circleCenter = arcCenter + arcWidth / 2
        drawCircle(
            color = Color.Black,
            center = Offset(x = circleCenter, y = circleCenter),
            radius = lineHeight
        )

//        val textPaint = android.graphics.Paint()
//        textPaint.textSize = 64f
//        textPaint.color = 0xff000000.toInt()
//        drawIntoCanvas {
//            it.nativeCanvas.drawText(currentCountDownTime, arcCenter + radius / 1.5f, arcCenter + radius * 2 + radius / 2, textPaint)
//        }
    }
}

fun getCurrentCountDownSec(value: Long): String {
    val seconds = value / 1000
    return if (seconds < 60) seconds.toString() else DateUtils.formatElapsedTime(seconds)
}
