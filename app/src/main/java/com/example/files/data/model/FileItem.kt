package com.example.files.data.model

import android.graphics.Bitmap
import androidx.annotation.DrawableRes
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.absolutePathString

data class FileItem(
    @DrawableRes val icon: Int,
    val name: String,
    val path: Path,
    val isHidden: Boolean,
    val thumb: Bitmap? = null,
) {
    fun renameTo(newName: String): Boolean {
        return path.toFile().renameTo(Paths.get(path.parent.absolutePathString(), newName).toFile())
    }
}
