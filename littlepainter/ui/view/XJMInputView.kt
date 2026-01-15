package com.example.littlepainter.ui.view

import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import com.example.littlepainter.R
import com.example.littlepainter.databinding.LayoutInputViewBinding
import androidx.core.content.withStyledAttributes
import androidx.core.view.get

class XJMInputView: FrameLayout {
    private lateinit var binding:LayoutInputViewBinding
    constructor(context: Context):super(context){
        initView()
        initEvent()
    }
    constructor(context: Context,attrs:AttributeSet?):super(context,attrs){
        initView()
        initEvent()
        parseAttribute(attrs)
    }

    private fun parseAttribute(attrs: AttributeSet?) {
        context.withStyledAttributes(attrs, R.styleable.XJMInputView) {
            val info = getString(R.styleable.XJMInputView_info_text)
            val hint = getString(R.styleable.XJMInputView_hint_text)
            val iconFont=getResourceId(R.styleable.XJMInputView_iconFont,0)
            val inputType=getInt(R.styleable.XJMInputView_input_type,0)
            binding.tvInfo.text = info
            binding.etInput.hint = hint
            binding.imageView.setImageResource(iconFont)
            if (inputType==1){
                binding.etInput.inputType=InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
        }
    }

    private fun initView(){
        val layoutInflater=LayoutInflater.from(context)
        binding=LayoutInputViewBinding.inflate(layoutInflater,this,false)
        val layoutParams=LayoutParams(LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        addView(binding.root,layoutParams)
    }
    private fun initEvent(){
        val blue=resources.getColor(R.color.blue,null)
        val default=resources.getColor(R.color.light_black,null)
        binding.etInput.setOnFocusChangeListener{v,hasFocus->
            if (hasFocus){
                binding.etInput.setTextColor(blue)
                binding.view.setBackgroundColor(blue)
                binding.tvInfo.setTextColor(blue)
            }else{
                binding.etInput.setTextColor(default)
                binding.view.setBackgroundColor(default)
                binding.tvInfo.setTextColor(default)
            }
        }
    }
    fun text():String{
        return binding.etInput.text.toString()
    }

    fun addTextChangeListener(afterTextChanged:(String)->Unit){
        binding.etInput.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                afterTextChanged(s.toString())
            }
        })
    }

    fun showError(){
        binding.tvInfo.setTextColor(Color.RED)
        binding.etInput.setTextColor(Color.RED)
    }

}