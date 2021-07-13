package com.example.lull

data class Wallpaper(var id : Int, var name : String, var drawableId : Int, var tags : List<String>, var artist : String? = null) {
}