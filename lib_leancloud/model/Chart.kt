package com.baidu.lib_leancloud.model

import cn.leancloud.LCObject
import cn.leancloud.annotation.LCClassName

/**
 * 榜单信息
 * 榜单名称
 * 榜单id
 */
@LCClassName("Chart")
class Chart: LCObject()  {
    val name: String
        get() {
            return getString("name")
        }
    val id: String
        get() {
            return getString("objectId")
        }
}