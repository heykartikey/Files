package com.example.files.ui.search

import android.os.Build
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.files.R
import com.example.files.domain.adapters.DirectoryAdapter
import com.example.files.domain.common.FileUtils
import com.example.files.domain.common.openFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        setSupportActionBar(findViewById(R.id.topAppBar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val searchBox = findViewById<EditText>(R.id.etSearchBox)
        val clearButton = findViewById<ImageButton>(R.id.btnClear)

        val searchResult = findViewById<RecyclerView>(R.id.searchResult)
        val adapter = DirectoryAdapter(mutableListOf()) {
            openFile(this, it)
        }

        searchResult.adapter = adapter

        searchBox.setOnEditorActionListener { view, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    lifecycleScope.launch(Dispatchers.IO) {
                        val items =
                            FileUtils.searchWithFileName(this@SearchActivity, view.text.toString())

                        withContext(Dispatchers.Main) {
                            adapter.values.clear()
                            adapter.values.addAll(items)
                            adapter.notifyDataSetChanged()
                        }
                    }
                    true
                }

                else -> true
            }
        }

        clearButton.setOnClickListener { searchBox.text.clear() }
    }
}