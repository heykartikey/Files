package com.example.files.domain.common

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Size
import android.webkit.MimeTypeMap
import androidx.annotation.RequiresApi
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import com.example.files.R
import com.example.files.data.model.FileItem
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import kotlin.io.path.*

object FileUtils {

    @Suppress("DEPRECATION")
    private val rootInternal = Environment.getExternalStorageDirectory()

    object MIME {
        const val UNKNOWN_MIME = "unknown_mime"
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun searchWithFileName(context: Context, searchQuery: String): MutableList<FileItem> {
        val collection = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL)
        val projection = arrayOf(
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns._ID,
        )

        val result = mutableListOf<FileItem>()

        context.contentResolver.query(collection, projection, null, null, null)?.use {
            while (it.moveToNext()) {
                val name =
                    it.getStringOrNull(it.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME))
                        ?: continue
                val path = it.getStringOrNull(it.getColumnIndex(MediaStore.Files.FileColumns.DATA))
                    ?: continue

                if (!File(path).isHidden && !File(path).isDirectory && name.lowercase()
                        .contains(searchQuery.lowercase())
                ) {
                    val id: Long =
                        it.getLongOrNull(it.getColumnIndex(MediaStore.Images.ImageColumns._ID))
                            ?: continue
                    val uri = ContentUris.withAppendedId(collection, id)

                    var thumb: Bitmap? = null
                    try {
                        thumb = context.contentResolver.loadThumbnail(uri, Size(64, 64), null)
                    } catch (e: Exception) {
                    }

                    result.add(FileItem(R.drawable.ic_file, name, Path(path), false, thumb))
                }
            }
        }

        return result
    }

    fun getMimeType(file: File): String {
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(file.extension)
            ?: MIME.UNKNOWN_MIME
    }

    fun getFilesList(pathName: String?): List<FileItem> {
        return pathName?.let {
            File(pathName).listFiles()?.partition { it.isDirectory }?.let { (folders, files) ->
                folders.map {
                    FileItem(R.drawable.ic_folder, it.name, it.toPath(), it.isHidden)
                }.sortedBy { it.name.lowercase() } + files.map {
                    FileItem(R.drawable.ic_file, it.name, it.toPath(), it.isHidden)
                }.sortedBy { it.name.lowercase() }
            }
        } ?: listOf()
    }

    fun getSizeWithUnit(bytes: Long): String {
        val kilobytes = bytes / 1024
        if (kilobytes < 1) return "${bytes.toInt()} Bytes"

        val megabytes = kilobytes / 1024
        if (megabytes < 1) return "${kilobytes.toInt()} kB"

        val gigabytes = megabytes / 1024
        if (gigabytes < 1) return "${megabytes.toInt()} MB"

        val terabytes = gigabytes / 1024
        if (terabytes < 1) return "${gigabytes.toInt()} GB"

        return "${gigabytes.toInt()} TB"
    }

    fun getDeletedFiles(pathName: String): List<File> {
        return File(pathName).walkBottomUp().filterNot { it.isDirectory }
            .filter { it.name.startsWith(".trashed") }.toList()
    }

    private fun getNameOfDeleted(file: Path): String {
        val name = file.name.split('-')
        return name.slice(2..name.lastIndex).joinToString("-")
    }

    fun restoreItem(file: Path): Boolean {
        return file.toFile()
            .renameTo(Paths.get(file.parent.absolutePathString(), getNameOfDeleted(file)).toFile())
    }

    fun deleteFile(file: Path): Boolean {
        if (file.isDirectory()) {
            file.listDirectoryEntries().forEach(::deleteFile)
        }

        return file.deleteIfExists()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun getImages(context: Context): MutableList<FileItem> {
        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Images.Media.getContentUri("external")
        }

        val items = mutableListOf<FileItem>()

        context.contentResolver.query(collection,
            arrayOf(
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
            ),
            null,
            null,
            null)?.use {
            while (it.moveToNext()) {
                val id: Long =
                    it.getLongOrNull(it.getColumnIndex(MediaStore.Images.ImageColumns._ID))
                        ?: continue
                val uri = ContentUris.withAppendedId(collection, id)

                val data =
                    it.getStringOrNull(it.getColumnIndex(MediaStore.Images.ImageColumns.DATA))
                        ?: continue
                println(data)

                val thumb = context.contentResolver.loadThumbnail(uri, Size(64, 64), null)
                val name =
                    it.getStringOrNull(it.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME))
                        ?: continue
                items.add(FileItem(R.drawable.ic_file, name, Path(data), false, thumb))
            }
        }

        return items
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun getVideos(context: Context): MutableList<FileItem> {
        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Video.Media.getContentUri("external")
        }

        val items = mutableListOf<FileItem>()

        context.contentResolver.query(collection,
            arrayOf(
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DATA,
            ),
            null,
            null,
            null)?.use {
            while (it.moveToNext()) {
                val id: Long =
                    it.getLongOrNull(it.getColumnIndex(MediaStore.Video.VideoColumns._ID))
                        ?: continue
                val uri = ContentUris.withAppendedId(collection, id)
                val data = it.getStringOrNull(it.getColumnIndex(MediaStore.Video.VideoColumns.DATA))
                    ?: continue

                val thumb = context.contentResolver.loadThumbnail(uri, Size(64, 64), null)
                val name =
                    it.getStringOrNull(it.getColumnIndex(MediaStore.Video.VideoColumns.DISPLAY_NAME))
                        ?: continue
                items.add(FileItem(R.drawable.ic_file, name, Path(data), false, thumb))
            }
        }

        return items
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun getAudios(context: Context): MutableList<FileItem> {
        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Audio.Media.getContentUri("external")
        }

        val items = mutableListOf<FileItem>()

        context.contentResolver.query(collection,
            arrayOf(MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DATA),
            null,
            null,
            null)?.use {
            while (it.moveToNext()) {
                val id: Long =
                    it.getLongOrNull(it.getColumnIndex(MediaStore.Audio.Media._ID)) ?: continue
                val uri = ContentUris.withAppendedId(collection, id)
                var thumb: Bitmap? = null
                try {
                    thumb = context.contentResolver.loadThumbnail(uri, Size(64, 64), null)
                } catch (e: Exception) {
                }

                val data = it.getStringOrNull(it.getColumnIndex(MediaStore.Audio.AudioColumns.DATA))
                    ?: continue

                val name =
                    it.getStringOrNull(it.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME))
                        ?: continue
                items.add(FileItem(R.drawable.ic_audio, name, Path(data), false, thumb))
            }
        }

        return items
    }
}