package com.example.files.ui.browsing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.files.R
import com.example.files.data.model.FileItem
import com.example.files.domain.adapters.DirectoryAdapter
import com.example.files.domain.common.FileUtils
import com.example.files.domain.common.ShiftedDividerItemDecoration
import com.example.files.domain.common.openFile
import com.google.android.material.progressindicator.CircularProgressIndicator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.io.path.absolutePathString
import kotlin.io.path.isDirectory
import kotlin.properties.Delegates

class DirectoryFragment : Fragment() {
    private lateinit var folderPath: String
    private var showHiddenFiles by Delegates.notNull<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            folderPath = it.getString(FOLDER_PATH)!!
            showHiddenFiles = it.getBoolean(SHOW_HIDDEN_FILES)
        }

        setHasOptionsMenu(true)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.clear()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return inflater.inflate(R.layout.fragment_directory, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val directoryList = view.findViewById<RecyclerView>(R.id.dirList)
        val loader = view.findViewById<CircularProgressIndicator>(R.id.loader)

        val onItemClick: (FileItem) -> Unit = { item ->
            val pathName = item.path.absolutePathString()
            if (item.path.isDirectory()) {
                parentFragmentManager.beginTransaction().replace(R.id.directoryFragmentContainer,
                    newInstance(pathName, showHiddenFiles)).addToBackStack(pathName).commit()
            } else {
                openFile(requireContext(), item)
            }
        }

        directoryList.addItemDecoration(ShiftedDividerItemDecoration(requireContext(),
            LinearLayoutManager.VERTICAL,
            64))

        val emptyState = view.findViewById<LinearLayout>(R.id.emptyState)

        directoryList.visibility = View.GONE
        emptyState.visibility = View.GONE
        loader.visibility = View.VISIBLE

        lifecycleScope.launch(Dispatchers.IO) {
            var items = FileUtils.getFilesList(folderPath)

            if (!showHiddenFiles) {
                items = items.filter { !it.isHidden }
            }

            withContext(Dispatchers.Main) {
                if (items.isNotEmpty()) {
                    directoryList.adapter = DirectoryAdapter(items.toMutableList(), onItemClick)
                    directoryList.visibility = View.VISIBLE
                } else {
                    emptyState.visibility = View.VISIBLE
                    activity?.invalidateOptionsMenu()
                }

                loader.visibility = View.GONE

            }
        }
    }

    companion object {
        private const val FOLDER_PATH = "FOLDER_PATH"
        private const val SHOW_HIDDEN_FILES = "SHOW_HIDDEN_FILES"

        fun newInstance(folderPath: String, showHiddenFiles: Boolean) = DirectoryFragment().apply {
            arguments = Bundle().apply {
                putString(FOLDER_PATH, folderPath)
                putBoolean(SHOW_HIDDEN_FILES, showHiddenFiles)
            }
        }
    }
}