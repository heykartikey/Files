package com.example.files.data.model

import androidx.annotation.DrawableRes
import java.nio.file.Path

data class StorageItem(
    @DrawableRes val icon: Int,
    val name: String,
    val path: Path,
    val info: String = "",
)
