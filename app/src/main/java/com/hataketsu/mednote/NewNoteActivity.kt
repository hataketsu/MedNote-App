package com.hataketsu.mednote

import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.mikepenz.fontawesome_typeface_library.FontAwesome
import com.mikepenz.iconics.IconicsDrawable
import kotlinx.android.synthetic.main.activity_new_note.*
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.defaultSharedPreferences
import org.jetbrains.anko.toast
import java.text.SimpleDateFormat
import java.util.*

open class NewNoteActivity : AppCompatActivity() {
    val db: SQLiteDatabase by lazy {
        SQLiteDatabase.openOrCreateDatabase(Environment.getExternalStorageDirectory().path + "/Drug/notes.db", null, null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_note)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(getString(R.string.new_note))
        initUI()
    }

    open fun initUI() {

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.new_note, menu)
        menu.findItem(R.id.save_action).icon = IconicsDrawable(this, FontAwesome.Icon.faw_floppy_o).color(Color.WHITE).actionBar()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.save_action -> save()
        }
        return super.onOptionsItemSelected(item)
    }

    open fun save() {
        var id = defaultSharedPreferences.getInt("new_id", 1000000)
        id++
        val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
        db.insert("notes",
                "id" to id,
                "title" to new_note_title.text.toString(),
                "content" to new_note_content.text.toString(),
                "created_at" to date,
                "updated_at" to date)
        toast("Saved!")
        db.execSQL("insert into note_fts select id, title, updated_at, content from notes where id=$id")
        toast("Indexed!")
        defaultSharedPreferences.edit().putInt("new_id", id).apply()
    }

}
