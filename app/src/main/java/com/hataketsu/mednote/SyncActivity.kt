package com.hataketsu.mednote

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.github.kittinunf.fuel.httpPost
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_sync.*
import org.jetbrains.anko.AnkoAsyncContext
import org.jetbrains.anko.db.delete
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.select
import org.jetbrains.anko.defaultSharedPreferences
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.net.ConnectException
import java.net.URL

class SyncActivity : AppCompatActivity() {
    val db: SQLiteDatabase by lazy {
        SQLiteDatabase.openOrCreateDatabase(Environment.getExternalStorageDirectory().path + "/Drug/notes.db", null, null)
    }
    val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sync)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.sync_notes)
        sync_button.setOnClickListener {
            startSync()
        }
    }

    private fun startSync() {
        val url = defaultSharedPreferences.getString("url","http://192.168.43.207:8000")
        doAsync {
            try {
                ping(url)

                uploadNotes(url)

                uiThread {
                    sync_log.append("Uploading notes timestamps\n")
                }

                uiThread {
                    sync_log.append("Result: \n")
                }


            } catch (e: ConnectException) {
                uiThread {
                    sync_log.append("Cannot connect\n${e.message}\n")
                }
                return@doAsync
            }


        }
    }

    private fun AnkoAsyncContext<SyncActivity>.uploadNotes(url: String) {
        uiThread {
            sync_log.append("Synchronizing notes\n")
        }

        val notes = mutableListOf<NoteModel>()
        db.select("notes",
                "id", "title", "content", "updated_at", "created_at","deleted_at").exec {
            while (moveToNext())
                notes.add(NoteModel(getString(0) ?: "", getString(1) ?: "", getString(2) ?: "", getString(4) ?: "", getString(3) ?: "", getString(5) ?:"", ""))
        }
        val result = "$url/api/notes".httpPost(listOf("notes" to gson.toJson(notes))).responseString().third
        uiThread {
            sync_log.append("Result: $result\n")
        }

        db.delete("notes")
        db.delete("note_fts")
        notes.clear()
        notes.addAll(gson.fromJson(URL(url + "/api/notes").readText(), Array<NoteModel>::class.java))
        for (note in notes) {
            println(note)
            db.insert("notes",
                    "id" to note.id,
                    "title" to note.title,
                    "content" to note.content,
                    "created_at" to note.created_at,
                    "updated_at" to note.updated_at)
            db.insert("note_fts",
                    "id" to note.id,
                    "title" to note.title,
                    "content" to note.content,
                    "updated_at" to note.updated_at
            )
        }

        uiThread {
            sync_log.append("Synced\n")
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun AnkoAsyncContext<SyncActivity>.ping(url: String) {
        val respone = URL(url + "/api/ping").readText()
        uiThread {
            sync_log.append(respone + "\n")
        }
    }

}
