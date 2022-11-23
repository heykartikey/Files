package com.example.files

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.example.files.ui.main.MainFragment
import com.example.files.ui.search.SearchActivity
import com.example.files.ui.settings.SettingsActivity
import com.example.files.ui.trash.TrashActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity() {
    private lateinit var drawer: DrawerLayout

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Files)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(findViewById(R.id.topAppBar))
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setHomeActionContentDescription("Open Drawer Menu")

        drawer = findViewById(R.id.drawerLayout)
        findViewById<NavigationView>(R.id.drawerMenu).setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.actionTrash -> {
                    startActivity(Intent(this, TrashActivity::class.java))
                }
                R.id.actionSettings -> startActivity(Intent(this, SettingsActivity::class.java))
            }

            true
        }

        with(findViewById<BottomNavigationView>(R.id.bottom_navigation)) {
            setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.bn_home -> {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.home_fragment_container, MainFragment()).commit()
                    }
                    R.id.bn_storage_overview -> {
                        val storageSettingsIntent = Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS)
                        startActivity(storageSettingsIntent)
                    }
                }
                true
            }

            selectedItemId = R.id.bn_home
        }

        requestRequiredPermissions()
    }

    private fun requestRequiredPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val requestPermissionLauncher =
                registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}

            val permissionIntent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)

            if (!Environment.isExternalStorageManager()) {
                requestPermissionLauncher.launch(permissionIntent)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_mixin, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        drawer.open()
        return super.onSupportNavigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.actionSearch -> startActivity(Intent(this, SearchActivity::class.java))
        }

        return super.onOptionsItemSelected(item)
    }
}

