package com.hataketsu.mednote

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.mikepenz.fontawesome_typeface_library.FontAwesome
import com.mikepenz.iconics.IconicsDrawable
import kotlinx.android.synthetic.main.activity_new_note.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.db.delete
import org.jetbrains.anko.db.update
import org.jetbrains.anko.noButton
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton
import java.text.SimpleDateFormat
import java.util.*

class EditNoteActivity : NewNoteActivity() {
    val id by lazy { intent.getStringExtra("id") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setTitle(intent.getStringExtra("title"))
    }

    override fun initUI() {
        new_note_title.setText(intent.getStringExtra("title"))
        new_note_content.setText(intent.getStringExtra("content"))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.edit_note, menu)
        menu.findItem(R.id.save_action).icon = IconicsDrawable(this, FontAwesome.Icon.faw_floppy_o).color(Color.WHITE).actionBar()
        menu.findItem(R.id.delete_action).icon = IconicsDrawable(this, FontAwesome.Icon.faw_recycle).color(Color.WHITE).actionBar()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.save_action -> save()
            R.id.delete_action -> delete()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun delete() {
        alert("Delete this note?", "Confirm") {
            yesButton {
                db.update("notes", "deleted_at" to SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())).whereArgs("id = $id").exec()
                db.delete("note_fts", "id = $id")
                toast("Deleted!")
                finish()
            }
            noButton { }
        }.show()
    }

    override fun save() {
        val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
        db.update("notes",
                "title" to new_note_title.text.toString(),
                "content" to new_note_content.text.toString(),
                "updated_at" to date).
                whereArgs("id = $id").exec()
        toast("Saved!")
        db.update("note_fts",
                "title" to new_note_title.text.toString(),
                "content" to new_note_content.text.toString(),
                "updated_at" to date).whereArgs("id = $id").
                exec()
        toast("Indexed!")
    }
}
