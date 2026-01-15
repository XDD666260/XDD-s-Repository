package com.example.littlepainter.ui.fragment.account.album

data class PhotoModel (
    val thumbnailPath:String,
    val originalPath:String,
    var selectSate: SelectState = SelectState.NORMAL
)



