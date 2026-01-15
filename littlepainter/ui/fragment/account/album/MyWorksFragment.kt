package com.example.littlepainter.ui.fragment.account.album

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.grid
import com.drake.brv.utils.models
import com.drake.brv.utils.setup
import com.example.littlepainter.R
import com.example.littlepainter.databinding.FragmentMyWorksBinding
import com.example.littlepainter.databinding.MyworkAlbumItemLayoutBinding
import com.example.littlepainter.ui.base.BaseFragment
import com.example.littlepainter.ui.fragment.account.PhotoViewModel
import com.example.littlepainter.ui.fragment.home.file.FileManager
import com.example.littlepainter.ui.fragment.home.view.loadingview.LoadingView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyWorksFragment:BaseFragment<FragmentMyWorksBinding>() {
    private val mViewModel:PhotoViewModel by activityViewModels()
    private var mEditingState= EditingState.NORMAL
    private var mSelectedModel= arrayListOf<PhotoModel>()
    private val mLoadingView:LoadingView by lazy {
        LoadingView(requireContext())
    }
    override fun initUI(savedInstanceState: Bundle?) {
        mViewModel.reloadData()
        mViewModel.photoModels.observe(viewLifecycleOwner){models->
            mBinding.recycleView.models=models
        }

        mBinding.recycleView.grid(5).setup {
            addType<PhotoModel>(R.layout.mywork_album_item_layout)
            onBind {
                val binding=getBinding<MyworkAlbumItemLayoutBinding>()
                val model=getModel<PhotoModel>()
                Glide.with(requireContext()).load(model.thumbnailPath).into(binding.ivThumbnail)
                if (mEditingState== EditingState.NORMAL){
                    binding.ivUnChoose.visibility=View.INVISIBLE
                }else{
                    binding.ivUnChoose.visibility=View.VISIBLE
                }
                if (model.selectSate== SelectState.NORMAL){
                    binding.ivSelected.visibility=View.INVISIBLE
                }else{
                    binding.ivSelected.visibility=View.VISIBLE
                }
                binding.root.setOnClickListener{
                    if (mEditingState== EditingState.NORMAL){
                        //进入照片浏览
                        mViewModel.selectedIndex=modelPosition
                        findNavController().navigate(R.id.action_myWorksFragment_to_photoBrowserFragment)
                    }else{
                        if (model.selectSate== SelectState.NORMAL){
                            //选中
                            model.selectSate= SelectState.SELECTED
                            mSelectedModel.add(model)
                        }else{
                            //取消选中
                            model.selectSate= SelectState.NORMAL
                            mSelectedModel.remove(model)
                        }
                        notifyItemChanged(modelPosition)
                    }
                }
            }
        }
        mBinding.tvEdit.setOnClickListener {
            if (mEditingState== EditingState.NORMAL){
                mBinding.tvEdit.text="Done"
                mBinding.tvEdit.setTextColor(Color.WHITE)
                mBinding.ivDelete.visibility=View.VISIBLE
                mEditingState= EditingState.EDITING
            }else{
                mBinding.tvEdit.text="Edit"
                mBinding.tvEdit.setTextColor(Color.parseColor("#B4B4B5"))
                mBinding.ivDelete.visibility=View.INVISIBLE
                mEditingState= EditingState.NORMAL
            }
            mBinding.recycleView.bindingAdapter.notifyDataSetChanged()
        }
        mBinding.ivArrow.setOnClickListener {
            findNavController().navigateUp()
        }
        mBinding.ivDelete.setOnClickListener {
            if (mSelectedModel.isEmpty())return@setOnClickListener
            //删除选中图片
            mLoadingView.show(mBinding.root)
            lifecycleScope.launch(Dispatchers.IO) {
                mSelectedModel.forEach{model->
                    FileManager.instance.removeFile(model.originalPath)
                    FileManager.instance.removeFile(model.thumbnailPath)
                }
                withContext(Dispatchers.Main){
                    mLoadingView.hide{
                        mViewModel.removeAll(mSelectedModel)
                        mSelectedModel.clear()
                        mBinding.recycleView.bindingAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

}