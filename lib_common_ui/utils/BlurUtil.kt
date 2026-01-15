package com.baidu.lib_common_ui.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.HardwareRenderer
import android.graphics.PixelFormat
import android.graphics.RenderEffect
import android.graphics.RenderNode
import android.graphics.Shader
import android.graphics.drawable.Drawable
import android.hardware.HardwareBuffer
import android.media.ImageReader
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

object BlurUtil {
    fun blur(context: Context, path: String, radius: Float, callback: (Bitmap) -> Unit = {}) {
        val mBitmap =
            Glide.with(context).asBitmap().load(path).into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    blurImage(resource, radius)
                }

                override fun onLoadCleared(placeholder: Drawable?) {

                }

            })
    }

    fun blurImage(bitmap: Bitmap, radius: Float): Bitmap {
        isVersion29(task = {
            // 配置跟 bitmap 同样大小的 ImageReader
            val imageReader = ImageReader.newInstance(
                bitmap.width, bitmap.height,
                PixelFormat.RGBA_8888, 1,
                HardwareBuffer.USAGE_GPU_SAMPLED_IMAGE or HardwareBuffer.USAGE_GPU_COLOR_OUTPUT
            )
            val renderNode = RenderNode("RenderEffect")
            val hardwareRenderer = HardwareRenderer()

            // 将 ImageReader 的surface 设置到 HardwareRenderer 中
            hardwareRenderer.setSurface(imageReader.surface)
            hardwareRenderer.setContentRoot(renderNode)
            renderNode.setPosition(0, 0, imageReader.width, imageReader.height)

            // 使用 RenderEffect 配置模糊效果，并设置到 RenderNode 中。

            isVersion31(task = {
                val blurRenderEffect = RenderEffect.createBlurEffect(
                    radius, radius,
                    Shader.TileMode.MIRROR
                )
                renderNode.setRenderEffect(blurRenderEffect)
            })


            // 通过 RenderNode 的 RenderCanvas 绘制 Bitmap。
            val renderCanvas = renderNode.beginRecording()
            renderCanvas.drawBitmap(bitmap, 0f, 0f, null)
            renderNode.endRecording()

            // 通过 HardwareRenderer 创建 Render 异步请求。
            hardwareRenderer.createRenderRequest()
                .setWaitForPresent(true)
                .syncAndDraw()

            // 通过 ImageReader 获取模糊后的 Image 。
            val image = imageReader.acquireNextImage() ?: throw RuntimeException("No Image")

            // 将 Image 的 HardwareBuffer 包装为 Bitmap , 也就是模糊后的。
            val hardwareBuffer = image.hardwareBuffer ?: throw RuntimeException("No HardwareBuffer")
            val bitmap = Bitmap.wrapHardwareBuffer(hardwareBuffer, null)
                ?: throw RuntimeException("Create Bitmap Failed")
            hardwareBuffer.close()
            image.close()
        })
        return bitmap
    }
}