package com.qhy040404.libraryonetap

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    fun buttonOpenWebView(view: View) {
        val intent = Intent(this, WebviewActivity::class.java)
        startActivity(intent)
    }

    fun buttonOpenSettings(view: View) {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

    fun buttonExit(view: View) {
        exitProcess(0)
    }

}