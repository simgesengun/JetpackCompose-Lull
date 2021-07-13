package com.example.lull

import android.graphics.drawable.Drawable

data class Wallpaper(var id : Int, var name : String, var drawableId : Int, var tags : List<String>, var artist : String? = null) {
}