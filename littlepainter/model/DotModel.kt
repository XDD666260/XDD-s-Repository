package com.example.littlepainter.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.RectF
import androidx.annotation.DrawableRes
import com.example.littlepainter.R
import java.lang.ref.WeakReference

data class DotModel(
    val num:Int,
    val cx:Float,
    val cy:Float,
    val radius:Float,
    val context: WeakReference<Context>,
    var state: DotState = DotState.NORMAL,
    @DrawableRes val normalRes:Int= R.drawable.dot_normal_selected,
    @DrawableRes val errorRes:Int=R.drawable.dot_error_selected

){
    val normalBitmap:Bitmap by lazy {
        BitmapFactory.decodeResource(
            context.get()?.resources,
            normalRes
        )
    }
    val errorBitmap:Bitmap by lazy {
        BitmapFactory.decodeResource(
            context.get()?.resources,
            errorRes
        )
    }
    val recF=RectF(cx-radius,cy-radius,cx+radius,cy+radius)
    fun containPoint(x:Float,y:Float):Boolean{
        return recF.contains(x,y)
    }
}