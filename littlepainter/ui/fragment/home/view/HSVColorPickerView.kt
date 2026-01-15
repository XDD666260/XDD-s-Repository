package com.example.littlepainter.ui.fragment.home.view

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.littlepainter.R
import com.example.littlepainter.ui.fragment.home.colorpicker.ColorAdapter
import com.example.littlepainter.ui.fragment.home.colorpicker.ItemAction
import com.example.littlepainter.ui.fragment.home.colorpicker.getDefaultColors

class HSVColorPickerView(context: Context,attrs:AttributeSet?): FrameLayout(context,attrs) {
    private lateinit var mColorPickerView: ColorPickerView
    private lateinit var mSaturationBar:AppCompatSeekBar
    private lateinit var mTvLightness:TextView
    private lateinit var mTvSaturation:TextView
    private lateinit var mLightnessBar:AppCompatSeekBar
    private lateinit var mRecycleView:RecyclerView
    private val mColorAdapter=ColorAdapter()
    private var mColorList:ArrayList<Int> = arrayListOf()

    var pickColorCallBack:(Int)->Unit={}

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mColorPickerView=findViewById(R.id.pickerView)
        mSaturationBar=findViewById(R.id.saturationBar)
        mLightnessBar=findViewById(R.id.lightnessBar)
        mTvLightness=findViewById(R.id.tvLightness)
        mTvSaturation=findViewById(R.id.tvSaturation)
        mRecycleView=findViewById(R.id.recycleView)
        mColorPickerView.addPickColorListener {color->
            pickColorCallBack(color)
        }
        //饱和度改变事件
        mSaturationBar.setOnSeekBarChangeListener(object :OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                mTvSaturation.text="$progress"
                mColorPickerView.setSaturation(progress/100f)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })
        mLightnessBar.setOnSeekBarChangeListener(object :OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                mTvLightness.text="$progress"
                mColorPickerView.setLightness(progress/100f)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
        mRecycleView.adapter=mColorAdapter
        mRecycleView.layoutManager=GridLayoutManager(context,5,RecyclerView.VERTICAL,false)
        mColorList.addAll(getDefaultColors())
        mColorAdapter.setDates(mColorList)

        //监听点击事件
        mColorAdapter.actionListener={ action,color->
            when(action){
                ItemAction.ADD->mColorList.add(0,mColorPickerView.getCurrentColor())
                ItemAction.DELETE->{
                    if (mColorList.isNotEmpty()){
                        mColorList.removeFirst()
                    }
                }
                else->{
                    mColorPickerView.setCurrentColor(color!!)
                }
            }
            mColorAdapter.setDates(mColorList)
        }
    }
}