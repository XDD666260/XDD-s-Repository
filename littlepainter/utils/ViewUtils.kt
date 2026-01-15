package com.example.littlepainter.utils

import android.content.Context
import android.content.res.Resources
import androidx.core.view.ViewCompat

object ViewUtils {
    fun dp2px(dp:Int):Int{
        return (dp* Resources.getSystem().displayMetrics.density).toInt()
    }
    fun dp2pxF(dp:Int):Float{
        return (dp* Resources.getSystem().displayMetrics.density).toFloat()
    }

}