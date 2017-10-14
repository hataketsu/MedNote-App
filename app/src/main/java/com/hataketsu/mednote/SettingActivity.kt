package com.hataketsu.mednote

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_setting.*
import org.jetbrains.anko.defaultSharedPreferences
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import java.net.URL

class SettingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.setting)
        initUI()

    }

    private fun initUI() {
        val url = defaultSharedPreferences.getString("url", "http://192.168.43.207:8000")
        setting_url.setText(url)
        setting_test_connect.setOnClickListener {
            doAsync {
                try {
                    URL(setting_url.text.toString() + "/api/ping").readText()
                } catch (e: Exception) {
                    uiThread {
                        toast("Cannot connect " + e.message)
                    }
                    e.printStackTrace()
                    return@doAsync
                }
                defaultSharedPreferences.edit().putString("url", setting_url.text.toString()).apply()
                uiThread {
                    toast("Connected!")
                }
            }

        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }
}
