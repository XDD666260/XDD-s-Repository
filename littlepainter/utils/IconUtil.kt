package com.example.littlepainter.utils

import com.example.littlepainter.R
import com.example.littlepainter.model.IconModel

fun getMenuIconModel():IconModel{
    return IconModel(
        IconType.DRAW_MENU,
        R.string.menu,
        IconState.SELECTED,
        R.color.light_red,
        R.color.light_red
    )
}
//左侧工具栏
fun getDrawToolIconModels():List<IconModel>{
    return listOf(
        IconModel(IconType.DRAW_MOVE, R.string.move),
        IconModel(IconType.DRAW_ERASER, R.string.eraser),
        IconModel(IconType.DRAW_BRUSH,R.string.brush),
        IconModel(IconType.DRAW_CURVE, R.string.curve),
        IconModel(IconType.DRAW_LINE_ARROW, R.string.line_arrow),
        IconModel(IconType.DRAW_CIRCLE, R.string.circle),
        IconModel(IconType.DRAW_TRIANGLE, R.string.triangle),
        IconModel(IconType.DRAW_RECTANGLE, R.string.rectangle),
        IconModel(IconType.DRAW_TEXT, R.string.text),
        IconModel(IconType.DRAW_LINE, R.string.line),
        IconModel(IconType.DRAW_BEZEL, R.string.bezel),
        IconModel(IconType.DRAW_LOCATION, R.string.location)
    )
}
//顶部工具栏
fun getHomeMenuIconModels():List<IconModel>{
    return listOf(
        IconModel(IconType.MENU_SAVE, R.string.save),
        IconModel(IconType.MENU_DOWNLOAD, R.string.download),
        IconModel(IconType.MENU_SHARE, R.string.share),
        IconModel(IconType.MENU_PICTURE, R.string.picture),
        IconModel(IconType.MENU_ACCOUNT, R.string.account),
        IconModel(IconType.MENU_LAYER, R.string.layer),
    )
}

//右侧
fun getOperationToolIconModels():List<IconModel>{
    return listOf(
        IconModel(IconType.OPERATION_UNDO, R.string.undo),
        IconModel(IconType.OPERATION_DELETE, R.string.garbage),
        IconModel(IconType.OPERATION_PENCIL, R.string.pencil),
        IconModel(IconType.OPERATION_PALETTE, R.string.palette),
    )
}