package com.example.files.data.model

import androidx.annotation.DrawableRes

data class Category(
    @DrawableRes val icon: Int,
    val name: String,
    var info: String,
    val mime: String,
)