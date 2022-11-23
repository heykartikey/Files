package com.example.files.domain.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.forEach
import androidx.recyclerview.widget.RecyclerView
import com.example.files.R
import com.example.files.data.model.FileItem
import com.example.files.domain.common.FileUtils
import com.example.files.domain.common.moveToTrash
import com.example.files.domain.common.shareFile
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import java.nio.file.Files
import java.text.SimpleDateFormat
import java.util.*
import kotlin.io.path.isDirectory
import kotlin.io.path.name

class DirectoryAdapter(
    val values: MutableList<FileItem>,
    private val onItemClick: (FileItem) -> Unit,
) : RecyclerView.Adapter<DirectoryAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_directory, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            val item = values[position]

            if (item.thumb != null) {
                holder.thumbnail.setImageBitmap(item.thumb)
                holder.thumbnail.visibility = View.VISIBLE
                holder.icon.visibility = View.GONE
            } else {
                holder.icon.setImageResource(item.icon)
                holder.icon.visibility = View.VISIBLE
                holder.thumbnail.visibility = View.GONE
            }
            holder.fileItemView.text = item.name
            holder.fileInfoView.text = SimpleDateFormat("MMM dd",
                Locale.getDefault()).format(Date.from(Files.getLastModifiedTime(item.path)
                .toInstant()))

            holder.btnMore.setOnClickListener { btnMore ->
                with(PopupMenu(btnMore.context, btnMore)) {
                    inflate(if (item.path.isDirectory()) R.menu.folder_dropdown else R.menu.file_dropdown)

                    this.menu.forEach { menuItem ->
                        if (item.name.startsWith(".trashed-")) {
                            if (menuItem.itemId == R.id.move_to_trash_action) {
                                menuItem.isVisible = false
                            } else if (menuItem.itemId == R.id.delete_action) {
                                menuItem.isVisible = true
                            }
                        } else if (!item.name.startsWith(".trashed-")) {
                            if (menuItem.itemId == R.id.move_to_trash_action) {
                                menuItem.isVisible = true
                            } else if (menuItem.itemId == R.id.delete_action) {
                                menuItem.isVisible = item.path.isDirectory()
                            }
                        }
                    }

                    setOnMenuItemClickListener { menuItem ->
                        when (menuItem.itemId) {
                            R.id.move_to_trash_action -> {
                                if (moveToTrash(item.path.toFile())) {
                                    values.removeAt(position)
                                    notifyItemRemoved(position)

                                    Snackbar.make(btnMore,
                                        "File moved to trash successfully",
                                        Snackbar.LENGTH_SHORT).show()
                                }
                            }
                            R.id.delete_action -> {
                                if (FileUtils.deleteFile(item.path)) {
                                    values.removeAt(position)
                                    notifyItemRemoved(position)

                                    Snackbar.make(btnMore,
                                        "${if (item.path.isDirectory()) "Folder" else "File"} has been deleted permanently",
                                        Snackbar.LENGTH_SHORT).show()
                                }
                            }

                            R.id.share_action -> shareFile(holder.itemView.context, item)

                            R.id.rename_action -> {
                                renameFile(holder.itemView.context, item, position)
                            }

                            else -> {}
                        }

                        return@setOnMenuItemClickListener true
                    }
                    show()
                }
            }
        } catch (e: Exception) {
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val fileItemView = itemView.findViewById<TextView>(R.id.tvName)!!
        val fileInfoView = itemView.findViewById<TextView>(R.id.tvInfo)!!
        val thumbnail = itemView.findViewById<ImageView>(R.id.ivThumbnail)!!
        val icon = itemView.findViewById<ImageView>(R.id.ivIcon)!!
        val btnMore = itemView.findViewById<MaterialButton>(R.id.btnMore)!!

        init {
            itemView.setOnClickListener { onItemClick(values[adapterPosition]) }
        }
    }

    fun renameFile(context: Context, item: FileItem, position: Int) {
        val tvName = EditText(context)

        MaterialAlertDialogBuilder(context).setTitle("Rename").setView(tvName)
            .setPositiveButton("Ok") { dialog, _ ->
                val newName = tvName.text.toString()
                if (newName.isNotBlank()) {
                    if (!item.path.isDirectory()) {
                        item.renameTo(newName + '.' + item.path.name.substringAfterLast('.'))
                    } else {
                        item.renameTo(newName)
                    }
                }
                dialog.dismiss()
                notifyItemChanged(position)
            }.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }.create().show()
    }
}