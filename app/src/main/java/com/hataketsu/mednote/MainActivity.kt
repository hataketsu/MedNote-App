package com.hataketsu.mednote

import android.Manifest
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import com.mikepenz.fontawesome_typeface_library.FontAwesome
import com.mikepenz.iconics.IconicsDrawable
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.jetbrains.anko.db.select
import org.jetbrains.anko.startActivity


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    val notes = mutableMapOf<String, NoteModel>()
    var query = ""

    val db: SQLiteDatabase by lazy {
        SQLiteDatabase.openOrCreateDatabase(Environment.getExternalStorageDirectory().path + "/Drug/notes.db", null, null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        val toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.setDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)
        allNotes.adapter = NoteAdapter(this, notes)
        askPermission()
    }

    private fun askPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 134)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        askPermission()
    }

    override fun onResume() {
        super.onResume()
        if (query.isNullOrEmpty())
            listAll()
        else
            search(query)
    }

    override fun onBackPressed() {
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        val searchItem = menu.findItem(R.id.app_bar_search)
        searchItem.icon = IconicsDrawable(this, FontAwesome.Icon.faw_search).color(Color.WHITE).actionBar()
        val searchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                search(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean = true

        })
        return true
    }

    private fun search(query: String) {
        this.query = query
        notes.clear()
        db.select("note_fts",
                "id", "title", "content", "updated_at", "offsets(note_fts)").whereArgs("note_fts match '$query'").exec {
            while (moveToNext())
                notes[getString(0)] =
                        NoteModel(getString(0) ?: "", getString(1) ?: "", getString(2) ?: "", "", getString(3) ?: "", "", getString(4) ?: "")
        }
        allNotes.adapter.notifyDataSetChanged()
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_new_note -> startActivity<NewNoteActivity>()
            R.id.nav_sync -> startActivity<SyncActivity>()
            R.id.nav_browse -> listAll()
            R.id.nav_setting -> startActivity<SettingActivity>()
        }
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    private fun listAll() {
        query = ""
        notes.clear()
        db.select("notes",
                "id", "title", "content", "updated_at", "deleted_at").whereArgs("deleted_at is null").exec {
            while (moveToNext()) {
                notes[getString(0)] =
                        NoteModel(getString(0) ?: "", getString(1) ?: "", getString(2) ?: "", "", getString(3) ?: "", "", "")
            }
        }
        allNotes.adapter.notifyDataSetChanged()
    }
}



