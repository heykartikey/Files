package com.example.files.ui.category

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.files.R
import com.example.files.domain.adapters.DirectoryAdapter
import com.example.files.domain.common.FileUtils
import com.example.files.domain.common.openFile
import com.google.android.material.progressindicator.CircularProgressIndicator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CategoryActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_TYPE = "extra_type"
        const val EXTRA_TITLE = "extra_title"
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        setSupportActionBar(findViewById(R.id.topAppBar))
        supportActionBar?.title = intent?.getStringExtra(EXTRA_TITLE)!!
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val type = intent?.getStringExtra(EXTRA_TYPE)!!

        val recyclerView = findViewById<RecyclerView>(R.id.rv)
        val loader = findViewById<CircularProgressIndicator>(R.id.loader)

        recyclerView.visibility = View.GONE
        loader.visibility = View.VISIBLE

        lifecycleScope.launch(Dispatchers.IO) {
            val items = when (type) {
                "image" -> FileUtils.getImages(this@CategoryActivity)
                "video" -> FileUtils.getVideos(this@CategoryActivity)
                "audio" -> FileUtils.getAudios(this@CategoryActivity)
                else -> mutableListOf()
            }

            withContext(Dispatchers.Main) {
                recyclerView.adapter = DirectoryAdapter(items) {
                    openFile(this@CategoryActivity, it)
                }

                recyclerView.visibility = View.VISIBLE
                loader.visibility = View.GONE
            }
        }
    }
}