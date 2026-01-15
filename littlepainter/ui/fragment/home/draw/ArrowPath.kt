package com.example.littlepainter.ui.fragment.home.draw

import android.graphics.Path

object ArrowPath {
    fun addArrowToPath(mPath:Path,startX:Float,startY:Float,endX:Float,endY:Float,mArrowLength:Float){
        mPath.moveTo(startX,startY)
        mPath.lineTo(endX,endY)

        val angle =Math.atan2((endY-startY).toDouble(),(endX-startX).toDouble())

        val arrowX1=endX-mArrowLength*Math.cos(angle-Math.PI/6).toFloat()
        val arrowY1=endY-mArrowLength*Math.sin(angle-Math.PI/6).toFloat()

        val arrowX2=endX-mArrowLength*Math.cos(angle+Math.PI/6).toFloat()
        val arrowY2=endY-mArrowLength*Math.sin(angle+Math.PI/6).toFloat()
        mPath.lineTo(arrowX1,arrowY1)
        mPath.moveTo(endX,endY)
        mPath.lineTo(arrowX2,arrowY2)
    }
}