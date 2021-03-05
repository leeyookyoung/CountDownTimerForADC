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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androiddevchallenge.ui.theme.MyTheme

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
    validationCheck: (String, String) -> Unit,
    onMinuteAndSecChanged: (Int, Int) -> Unit,
    startCountDown: () -> Unit,
    stopCountDown: () -> Unit

) {
    var countDownMinStr = remember { mutableStateOf("0") }
    var countDownSecStr = remember { mutableStateOf("0") }
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
                            if (!it.isEmpty()) {
                                validationCheck(it, countDownSecStr.value)
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        label = { Text("Input Minutes") },
                        singleLine = true,
                        maxLines = 1,
                        modifier = Modifier.weight(0.5f)
                    )
                    OutlinedTextField(
                        value = countDownSecStr.value,
                        onValueChange = {
                            countDownSecStr.value = it
                            if (!it.isEmpty()) {
                                validationCheck(countDownMinStr.value, it)
                            }
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
            Spacer(modifier = Modifier.height(200.dp))
            AnimatedVisibility(countDownStarted) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${getCurrentCountDownSec(countDownMin, countDownSec)}",
                        fontSize = 32.sp,
                    )
                }
            }

            AnimatedVisibility(wrongNumberExpanded) {
                Text(
                    text = "Wrong Minute or Second numbers",
                    color = Color.Red,
                    fontSize = 16.sp,
                )
            }
        }
    }
}

fun getCurrentCountDownSec(min: Int, sec: Int): String {
    var cStr = if (min!! <= 9) "0$min" else min.toString()
    cStr += " : "
    cStr += if (sec!! <= 9) "0$sec" else sec.toString()
    return cStr
}

fun numberCheck(min: String, sec: String): Boolean {
    try {
        min.toInt()
        sec.toInt()
        return true
    } catch (e: NumberFormatException) {
        return false
    }
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
