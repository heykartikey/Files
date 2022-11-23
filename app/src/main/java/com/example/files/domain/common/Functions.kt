package com.example.files.domain.common

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.files.data.model.FileItem
import java.io.File
import java.nio.file.Paths
import java.util.*
import kotlin.io.path.absolutePathString

fun moveToTrash(file: File): Boolean {
    val targetName = ".trashed-${Date().toInstant().toEpochMilli()}-${file.name}"
    return file.renameTo(Paths.get(file.parent, targetName).toFile())
}

fun openFile(context: Context, item: FileItem) {
    context.startActivity(Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(Uri.parse(item.path.absolutePathString()),
            FileUtils.getMimeType(item.path.toFile()))
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    })
}

fun shareFile(context: Context, item: FileItem) {
    context.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
        putExtra(Intent.EXTRA_STREAM, Uri.parse(item.path.absolutePathString()))
        type = FileUtils.getMimeType(item.path.toFile())
    }, "Share"))
}