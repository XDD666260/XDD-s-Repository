package com.baidu.cloudmusic.ui.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.lifecycle.ViewModelProvider
import com.baidu.cloudmusic.R
import com.baidu.cloudmusic.adapter.MainAdapter
import com.baidu.cloudmusic.databinding.ActivityMainBinding
import com.baidu.cloudmusic.ui.fragment.CircleFragment
import com.baidu.cloudmusic.ui.fragment.HomeFragment
import com.baidu.cloudmusic.ui.fragment.LiveFragment
import com.baidu.cloudmusic.ui.fragment.MeFragment
import com.baidu.cloudmusic.viewmodel.MainViewModel
import com.baidu.lib_audio.AudioHelper
import com.baidu.lib_audio.AudioPlayer
import com.baidu.lib_audio.MusicService
import com.baidu.lib_common_ui.base.BaseActivity
import com.baidu.lib_common_ui.utils.PermissionsUtils
import com.baidu.lib_common_ui.utils.isVersion34
import com.baidu.lib_leancloud.LeanCloudManager
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : BaseActivity<ActivityMainBinding>() {
    private val mServiceIntent: Intent by lazy {
        Intent(this, MusicService::class.java)
    }

    override fun initUI() {
        initViewPagerAndTabLayout()

        initBottomViewPlayList()

        requestPostNotificationPermission()

        AudioHelper.initViewModel(this)
    }


    private fun requestPostNotificationPermission() {
        PermissionsUtils.sharedInstance.init(this, "需要权限")
        //判断版本是不是大于33
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            PermissionsUtils.sharedInstance.checkPermission(Manifest.permission.POST_NOTIFICATIONS)
                .also {
                    if (it) {
                        //已经有权限了
                        //启动音乐播放的服务
                        startService(mServiceIntent)
                    } else {
                        //没有权限
                        PermissionsUtils.sharedInstance.requestPermission(Manifest.permission.POST_NOTIFICATIONS) { result ->
                            if (result) {
                                //启动音乐播放的服务
                                startService(mServiceIntent)
                            }
                        }
                    }
                }
        }
    }

    private fun initBottomViewPlayList() {
        //监听数据源
        LeanCloudManager.instance.chartMusicModelList.observe(this) {
            //默认给第一个榜单的音乐数据
            mBinding.bottomPlayView.setPlayList(it[0].musics)
        }
    }

    private fun initViewPagerAndTabLayout() {
        mBinding.viewPager.isUserInputEnabled = false
        mBinding.viewPager.adapter = MainAdapter(
            this, listOf(
                HomeFragment(), LiveFragment(), CircleFragment(), MeFragment()
            )
        )
        TabLayoutMediator(mBinding.tabLayout, mBinding.viewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.setIcon(R.drawable.tab_home)
                    tab.setText("主页")
                }

                1 -> {
                    tab.setIcon(R.drawable.tab_live)
                    tab.setText("现场")
                }

                2 -> {
                    tab.setIcon(R.drawable.tab_circle)
                    tab.setText("圈子")
                }

                3 -> {
                    tab.setIcon(R.drawable.tab_me)
                    tab.setText("我的")
                }
            }
        }.attach()
    }

    override fun onDestroy() {
        super.onDestroy()
        //stopService(mServiceIntent)
    }
}
