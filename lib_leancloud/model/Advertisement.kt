package com.baidu.lib_leancloud.model

import cn.leancloud.LCObject
import cn.leancloud.annotation.LCClassName

@LCClassName("Adv")
class Advertisement: LCObject(){
    val image: String
        get() {
            return getString("image")
        }
}
