package com.example.files.ui.trash

import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.files.R
import com.example.files.data.model.FileItem
import com.example.files.domain.adapters.TrashAdapter
import com.example.files.domain.common.FileUtils
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TrashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trash)

        setSupportActionBar(findViewById(R.id.topAppBar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val trashList = findViewById<RecyclerView>(R.id.listTrashFiles)
        val adapter = TrashAdapter(mutableListOf(), mutableListOf())
        trashList.adapter = adapter

        val loader = findViewById<CircularProgressIndicator>(R.id.loader)
        val emptyState = findViewById<LinearLayout>(R.id.trashEmptyState)
        val bottomBar = findViewById<BottomAppBar>(R.id.bottomBar)

        emptyState.visibility = View.GONE
        trashList.visibility = View.GONE
        loader.visibility = View.VISIBLE
        bottomBar.visibility = View.GONE

        lifecycleScope.launch(Dispatchers.IO) {
            val items =
                FileUtils.getDeletedFiles(Environment.getExternalStorageDirectory().absolutePath)

            withContext(Dispatchers.Main) {
                if (items.isNotEmpty()) {
                    adapter.values.clear()
                    adapter.values.addAll(items.map {
                        FileItem(R.drawable.ic_file, it.name, it.toPath(), false)
                    })

                    adapter.notifyItemRangeChanged(0, items.size)

                    trashList.visibility = View.VISIBLE
                    bottomBar.visibility = View.VISIBLE
                } else {
                    emptyState.visibility = View.VISIBLE
                }
                loader.visibility = View.GONE
            }
        }

        val btnRestore = findViewById<Button>(R.id.btnRestore)
        val btnDelete = findViewById<Button>(R.id.btnDelete)

        adapter.anySelected.observe(this) {
            btnRestore.isEnabled = it!!
            btnDelete.isEnabled = it
        }

        btnRestore.setOnClickListener { btn ->
            val items = adapter.selectedItems.map { it to adapter.values[it] }
            var countRestored = 0

            lifecycleScope.launch(Dispatchers.IO) {
                items.forEach {
                    val restored = FileUtils.restoreItem(it.second.path)
                    withContext(Dispatchers.Main) {
                        if (restored) {
                            countRestored++
                            adapter.values.removeAt(it.first)
                            adapter.notifyItemRemoved(it.first)
                        }
                    }
                }
            }.invokeOnCompletion {
                    if (it == null) {
                        Snackbar.make(btn,
                            "$countRestored of ${items.size} items restored",
                            Snackbar.LENGTH_SHORT).show()
                    }
                }

        }

        btnDelete.setOnClickListener { btn ->
            val items = adapter.selectedItems.map { it to adapter.values[it] }

            MaterialAlertDialogBuilder(this).setTitle("Delete Permanently")
                .setMessage("Are you sure you want to delete ${items.size} file${if (items.size > 1) "s" else ""} permanently")
                .setPositiveButton("Delete") { _, _ ->
                    var countDeleted = 0

                    lifecycleScope.launch(Dispatchers.IO) {
                        items.forEach {
                            val deleted = FileUtils.deleteFile(it.second.path)
                            withContext(Dispatchers.Main) {
                                if (deleted) {
                                    countDeleted++
                                    adapter.values.removeAt(it.first)
                                    adapter.notifyItemRemoved(it.first)
                                }
                            }
                        }
                    }.invokeOnCompletion {
                            if (it == null) {
                                Snackbar.make(btn,
                                    "$countDeleted of ${items.size} items deleted",
                                    Snackbar.LENGTH_SHORT).show()
                            }
                        }
                }.setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }.create().show()
        }
    }
}