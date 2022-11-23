package com.example.files.ui.browsing

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.example.files.R

class BrowsingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_browsing)

        setSupportActionBar(findViewById(R.id.topAppBar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = intent?.getStringExtra("name")

        val showHiddenFiles =
            PreferenceManager.getDefaultSharedPreferences(this).getBoolean("showHiddenFiles", false)

        intent.getStringExtra("path")?.let { path ->
            supportFragmentManager.beginTransaction()
                .replace(R.id.directoryFragmentContainer,
                    DirectoryFragment.newInstance(path, showHiddenFiles))
                .addToBackStack(path).commit()
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 1) {
            super.onBackPressed()
        } else {
            super.onSupportNavigateUp()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        if (supportFragmentManager.backStackEntryCount > 1) {
            supportFragmentManager.popBackStack()
            return true
        }

        return super.onSupportNavigateUp()
    }
}