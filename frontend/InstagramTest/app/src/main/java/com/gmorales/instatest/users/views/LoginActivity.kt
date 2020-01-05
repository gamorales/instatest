package com.gmorales.instatest.users.views

import android.content.Intent
import com.gmorales.instatest.R

import android.content.SharedPreferences

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.gmorales.instatest.MainActivity
import com.gmorales.instatest.core.isEmail
import com.gmorales.instatest.core.RetrofitClient
import com.gmorales.instatest.users.controllers.UserAPI
import com.gmorales.instatest.users.models.LoginResponseDTO
import com.gmorales.instatest.core.models.ErrorDTO


import com.gmorales.instatest.users.models.TokenDTO
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.login_activity.*

import retrofit2.Call
import retrofit2.Response
import retrofit2.Callback


class LoginActivity : AppCompatActivity() {
    private var PRIVATE_MODE = 0
    private val PREF_NAME = "com.gmorales.instatest.prefs"
    private val TOKEN = "instagram-token"
    private val REFRESH = "instagram-refresh-token"
    private val LOGGED = "instagram-logged"
    private val ID= "instagram-id"
    private val NAME = "instagram-name"
    private val EMAIL = "instagram-email"
    var sharedPref: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        supportActionBar?.hide()
        setContentView(R.layout.login_activity)

        sharedPref = this.getSharedPreferences(PREF_NAME, PRIVATE_MODE)

        var logged: Boolean = sharedPref!!.getBoolean(LOGGED, false)
        signin_email.setText(sharedPref!!.getString(EMAIL, "").toString())

        // If is logged, goes directly to main screen
        if (logged==true) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        setupUI()
    }

    private fun setupUI() {
        signin_btn.setOnClickListener {

            val email = signin_email.text.toString().trim()
            val password = signin_password.text.toString().trim()

            if (email.isEmpty()) {
                signin_email.error = getString(R.string.email_required)
                signin_email.requestFocus()
                return@setOnClickListener
            }

            if (!email.isEmail()) {
                signin_email.error = getString(R.string.email_bad_format)
                signin_email.requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                signin_password.error = getString(R.string.password_required)
                signin_password.requestFocus()
                return@setOnClickListener
            }

            if (password.length<8) {
                signin_password.error = getString(R.string.password_min_8)
                signin_password.requestFocus()
                return@setOnClickListener
            }

            loginProgressBar.visibility = View.VISIBLE

            var service = RetrofitClient.instance?.create(UserAPI::class.java)
            var call: Call<TokenDTO>? = service?.loginAuth(email, password)

            call?.enqueue(object: Callback<TokenDTO> {
                override fun onFailure(call: Call<TokenDTO>, t: Throwable) {
                    Log.e("ERROR", t.message)
                }

                override fun onResponse(
                    call: Call<TokenDTO>,
                    response: Response<TokenDTO>
                ) {
                    //loginProgressBar.visibility = View.GONE
                    if(response?.code()!=200) {
                        val gson = Gson()
                        val type = object : TypeToken<ErrorDTO>() {}.type
                        var errorResponse: ErrorDTO? = gson.fromJson(response.errorBody()!!.charStream(), type)

                        Toast.makeText(applicationContext, errorResponse?.detail, Toast.LENGTH_LONG).show()
                        Log.e("ALGO", errorResponse?.detail)
                    } else {
                        var accessToken: String = "instatest "+response?.body()?.access

                        var callUserData: Call<LoginResponseDTO>? = service?.userDataAuth(accessToken, email)

                        callUserData?.enqueue(object: Callback<LoginResponseDTO> {
                            override fun onFailure(call: Call<LoginResponseDTO>, t: Throwable) {
                                Log.e("ERROR", t.message)
                            }

                            override fun onResponse(
                                call: Call<LoginResponseDTO>,
                                responseUserData: Response<LoginResponseDTO>
                            ) {
                                loginProgressBar.visibility = View.GONE
                                if(responseUserData?.code()!=200) {
                                    val gson = Gson()
                                    val type = object : TypeToken<ErrorDTO>() {}.type
                                    var errorResponse: ErrorDTO? = gson.fromJson(
                                        responseUserData.errorBody()!!.charStream(),
                                        type
                                    )

                                    Toast.makeText(
                                        applicationContext,
                                        errorResponse?.detail,
                                        Toast.LENGTH_LONG
                                    ).show()
                                    Log.e("ALGO", errorResponse?.detail)
                                } else {
                                    Log.e("DATA", responseUserData?.body().toString())
                                    val editor = sharedPref!!.edit()
                                    editor.putString(
                                        NAME,
                                        "${responseUserData?.body()?.first_name} " +
                                        "${responseUserData?.body()?.last_name}"
                                    )
                                    editor.putString(ID, "${responseUserData?.body()?.id}")
                                    editor.putString(EMAIL, "${responseUserData?.body()?.email}")
                                    editor.putString(TOKEN, accessToken)
                                    editor.putString(REFRESH, response?.body()?.refresh)
                                    editor.apply()

                                    val intent = Intent(applicationContext, MainActivity::class.java)
                                    startActivity(intent)

                                }
                            }
                        })

                    }
                }
            })
        }

        tv_forgot_password.setOnClickListener {
            val intent = Intent(this, PasswordActivity::class.java)
            startActivity(intent)
        }

        tv_create_account.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        // super.onBackPressed()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onStart() {
        super.onStart()
    }

    fun showDialog(title: String, message: String?, view: View){

        val builder = AlertDialog.Builder(this)

        with(builder)
        {
            setTitle(title)
            setMessage("lerolero")
            setPositiveButton(android.R.string.ok) { dialog, which ->
                Toast.makeText(applicationContext,
                    android.R.string.yes, Toast.LENGTH_SHORT).show()
            }
            show()
        }
    }
}
