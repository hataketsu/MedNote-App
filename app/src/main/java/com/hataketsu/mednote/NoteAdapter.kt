package com.hataketsu.mednote;

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import android.view.ViewGroup
import com.worker.landscape.util.inflate
import kotlinx.android.synthetic.main.note_item.view.*
import org.jetbrains.anko.startActivity
import java.nio.charset.Charset

class NoteAdapter(val mainActivity: MainActivity, val notes: MutableMap<String, NoteModel>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun getItemCount() = notes.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val v = holder.itemView
        val id = notes.keys.toList()[position]
        val note = notes[id] as NoteModel
        v.note_title.text = hightlight(note.title, note.offsets, "1")
        v.note_date.text = note.updated_at
        v.note_content.text = hightlight(note.content, note.offsets, "3")

        v.click_layout.setOnLongClickListener {
            mainActivity.startActivity<EditNoteActivity>(
                    "id" to id,
                    "title" to note.title,
                    "content" to note.content
            )
            true
        }
    }

    private fun hightlight(text: String, offsets: String, column: String): SpannableString {
        val result = SpannableString(text)
        val list = offsets.split(" ")
        var i = 0
        while (i < list.size) {
            if (list[i].equals(column)) {
                val start = list[i + 2].toInt()
                val lenght = list[i + 3].toInt()
                result.setSpan(BackgroundColorSpan(Color.YELLOW), decode_utf8_offset(text, start), decode_utf8_offset(text, start + lenght), 0)
            }
            i += 4
        }
        return result
    }

    fun decode_utf8_offset(text: String, org_offset: Int): Int {
        val bytes = text.toByteArray(Charset.forName("utf-8"))
        println("bytes len: ${bytes.size}")
        var i = 0
        var l = 0
        while (i < bytes.size && i < org_offset) {
            val x = (bytes[i] + 256) % 256 //since byte is signed
            when {
                x > 0b11110000 -> i += 4
                x > 0b11100000 -> i += 3
                x > 0b11000000 -> i += 2
                else -> i += 1
            }
            l++
        }
        return l
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = object : RecyclerView.ViewHolder(parent.inflate(R.layout.note_item)) {}
}