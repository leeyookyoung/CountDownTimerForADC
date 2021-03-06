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

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androiddevchallenge.ui.theme.MyTheme
import com.example.androiddevchallenge.ui.theme.red300
import kotlin.math.cos
import kotlin.math.sin

class MainActivity : AppCompatActivity() {

    private val countDownViewModel by viewModels<CountDownViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                MyApp(countDownViewModel)
            }
        }
    }
}

// Start building your app here!
@Composable
fun MyApp(countDownViewModel: CountDownViewModel) {
    CountDownScreen(
        countDownMin = countDownViewModel.countDownMin,
        countDownSec = countDownViewModel.countDownSec,
        countDownStarted = countDownViewModel.countDownStarted,
        buttonExpanded = countDownViewModel.buttonExpanded,
        wrongNumberExpanded = countDownViewModel.wrongNumberExpanded,
        setWrongNumber = countDownViewModel::setWrongNumber,
        validationCheck = countDownViewModel::validateNumCheck,
        onMinuteAndSecChanged = countDownViewModel::onMinuteAndSecChanged,
        startCountDown = countDownViewModel::startCountDown,
        stopCountDown = countDownViewModel::stopCountDown
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CountDownScreen(
    countDownMin: Int,
    countDownSec: Int,
    countDownStarted: Boolean,
    buttonExpanded: Boolean,
    wrongNumberExpanded: Boolean,
    setWrongNumber: () -> Unit,
    validationCheck: (String, String) -> Unit,
    onMinuteAndSecChanged: (Int, Int) -> Unit,
    startCountDown: () -> Unit,
    stopCountDown: () -> Unit

) {
    var countDownMinStr = remember { mutableStateOf("0") }
    var countDownSecStr = remember { mutableStateOf("0") }
    fun check(str1: String, str2: String) {
        if (!str1.isEmpty()) {
            validationCheck(str1, str2)
        } else {
            setWrongNumber()
        }
    }
    Surface(color = MaterialTheme.colors.background) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "CountDown Timer!",
                fontSize = 32.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            AnimatedVisibility(!countDownStarted) {
                Row {
                    OutlinedTextField(
                        value = countDownMinStr.value,
                        onValueChange = {
                            countDownMinStr.value = it
                            check(it, countDownSecStr.value)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        label = { Text("Input Minutes") },
                        singleLine = true,
                        maxLines = 1,
                        modifier = Modifier.weight(0.5f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = countDownSecStr.value,
                        onValueChange = {
                            countDownSecStr.value = it
                            check(countDownMinStr.value, it)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        label = { Text("Input Seconds") },
                        singleLine = true,
                        maxLines = 1,
                        modifier = Modifier.weight(0.5f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            AnimatedVisibility(buttonExpanded) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = {
                            if (!countDownStarted) {
                                onMinuteAndSecChanged(
                                    countDownMinStr.value.toInt(),
                                    countDownSecStr.value.toInt()
                                )
                                startCountDown()
                            } else {
                                stopCountDown()
                            }
                        }
                    ) {
                        Text("${if (!countDownStarted) "Start" else "Stop"}")
                    }
                }
            }
            AnimatedVisibility(wrongNumberExpanded) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Wrong Minute or Second\nPlease input 60 below and only numbers",
                        color = Color.Red,
                        fontSize = 16.sp,
                    )
                }
            }
            Spacer(modifier = Modifier.height(50.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CounterClockCanvas(getCurrentCountDownSec(countDownMin, countDownSec), countDownMin, countDownSec)
//                    Text(
//                        text = "${getCurrentCountDownSec(countDownMin, countDownSec)}",
//                        fontSize = 32.sp,
//                    )
            }
        }
    }
}

@Composable
fun CounterClockCanvas(currentCountDownTime: String, min: Int, sec: Int) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val canvasWidth = size.width
//        val canvasHeight = size.height

        val arcWidth = canvasWidth / 1.5f
        val arcCenter = arcWidth / 3.5f
        val curSec = min * 60 + sec
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

        val paint = android.graphics.Paint()
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

        val textPaint = android.graphics.Paint()
        textPaint.textSize = 64f
        textPaint.color = 0xff000000.toInt()
        drawIntoCanvas {
            it.nativeCanvas.drawText(currentCountDownTime, arcCenter + radius / 1.5f, arcCenter + radius * 2 + radius / 2, textPaint)
        }
    }
}

fun getCurrentCountDownSec(min: Int, sec: Int): String {
    var cStr = if (min!! <= 9) "0$min" else min.toString()
    cStr += " : "
    cStr += if (sec!! <= 9) "0$sec" else sec.toString()
    return cStr
}

@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun LightPreview() {
    MyTheme {
        val countDownViewModel = CountDownViewModel()
        MyApp(countDownViewModel)
    }
}

// @Preview("Dark Theme", widthDp = 360, heightDp = 640)
// @Composable
// fun DarkPreview() {
//    MyTheme(darkTheme = true) {
// //        MyApp()
//    }
// }
