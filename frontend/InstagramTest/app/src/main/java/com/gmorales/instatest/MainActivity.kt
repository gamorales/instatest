package com.gmorales.instatest

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import androidx.fragment.app.Fragment
import com.gmorales.instatest.core.IOnBackPressed
import com.gmorales.instatest.posts.views.HomeFragment
//import com.gmorales.instatest.tips.views.TipsFragment
//import com.gmorales.instatest.vips.views.VIPFragment
import com.gmorales.instatest.users.views.LoginActivity
import com.gmorales.instatest.users.views.ProfileFragment
//import com.gmorales.instatest.users.views.SettingsFragment
import kotlinx.android.synthetic.main.main_activity.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var PRIVATE_MODE = 0
    private val PREF_NAME = "com.gmorales.instatest.prefs"
    private val PREMIUM = "golden-play-premium"
    var sharedPref: SharedPreferences? = null

    override fun onBackPressed() {
        if (this.title.equals(getString(R.string.tipsters_tips))) {
            val fragment = this.supportFragmentManager.findFragmentById(R.id.main_container)
            (fragment as? IOnBackPressed)?.onBackPressed()?.not()?.let {
                super.onBackPressed()
            }
        } else {
            val fragment = ProfileFragment.newInstance(this)
            openFragment(fragment)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        sharedPref = this.getSharedPreferences(PREF_NAME, PRIVATE_MODE)

        bottomnav.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.mnu_profile-> {
                    val fragment = ProfileFragment.newInstance(this)
                    openFragment(fragment)
                    true
                }
                R.id.mnu_home -> {
                    val fragment = HomeFragment.newInstance(this)
                    openFragment(fragment)
                    true
                }
                else -> false
            }
        }

        bottomnav.selectedItemId = R.id.mnu_home
    }

    private fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.main_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        // menuInflater.inflate(R.menu.mnu_main, menu)
        return true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return true
    }

    companion object {
        fun getLaunchIntent(from: Context) = Intent(from, LoginActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }

    }
}
