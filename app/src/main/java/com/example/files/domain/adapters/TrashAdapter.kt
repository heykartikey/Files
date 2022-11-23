package com.example.files.domain.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.files.R
import com.example.files.data.model.FileItem
import java.nio.file.Files
import java.text.SimpleDateFormat
import java.util.*

class TrashAdapter(
    val values: MutableList<FileItem>,
    val selectedItems: MutableList<Int>,
) : RecyclerView.Adapter<TrashAdapter.ViewHolder>() {

    val anySelected = MutableLiveData(false)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_trash, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.fileItemView.text = item.name
        holder.fileInfoView.text = SimpleDateFormat("MMM dd",
            Locale.getDefault()).format(Date.from(Files.getLastModifiedTime(item.path).toInstant()))

        holder.checkBox.setOnCheckedChangeListener(null)
        holder.checkBox.isChecked = selectedItems.contains(position)

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedItems.add(position)
                if (anySelected.value == false) {
                    anySelected.value = true
                }
            } else {
                selectedItems.remove(position)
                if (selectedItems.isEmpty()) {
                    anySelected.value = false
                }
            }
        }

        holder.itemView.setOnClickListener {
            holder.checkBox.isChecked = !holder.checkBox.isChecked
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val fileItemView = itemView.findViewById<TextView>(R.id.tvName)!!
        val fileInfoView = itemView.findViewById<TextView>(R.id.tvInfo)!!
        val checkBox = itemView.findViewById<CheckBox>(R.id.cbSelect)!!
    }
}