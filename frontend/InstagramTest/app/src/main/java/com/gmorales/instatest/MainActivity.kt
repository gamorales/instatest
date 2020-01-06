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
import com.gmorales.instatest.users.views.LoginActivity
import com.gmorales.instatest.users.views.ProfileFragment
import kotlinx.android.synthetic.main.main_activity.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var PRIVATE_MODE = 0
    private val PREF_NAME = "com.gmorales.instatest.prefs"
    private val TOKEN = "instagram-token"
    private val REFRESH = "instagram-refresh-token"
    private val LOGGED = "instagram-logged"
    private val ID= "instagram-id"
    private val FIRST_NAME = "instagram-first-name"
    private val LAST_NAME = "instagram-last-name"
    private val EMAIL = "instagram-email"
    private val PHOTO = "instagram-profile-photo"
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
        menuInflater.inflate(R.menu.action_bar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mnu_logout -> {
                val editor = sharedPref!!.edit()
                editor.putInt(ID, 0)
                editor.putString(EMAIL, "")
                editor.putString(FIRST_NAME, "")
                editor.putString(LAST_NAME, "")
                editor.putString(PHOTO, "")
                editor.putString(TOKEN, "")
                editor.putString(REFRESH, "")
                editor.putBoolean(LOGGED, false)
                editor.apply()

                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
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
