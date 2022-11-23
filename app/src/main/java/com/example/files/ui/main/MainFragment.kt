package com.example.files.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.files.R
import com.example.files.data.model.Category
import com.example.files.data.model.StorageItem
import com.example.files.domain.adapters.CategoriesAdapter
import com.example.files.domain.adapters.StorageAdapter
import com.example.files.domain.common.FileUtils
import com.example.files.domain.common.ShiftedDividerItemDecoration
import com.example.files.ui.category.CategoryActivity

class MainFragment : Fragment(R.layout.fragment_main) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(view.findViewById<RecyclerView>(R.id.rvLibraries)) {
            val categories = mutableListOf(
//                Category(R.drawable.ic_download, "Downloads", "", "downloads"),
                Category(R.drawable.ic_image, "Images", "", "image"),
                Category(R.drawable.ic_video, "Videos", "", "video"),
                Category(R.drawable.ic_audio, "Audio", "", "audio"),
//                Category(R.drawable.ic_file, "Documents", "", "application"),
            )

            val adapter = CategoriesAdapter(categories) {
                startActivity(Intent(requireActivity(), CategoryActivity::class.java).apply {
                    putExtra(CategoryActivity.EXTRA_TYPE, it.mime)
                    putExtra(CategoryActivity.EXTRA_TITLE, it.name)
                })
            }

            this.adapter = adapter

            addItemDecoration(ShiftedDividerItemDecoration(requireActivity(),
                LinearLayoutManager.VERTICAL,
                64))
        }

        with(view.findViewById<RecyclerView>(R.id.rvStorage)) {
            val storages = mutableListOf(StorageItem(R.drawable.ic_browse,
                "Internal Storage",
                android.os.Environment.getExternalStorageDirectory().toPath(),
                "${FileUtils.getSizeWithUnit(android.os.Environment.getExternalStorageDirectory().freeSpace)} free"))

            val adapter = StorageAdapter(storages)
            this.adapter = adapter

            addItemDecoration(ShiftedDividerItemDecoration(requireActivity(),
                LinearLayoutManager.VERTICAL,
                64))

        }
    }
}