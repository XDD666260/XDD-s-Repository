package com.example.littlepainter.ui.fragment.home

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.Gravity
import android.view.View
import android.view.WindowManager.LayoutParams
import android.view.animation.LinearInterpolator
import android.widget.PopupWindow
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.littlepainter.R
import com.example.littlepainter.databinding.ColorPickerLayoutBinding
import com.example.littlepainter.databinding.FragmentHomeBinding
import com.example.littlepainter.databinding.LayerPopupViewLayoutBinding
import com.example.littlepainter.ui.base.BaseFragment
import com.example.littlepainter.ui.fragment.home.view.HSVColorPickerView
import com.example.littlepainter.utils.IconState
import com.example.littlepainter.utils.IconType
import com.example.littlepainter.utils.ViewUtils
import com.example.littlepainter.utils.getDrawToolIconModels
import com.example.littlepainter.utils.getHomeMenuIconModels
import com.example.littlepainter.utils.getMenuIconModel
import com.example.littlepainter.utils.getOperationToolIconModels
import com.example.littlepainter.viewmodel.HomeViewModel
import androidx.recyclerview.widget.RecyclerView
import com.drake.brv.BindingAdapter
import com.drake.brv.listener.DefaultItemTouchCallback
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.linear
import com.drake.brv.utils.models
import com.drake.brv.utils.setup
import com.example.littlepainter.databinding.LayerItemLayoutBinding
import com.example.littlepainter.ui.fragment.home.draw.BroadCastCenter
import com.example.littlepainter.ui.fragment.home.file.FileManager
import com.example.littlepainter.ui.fragment.home.layer.LayerModel
import com.example.littlepainter.ui.fragment.home.layer.LayerModelManager
import com.example.littlepainter.ui.fragment.home.layer.LayerState
import com.example.littlepainter.ui.fragment.home.view.account.AccountPopupWindow
import com.example.littlepainter.ui.fragment.home.view.bg_image.PickBackgroundImagePopupWindow
import com.example.littlepainter.ui.fragment.home.view.loadingview.LoadingView
import com.example.littlepainter.ui.fragment.home.view.strokebar.StrokeBarView
import com.example.littlepainter.utils.TimeUtil
import com.example.littlepainter.utils.delayTask
import com.example.littlepainter.viewmodel.UserViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import androidx.core.graphics.scale
import androidx.navigation.fragment.findNavController

class HomeFragment: BaseFragment<FragmentHomeBinding>() {
    private val mUserViewModel:UserViewModel by activityViewModels()
    private var isDrawMenuOpen=true
    private var isMainMenuOpen=true

    //左侧动画
    private val drawMenuAnimOpenBottom:Int by lazy {
        mBinding.drawLayout.bottom
    }
    private val drawMenuAnimCloseBottom:Int by lazy {
        mBinding.drawLayout.top+mBinding.iconMenuView.top+ViewUtils.dp2px(8)
    }
    private val drawMenuCloseAnimator:AnimatorSet by lazy {
        val r=ObjectAnimator.ofFloat(mBinding.menuIconView,"rotation",0f,180f).apply {
            duration=500
        }
        val m=ObjectAnimator.ofInt(mBinding.drawLayout,"bottom",drawMenuAnimOpenBottom,drawMenuAnimCloseBottom).apply {
            duration=500
            interpolator=LinearInterpolator()
        }
        AnimatorSet().apply {
            playTogether(r,m)
        }
    }
    private val drawMenuOpenAnimator:AnimatorSet by lazy {
        val r=ObjectAnimator.ofFloat(mBinding.menuIconView,"rotation",0f,180f).apply {
            duration=500
        }
        val m=ObjectAnimator.ofInt(mBinding.drawLayout,"bottom",drawMenuAnimCloseBottom,drawMenuAnimOpenBottom).apply {
            duration=500
            interpolator=LinearInterpolator()
        }
        AnimatorSet().apply {
            playTogether(r,m)
        }
    }
    //顶部动画
    private val mainMenuAnimOpenLeft:Int by lazy {
        mBinding.mainMenuView.left
    }
    private val mainMenuAnimCloseLeft:Int by lazy {
        mBinding.mainMenuView.right
    }
    private val mainMenuCloseAnimator:AnimatorSet by lazy {
        val m=ObjectAnimator.ofInt(mBinding.mainMenuView,"left",mainMenuAnimOpenLeft,mainMenuAnimCloseLeft).apply {
            duration=500
            interpolator=LinearInterpolator()
        }
        val r=ObjectAnimator.ofFloat(mBinding.arrowImageView,"rotation",0f,180f).apply {
            duration=500
        }
        AnimatorSet().apply {
            playTogether(m,r)
        }
    }
    private val mainMenuOpenAnimator:AnimatorSet by lazy {
        val m=ObjectAnimator.ofInt(mBinding.mainMenuView,"left",mainMenuAnimCloseLeft,mainMenuAnimOpenLeft).apply {
            duration=500
            interpolator=LinearInterpolator()
        }
        val r=ObjectAnimator.ofFloat(mBinding.arrowImageView,"rotation",180f,360f).apply {
            duration=500
        }
        AnimatorSet().apply {
            playTogether(m,r)
        }
    }

