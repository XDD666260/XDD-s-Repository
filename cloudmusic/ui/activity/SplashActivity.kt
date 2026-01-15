package com.baidu.cloudmusic.ui.activity

import android.content.Intent
import androidx.lifecycle.lifecycleScope
import com.baidu.cloudmusic.databinding.ActivitySplashBinding
import com.baidu.lib_common_ui.base.BaseActivity
import com.baidu.lib_common_ui.utils.fadeOut
import com.baidu.lib_common_ui.utils.loadUrl
import com.baidu.lib_leancloud.LeanCloudManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SplashActivity : BaseActivity<ActivitySplashBinding>() {
    private var time = 2
    private lateinit var timerJob: Job
    override fun initUI() {
        //后台下载首页数据
        LeanCloudManager.instance.loadAllMusics()
        LeanCloudManager.instance.loadCharts()

        //记录当前的时间
        val startTime = System.currentTimeMillis()
        //下载广告图片
        LeanCloudManager.instance.loadAdv { manager, advs ->
            //显示广告
            manager.getRandomAdv().also { adv ->
                mBinding.ivAdv.loadUrl(adv.image)

                val endTime = System.currentTimeMillis()
                if (endTime - startTime < 1500) {
                    lifecycleScope.launch {
                        delay(1000 - (endTime - startTime))

                        showAdvertisement()
                    }
                } else {
                    showAdvertisement()
                }
            }
        }
    }

    private fun showAdvertisement() {
        //红色欢迎就消失
        hideSplashView()

        //定时器开始倒计时
        timerJob = lifecycleScope.launch(Dispatchers.IO) {
            while (true) {
                delay(1000)
                time--
                //显示时间
                withContext(Dispatchers.Main) {
                    mBinding.tvTime.text = time.toString()
                }
                if (time == 0) {
                    //跳转到下一个界面
                    goToMainActivity()
                    timerJob.cancel(null)
                    break
                }

            }
        }
    }

    private fun goToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun hideSplashView() {
        mBinding.splashView.fadeOut()
    }

}