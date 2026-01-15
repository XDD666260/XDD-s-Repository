package com.example.littlepainter.ui.fragment.account.album

import com.example.littlepainter.ui.fragment.home.file.FileManager

fun loadPhotoModels():ArrayList<PhotoModel>{
    val models= arrayListOf<PhotoModel>()
    FileManager.instance.loadThumbnailImage().forEach { thumbnailPath->
        val index=thumbnailPath.lastIndexOf("/")
        val name=thumbnailPath.substring(index+1)
        val model= PhotoModel(thumbnailPath, FileManager.instance.getOriginPathForPath(name))
        models.add(model)
    }
    return models
}