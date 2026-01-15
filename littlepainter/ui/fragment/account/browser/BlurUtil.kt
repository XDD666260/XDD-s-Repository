package com.example.littlepainter.ui.fragment.account.browser

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.HardwareRenderer
import android.graphics.PixelFormat
import android.graphics.RenderEffect
import android.graphics.RenderNode
import android.graphics.Shader
import android.hardware.HardwareBuffer
import android.media.ImageReader
import android.os.Build
import androidx.annotation.RequiresApi

object BlurUtil {
    @RequiresApi(Build.VERSION_CODES.S)
    fun blur(path:String, radius:Float):Bitmap{
        val mBitmap=BitmapFactory.decodeFile(path)
            val imageReader=ImageReader.newInstance(
                mBitmap.width,mBitmap.height,
            PixelFormat.RGBA_8888,1,
            HardwareBuffer.USAGE_GPU_SAMPLED_IMAGE or HardwareBuffer.USAGE_GPU_COLOR_OUTPUT
        )
        val renderNode=RenderNode("RenderEffect")
        val hardwareRender=HardwareRenderer()

        hardwareRender.setSurface(imageReader.surface)
        hardwareRender.setContentRoot(renderNode)
        renderNode.setPosition(0,0,imageReader.width,imageReader.height)

        val blurRenderEffect=RenderEffect.createBlurEffect(
            radius,radius,
            Shader.TileMode.MIRROR
        )
        renderNode.setRenderEffect(blurRenderEffect)
        val renderCanvas=renderNode.beginRecording()
        renderCanvas.drawBitmap(mBitmap,0f,0f,null)
        renderNode.endRecording()
        hardwareRender.createRenderRequest()
            .setWaitForPresent(true)
            .syncAndDraw()
        val image=imageReader.acquireNextImage() ?: throw RuntimeException("No Image")

        val hardwareBuffer=image.hardwareBuffer ?:throw RuntimeException("No HardwareBuffer")
        val bitmap=Bitmap.wrapHardwareBuffer(hardwareBuffer,null)
            ?:throw RuntimeException("Create Bitmap Failed")
        hardwareBuffer.close()
        image.close()
        return bitmap
    }
}