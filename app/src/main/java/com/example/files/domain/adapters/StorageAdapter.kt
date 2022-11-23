package com.example.files.domain.adapters

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.files.R
import com.example.files.data.model.StorageItem
import com.example.files.ui.browsing.BrowsingActivity
import kotlin.io.path.absolutePathString

class StorageAdapter(
    val items: MutableList<StorageItem>,
) : RecyclerView.Adapter<StorageAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView = itemView.findViewById<TextView>(R.id.tvName)!!
        val infoView = itemView.findViewById<TextView>(R.id.tvInfo)!!
        val imageView = itemView.findViewById<ImageView>(R.id.ivIcon)!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_storage, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.textView.text = item.name
        holder.imageView.setImageResource(item.icon)
        holder.infoView.text = item.info

        holder.itemView.setOnClickListener {
            Intent(it.context, BrowsingActivity::class.java).apply {
                putExtra("path", items[position].path.absolutePathString())
                putExtra("name", item.name)
            }.let { intent ->
                (it.context as Activity).startActivity(intent)
            }
        }
    }

    override fun getItemCount() = items.size
}