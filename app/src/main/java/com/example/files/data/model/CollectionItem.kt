package com.example.files.data.model

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import java.nio.file.Path

data class CollectionItem(
    @ColorRes val backgroundTint: Int,
    @DrawableRes val icon: Int,
    val name: String,
    val path: Path,
)
