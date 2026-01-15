package com.example.littlepainter.ui.fragment.account.browser

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.drake.brv.listener.DefaultItemTouchCallback
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.linear
import com.drake.brv.utils.models
import com.drake.brv.utils.setup
import com.example.littlepainter.R
import com.example.littlepainter.databinding.FragmentPhotoBroswerBinding
import com.example.littlepainter.databinding.PhotoBroserItemLayoutBinding
import com.example.littlepainter.model.IconModel
import com.example.littlepainter.ui.base.BaseFragment
import com.example.littlepainter.ui.fragment.account.PhotoViewModel
import com.example.littlepainter.ui.fragment.account.album.PhotoModel
import com.example.littlepainter.ui.fragment.home.file.FileManager
import com.example.littlepainter.ui.fragment.home.view.loadingview.LoadingView
import com.example.littlepainter.utils.IconType
import com.example.littlepainter.utils.TimeUtil
import com.example.littlepainter.utils.delayTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream


class PhotoBrowserFragment : BaseFragment<FragmentPhotoBroswerBinding>() {
    private var mCurrentIndex:Int=0
    private val mViewModel:PhotoViewModel by activityViewModels()
    private val mLoadingView: LoadingView by lazy {
        LoadingView(requireContext())
    }
    @RequiresApi(Build.VERSION_CODES.S)
    override fun initUI(savedInstanceState: Bundle?) {
        super.initUI(savedInstanceState)
        //监听数据
        mViewModel.photoModels.observe(viewLifecycleOwner){models->
            mBinding.recyclerView.models=models
            mBinding.recyclerView.scrollToPosition(mViewModel.selectedIndex)

            mCurrentIndex=mViewModel.selectedIndex
            mBinding.ivBg.setImageBitmap(BlurUtil.blur(models[mViewModel.selectedIndex].thumbnailPath,50f))
        }
        //按页显示
        PagerSnapHelper().attachToRecyclerView(mBinding.recyclerView)
        mBinding.recyclerView.linear(RecyclerView.HORIZONTAL).setup {
            addType<PhotoModel>(R.layout.photo_broser_item_layout)
            onBind {
                val binding=getBinding<PhotoBroserItemLayoutBinding>()
                val model=getModel<PhotoModel>()
                Glide.with(context).load(model.thumbnailPath).into(binding.iconImageView)
            }
        }
        mBinding.recyclerView.addOnScrollListener(object :RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val index=(recyclerView.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()

                if (index!=-1 && mCurrentIndex!=index){
                    val models=mViewModel.photoModels.value
                    models?.let {
                        mBinding.ivBg.setImageBitmap(
                            BlurUtil.blur(it[index].thumbnailPath,50f))
                    }
                }
                mCurrentIndex=index
            }
        })
        //配置底部按钮
        mBinding.mainMenuLayout.setIcons(listOf(
            IconModel(IconType.OPERATION_DELETE,R.string.garbage),
            IconModel(IconType.MENU_DOWNLOAD,R.string.download),
            IconModel(IconType.MENU_SHARE,R.string.share)
        ))
        mBinding.mainMenuLayout.iconClickListener={type,_->
            delayTask(200){
                mBinding.mainMenuLayout.resetIconState()
            }
            when(type){
                IconType.OPERATION_DELETE->{
                    //删除选中图片
                    mLoadingView.show(mBinding.root)
                    lifecycleScope.launch(Dispatchers.IO) {
                        val model= mViewModel.photoModels.value[mCurrentIndex]
                            FileManager.instance.removeFile(model.originalPath)
                            FileManager.instance.removeFile(model.thumbnailPath)

                        withContext(Dispatchers.Main){
                            mLoadingView.hide{
                                mViewModel.remove(model)
                                mBinding.recyclerView.bindingAdapter.notifyDataSetChanged()
                                if (mViewModel.photoModels.value!!.isNotEmpty()){
                                    if (mCurrentIndex==mViewModel.photoModels.value.size-1){
                                        mCurrentIndex-=1
                                    }
                                    val newModel=mViewModel.photoModels.value!![mCurrentIndex]
                                    mBinding.ivBg.setImageBitmap(BlurUtil.blur(newModel.thumbnailPath,50f))
                                }else{
                                    mBinding.ivBg.visibility=View.INVISIBLE
                                }
                            }
                        }
                    }
                }
                IconType.MENU_DOWNLOAD->{
                    downloadToAlbum()
                }
                else->{
                    shareImage()
                }
            }
        }
    }
    //保存到相册
    private fun downloadToAlbum() {
        val path=mViewModel.photoModels.value!![mCurrentIndex].originalPath
        val bitmap=BitmapFactory.decodeFile(path)
        //显示加载
        mLoadingView.show(mBinding.root)
        //将drawView上所有bitmap绘制到一个bitmap
        lifecycleScope.launch{
                //下载到本地
                //定位图片在系统中的位置
                val imagesUri=
                    MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                //确定插入的数据和字段
                val contentValues= ContentValues().apply {
                    //确定名字
                    put(MediaStore.Images.Media.DISPLAY_NAME, TimeUtil.getTimeName())
                    put(MediaStore.Images.Media.WIDTH, "${bitmap.width}")
                    put(MediaStore.Images.Media.HEIGHT, "${bitmap.height}")
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
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
    private fun shareImage() {
        val path=mViewModel.photoModels.value!![mCurrentIndex].originalPath
        val bitmap=BitmapFactory.decodeFile(path)
        //将drawView转换为图片
        lifecycleScope.launch {
                val externalDir=requireContext().getExternalFilesDir(null)
                val file= File(externalDir,"infinity")
                saveImageToExternalPath(file,bitmap)
                val uri= FileProvider.getUriForFile(
                    requireContext(),
                    "com.example.littlepainter.provider",
                    file
                )
                val intent= Intent(Intent.ACTION_SEND)
                intent.type="image/jpeg"
                intent.putExtra(Intent.EXTRA_STREAM,uri)
                requireContext().startActivity(Intent.createChooser(intent,"分享图片"))
        }
    }
    private fun saveImageToExternalPath(file:File,bitmap: Bitmap){
        FileOutputStream(file).use { fos->
            BufferedOutputStream(fos).use { bos->
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,bos)
            }
        }
    }
}