package com.example.littlepainter.utils

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun delayTask(time:Long,action: ()->Unit){
    CoroutineScope(Dispatchers.IO).launch {
        delay(time)
        withContext(Dispatchers.Main){
            action()
        }
    }
}