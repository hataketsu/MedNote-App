package com.worker.landscape.util

import android.graphics.Color
import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.mikepenz.fontawesome_typeface_library.FontAwesome
import com.mikepenz.iconics.IconicsDrawable

/**
 * Created by hataketsu on 4/20/17.
 */
//fun ImageView.loadUrl(url: String?) {
//    if (!url.isNullOrEmpty())
//        Picasso.with(allListActivity).load(url).into(this)
//}

fun ImageView.setIcon(icon: FontAwesome.Icon,size: Int = 24,color: Int = Color.WHITE) {
    this.setImageDrawable(IconicsDrawable(context, icon).sizeDp(size).color(color))
}

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}
//
//fun Context.toast(message: CharSequence) =
//        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
//
//fun Context.toast(messageID: Int) =
//        Toast.makeText(this, this.getString(messageID), Toast.LENGTH_SHORT).show()
//
//fun Context.toast(messageID: Int, error: String) =
//        Toast.makeText(this, this.getString(messageID) + " : " + error, Toast.LENGTH_SHORT).show()
class DummyHolder(itemView: View?) : RecyclerView.ViewHolder(itemView)

