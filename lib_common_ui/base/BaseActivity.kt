package com.baidu.lib_common_ui.base

import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType
import kotlin.jvm.java

abstract class BaseActivity<T : ViewBinding> : AppCompatActivity() {

    //内部真实保存binding对象的变量
    private lateinit var _binding: T

    //给外部提供一个属性访问这个binding对象
    val mBinding: T
        get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = initBinding()
        setContentView(mBinding.root)

        initUI()
    }

    open fun initUI() {

    }

    fun initBinding(): T {
        // 获取泛型类型
        val type = (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0]
        val clazz = type as Class<*>

        // 查找inflate方法
        val method = clazz.getMethod("inflate", LayoutInflater::class.java)
        return method.invoke(null, layoutInflater) as T
    }
}