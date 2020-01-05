package com.gmorales.instatest.users.views

import android.content.Intent
import com.gmorales.instatest.R

import android.content.SharedPreferences

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.gmorales.instatest.MainActivity

class LoginActivity : AppCompatActivity() {
    private var PRIVATE_MODE = 0
    private val PREF_NAME = "com.gmorales.instatest.prefs"
    private val LOGGED = "instagram-logged"
    private val NAME = "instagram-name"
    private val EMAIL = "instagram-email"
    var sharedPref: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        supportActionBar?.hide()
        setContentView(R.layout.login_activity)

    }
}
