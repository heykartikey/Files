package com.example.files.domain.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.files.R
import com.example.files.data.model.Category

class CategoriesAdapter(
    val values: MutableList<Category>,
    private val onItemClick: (Category) -> Unit,
) : RecyclerView.Adapter<CategoriesAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView = itemView.findViewById<TextView>(R.id.tvName)!!
        val imageView = itemView.findViewById<ImageView>(R.id.ivIcon)!!
        val infoView = itemView.findViewById<TextView>(R.id.tvInfo)!!

        init {
            itemView.setOnClickListener { onItemClick(values[adapterPosition]) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_storage, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (icon, name, info) = values[position]

        with(holder) {
            textView.text = name
            infoView.text = info
            if (info.isEmpty()) {
                infoView.visibility = View.GONE
            }
            imageView.setImageResource(icon)
        }
    }

    override fun getItemCount() = values.size
}