    //右侧动画
    private val hideLeft:Int by lazy {
        mBinding.root.width
    }
    private val showLeft:Int by lazy {
        mBinding.actionMenuView.left
    }
    private val actionMenuHideAnimator:AnimatorSet by lazy {
        val alpha=ObjectAnimator.ofFloat(mBinding.actionMenuView,"alpha",1f,0f).apply {
            duration=500
        }
        val m=ObjectAnimator.ofInt(mBinding.actionMenuView,"left",showLeft,hideLeft).apply {
            duration=500
            interpolator=LinearInterpolator()
        }
        AnimatorSet().apply {
            playTogether(alpha,m)
        }
    }
    private val actionMenuShowAnimator:AnimatorSet by lazy {
        val alpha=ObjectAnimator.ofFloat(mBinding.actionMenuView,"alpha",0f,1f).apply {
            duration=500
        }
        val m=ObjectAnimator.ofInt(mBinding.actionMenuView,"left",hideLeft,showLeft).apply {
            duration=500
            interpolator=LinearInterpolator()
        }
        AnimatorSet().apply {
            playTogether(alpha,m)
        }
    }

    //颜色选择器对象
    private lateinit var mHSVColorPickerView: HSVColorPickerView
    private val mColorPickerPopupWindow:PopupWindow by lazy {
        val colorPickerBinding=ColorPickerLayoutBinding.inflate(layoutInflater)
        mHSVColorPickerView=colorPickerBinding.root
        //颜色选择完毕
        mHSVColorPickerView.pickColorCallBack={color->
            //mColorPickerPopupWindow.dismiss()
            HomeViewModel.instance().mColor=color
            mBinding.drawView.refreshTextColor()
        }
        PopupWindow(requireContext()).apply {
            contentView=colorPickerBinding.root
            width=LayoutParams.WRAP_CONTENT
            height=LayoutParams.WRAP_CONTENT
            setBackgroundDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.corner_round_shape))
            //isFocusable=true
            //isOutsideTouchable=true
        }
    }
    private var mLayerPopupViewBinding:LayerPopupViewLayoutBinding?=null
    //弹出一个图层的显示视图
    private val mLayerPopupWindow:PopupWindow by lazy {
        mLayerPopupViewBinding=LayerPopupViewLayoutBinding.inflate(layoutInflater)
        mLayerPopupViewBinding!!.addBtn.clickCallback={
            HomeViewModel.instance().mLayerManager.addLayer(mBinding.drawView.width,mBinding.drawView.height)

            refreshLayerRecycleView()
        }
        initLayerRecycleView()
        PopupWindow(requireContext()).apply {
            contentView=mLayerPopupViewBinding!!.root
            width=LayoutParams.WRAP_CONTENT
            height=LayoutParams.WRAP_CONTENT
            setBackgroundDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.corner_round_shape))
        }
    }

    //画笔粗细显示视图
    private val mStrokeBarPopupWindow:PopupWindow by lazy {
        val barView= StrokeBarView(requireContext())

        PopupWindow(requireContext()).apply {
            contentView=barView
            width=LayoutParams.WRAP_CONTENT
            height=LayoutParams.WRAP_CONTENT
            setBackgroundDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.corner_round_shape))
        }
    }
    //选择背景
    private val pickImagePopupWindow:PickBackgroundImagePopupWindow by lazy {
        PickBackgroundImagePopupWindow(requireContext()).apply {
            addImageSelectListener={resId->
                mBinding.drawView.changeBackgroundImage(resId)
            }
        }
    }
    private val mLoadingView:LoadingView by lazy {
        LoadingView(requireContext())
    }
    private val mAccountPopupWindow:AccountPopupWindow by lazy {
        AccountPopupWindow(requireContext(),mUserViewModel)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        HomeViewModel.init(this)
    }
    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    override fun initUI(savedInstanceState: Bundle?) {
        super.initUI(savedInstanceState)
        //菜单按钮
        mBinding.menuIconView.setIconModel(getMenuIconModel())
        //左侧
        mBinding.iconMenuView.setIcons(getDrawToolIconModels())
        //顶部
        mBinding.mainMenuView.setIcons(getHomeMenuIconModels())
        //右侧
        mBinding.actionMenuView.setIcons(getOperationToolIconModels())

        //左侧按钮监听回调
        mBinding.iconMenuView.iconClickListener={type,state->
            sendUnselectShapeBroadCast()
            if (type==IconType.DRAW_MOVE){

                HomeViewModel.instance().mLayerManager.updateMoveMode(state==IconState.SELECTED)
            }
            if (state==IconState.NORMAL){
                mBinding.drawView.setCurrentDrawType(IconType.NONE)

            }else{
                mBinding.drawView.setCurrentDrawType(type)
            }
        }
        //菜单按钮被点击
        mBinding.menuIconView.clickCallback= {
            sendUnselectShapeBroadCast()
            //if (drawMenuOpenAnimator.isRunning || drawMenuCloseAnimator.isRunning)
            if (isDrawMenuOpen){
                //关闭动画
                drawMenuCloseAnimator.start()
                actionMenuHideAnimator.start()
                //隐藏颜色选择器
                hideColorPicker()
                mBinding.actionMenuView.resetIconState()
                mBinding.iconMenuView.resetIconState()
                mBinding.drawView.resetDrawToolType()
            }else{
                drawMenuOpenAnimator.start()
                actionMenuShowAnimator.start()
            }
            isDrawMenuOpen=!isDrawMenuOpen
        }
        //顶部收起与展开动画
        mBinding.arrowImageView.setOnClickListener {
            sendUnselectShapeBroadCast()
            if (isMainMenuOpen){
                mainMenuCloseAnimator.start()
                hideLayerView()
                mBinding.mainMenuView.resetIconState()
            }else{
                mainMenuOpenAnimator.start()
            }
            isMainMenuOpen=!isMainMenuOpen
        }
        //右侧按钮
        mBinding.actionMenuView.iconClickListener={type,state->
            sendUnselectShapeBroadCast()
            when(type){
                //颜色选择器
                IconType.OPERATION_PALETTE->{
                    if (state==IconState.NORMAL){
                        hideColorPicker()
                    }else{
                        showColorPicker()
                    }
                }
                //撤销
                IconType.OPERATION_UNDO->{
                    delayTask(200){
                        mBinding.actionMenuView.resetIconState()
                    }

                    HomeViewModel.instance().mLayerManager.undo()
                    mBinding.drawView.refresh()
                    refreshLayerRecycleView()
                }
                //删除
                IconType.OPERATION_DELETE->{
                    delayTask(200){
                        mBinding.actionMenuView.resetIconState()
                    }

                    HomeViewModel.instance().mLayerManager.clearLayer()
                    mBinding.drawView.refresh()
                    refreshLayerRecycleView()
                }
                IconType.OPERATION_PENCIL->{
                    if (state==IconState.NORMAL){
                        hideStrokeVarView()
                    }else{
                        showStrokeBarView()
                    }
                }
                else->{}
            }
        }
        //监听顶部的事件
        mBinding.mainMenuView.iconClickListener={type,state->
            sendUnselectShapeBroadCast()
            when(type){
                IconType.MENU_LAYER->{
                    pickImagePopupWindow.hide()
                    mAccountPopupWindow.hide()
                    if (state==IconState.NORMAL){
                        hideLayerView()
                    }else{
                        showLayerView()
                    }
                }
                IconType.MENU_PICTURE->{
                    hideLayerView()
                    mAccountPopupWindow.hide()
                    if (state==IconState.NORMAL){
                        pickImagePopupWindow.hide()
                    }else{
                        pickImagePopupWindow.showAsDropDown(mBinding.mainMenuView,ViewUtils.dp2px(100),0)
                    }
                }
                IconType.MENU_DOWNLOAD->{
                    delayTask(200){
                        mBinding.mainMenuView.resetIconState()
                        hideLayerView()
                        mAccountPopupWindow.hide()
                        pickImagePopupWindow.hide()
                    }
                    saveDrawViewToAlbum()

                }
                IconType.MENU_SHARE->{
                    delayTask(200){
                        mBinding.mainMenuView.resetIconState()
                        hideLayerView()
                        mAccountPopupWindow.hide()
                        pickImagePopupWindow.hide()
                    }
                    shareImage()
                }
                IconType.MENU_ACCOUNT->{
                    hideLayerView()
                    pickImagePopupWindow.hide()
                    if (state==IconState.NORMAL){
                        mAccountPopupWindow.hide()
                    }else{
                        mAccountPopupWindow.showAsDropDown(mBinding.mainMenuView)
                    }
                }
                IconType.MENU_SAVE->{
                    delayTask(200){
                        mBinding.mainMenuView.resetIconState()
                        hideLayerView()
                        mAccountPopupWindow.hide()
                        pickImagePopupWindow.hide()
                    }
                    //保存到本地
                    mLoadingView.show(mBinding.root)
                    lifecycleScope.launch(Dispatchers.IO) {
                        mBinding.drawView.getBitmap().collect{bitmap->
                            val name=TimeUtil.getTimeName()
                            //保存原图
                            FileManager.instance.saveBitmap(bitmap, name,true)
                            //保存缩略图
                            val thumbBitmap= bitmap.scale(
                                (bitmap.width * 0.2).toInt(),
                                (bitmap.height * 0.2).toInt()
                            )
                            FileManager.instance.saveBitmap(thumbBitmap,name,false)
                            withContext(Dispatchers.Main){
                                mLoadingView.hide()
                            }
                        }
                    }
                }
                else->{}
            }
        }

        //监听是否应该刷新layer视图
        mBinding.drawView.refreshLayerListener={
            refreshLayerRecycleView()
        }
        //监听输入框
        mBinding.edInput.addTextChangedListener(afterTextChanged = {
            mBinding.drawView.refreshText(it.toString())
        })
        mBinding.drawView.addShowKeyBoardListener={ isShow->
            if (isShow){
                mBinding.edInput.requestFocus()
                showKeyBoard()
            }else{
                mBinding.edInput.clearFocus()
                hideKeyBoard()
                mBinding.edInput.text.clear()
            }

        }
    }

    private fun saveImageToExternalPath(file:File,bitmap: Bitmap){
        FileOutputStream(file).use { fos->
            BufferedOutputStream(fos).use { bos->
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,bos)
            }
        }
    }
    //分享图片
    private fun shareImage() {
        //将drawView转换为图片
        lifecycleScope.launch {
            mBinding.drawView.getBitmap().collect{bitmap->
                val externalDir=requireContext().getExternalFilesDir(null)
                val file=File(externalDir,"infinity")
                saveImageToExternalPath(file,bitmap)
                val uri=FileProvider.getUriForFile(
                    requireContext(),
                    "com.example.littlepainter.provider",
                    file
                )
                val intent=Intent(Intent.ACTION_SEND)
                intent.type="image/jpeg"
                intent.putExtra(Intent.EXTRA_STREAM,uri)
                requireContext().startActivity(Intent.createChooser(intent,"分享图片"))
            }
        }
    }

    //保存到相册
    private fun saveDrawViewToAlbum() {
        //显示加载
        mLoadingView.show(mBinding.root)
        //将drawView上所有bitmap绘制到一个bitmap
        lifecycleScope.launch{
            mBinding.drawView.getBitmap().collect{bitmap->
                //下载到本地
                //定位图片在系统中的位置
                val imagesUri=
                    MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                //确定插入的数据和字段
                val contentValues=ContentValues().apply {
                    //确定名字
                    put(MediaStore.Images.Media.DISPLAY_NAME,TimeUtil.getTimeName())
                    put(MediaStore.Images.Media.WIDTH,"${bitmap.width}")
                    put(MediaStore.Images.Media.HEIGHT,"${bitmap.height}")
                    put(MediaStore.Images.Media.MIME_TYPE,"image/jpeg")

                }
                val imgUri=requireContext().contentResolver.insert(imagesUri,contentValues)
                imgUri?.let {
                    requireContext().contentResolver.openOutputStream(imgUri)?.use {
                        bitmap.compress(Bitmap.CompressFormat.JPEG,100,it)

                        delayTask(1000){
                            mLoadingView.hide()
                        }

                    }
                }
            }
        }
    }

    private fun showKeyBoard(){
        val insetsController=WindowCompat.getInsetsController(requireActivity().window,mBinding.edInput)
        insetsController.show(WindowInsetsCompat.Type.ime())
    }
    private fun hideKeyBoard(){
        val insetsController=WindowCompat.getInsetsController(requireActivity().window,mBinding.edInput)
        insetsController.hide(WindowInsetsCompat.Type.ime())
    }
    private fun refreshLayerRecycleView(){
        if (mLayerPopupViewBinding!=null){
            mLayerPopupViewBinding!!.recycleView.models= LayerModelManager.instance.getLayerModels()
            mLayerPopupViewBinding!!.recycleView.bindingAdapter.notifyDataSetChanged()
        }
    }
    private fun initLayerRecycleView(){
        mLayerPopupViewBinding!!.recycleView.linear().setup {
            addType<LayerModel>(R.layout.layer_item_layout)
            //绑定数据
            onBind {
                val binding=getBinding<LayerItemLayoutBinding>()
                val data=getModel<LayerModel>()
                binding.layerImageView.setImageBitmap(data.bitmap)
                binding.coverView.visibility=if (data.state==LayerState.NORMAL){
                    View.INVISIBLE
                }else{
                    View.VISIBLE
                }
                binding.root.setOnClickListener{
                    LayerModelManager.instance.selectLayer(data)
                    refreshLayerRecycleView()
                }
            }
            itemTouchHelper= ItemTouchHelper(object : DefaultItemTouchCallback(){
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    //super.onSwiped(viewHolder, direction)
                    val index=viewHolder.layoutPosition
                    val model=(viewHolder as BindingAdapter.BindingViewHolder).getModel<LayerModel>()
                    LayerModelManager.instance.removeLayer(model.id)
                    HomeViewModel.instance().mLayerManager.removeLayer(model.id)
                    refreshLayerRecycleView()
                    mBinding.drawView.refresh()
                }

                override fun onDrag(
                    source: BindingAdapter.BindingViewHolder,
                    target: BindingAdapter.BindingViewHolder
                ) {
                    //super.onDrag(source, target)
                    //获取交换的索引值
                    val sourceIndex=source.layoutPosition
                    val targetIndex=target.layoutPosition
                    val sLayerModel=source.getModel<LayerModel>()
                    val tLayerModel=target.getModel<LayerModel>()
                    HomeViewModel.instance().mLayerManager.switchLayer(sourceIndex,targetIndex)
                    mBinding.drawView.refresh()
                }
            })
        }.models= LayerModelManager.instance.getLayerModels()
    }
    //隐藏颜色选择器
    private fun hideColorPicker(){
        mColorPickerPopupWindow.dismiss()
        mBinding.actionMenuView.resetIconState()
    }
    //显示颜色选择器
    private fun showColorPicker() {
        mColorPickerPopupWindow.showAtLocation(
            mBinding.root,
            Gravity.END,
            mBinding.root.width - mBinding.actionMenuView.left,
            0
        )
    }
    //隐藏图层视图
    private fun hideLayerView(){
        mLayerPopupWindow.dismiss()

    }
    //显示图层视图
    private fun showLayerView(){
        mLayerPopupWindow.showAsDropDown(
            mBinding.mainMenuView,
            ViewUtils.dp2px(80),
            0
        )
    }
    private fun showStrokeBarView(){
        mStrokeBarPopupWindow.showAtLocation(
            mBinding.root,
            Gravity.END,
            mBinding.root.width - mBinding.actionMenuView.left,
            0
        )
    }
    private fun hideStrokeVarView(){
        mStrokeBarPopupWindow.dismiss()
    }
    //发送通知
    private fun sendUnselectShapeBroadCast(){
        //发送广播，取消选中状态
        requireActivity().sendBroadcast(Intent(BroadCastCenter.ICON_CLICK_BROADCAST_NAME))
        delayTask(200){
            mBinding.drawView.refresh()
        }
    }
}