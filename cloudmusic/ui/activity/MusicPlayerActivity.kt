package com.baidu.cloudmusic.ui.activity

import android.app.Activity
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.baidu.cloudmusic.R
import com.baidu.cloudmusic.databinding.ActivityMusicPlayerBinding
import com.baidu.lib_common_ui.base.BaseActivity
import com.baidu.lib_common_ui.utils.isVersion34

class MusicPlayerActivity : BaseActivity<ActivityMusicPlayerBinding>() {
    override fun initUI() {
        super.initUI()

        initActivityTransitionAnimation()
    }

    private fun initActivityTransitionAnimation() {
        isVersion34(task = {
            overrideActivityTransition(
                Activity.OVERRIDE_TRANSITION_OPEN,
                R.anim.bottom_in,
                0
            )

            overrideActivityTransition(
                Activity.OVERRIDE_TRANSITION_CLOSE,
                0,
                R.anim.bottom_out
            )
        }, elseTask = {
            overridePendingTransition(R.anim.bottom_in, 0)
        })
    }
}