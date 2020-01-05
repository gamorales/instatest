package com.gmorales.instatest.users.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.gmorales.instatest.R

import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Toast
import com.gmorales.instatest.core.RetrofitClient
import com.gmorales.instatest.core.isEmail
import com.gmorales.instatest.core.models.ErrorDTO
import com.gmorales.instatest.users.controllers.UserAPI
import com.gmorales.instatest.users.models.PasswordResponseDTO
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import kotlinx.android.synthetic.main.password_activity.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PasswordActivity : AppCompatActivity() {

    private var PRIVATE_MODE = 0
    private val PREF_NAME = "com.gmorales.instatest.prefs"
    private val EMAIL = "instagram-email"
    private val CODE = "instagram-code"
    var sharedPref: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        supportActionBar?.hide()
        setContentView(R.layout.password_activity)

        sharedPref = this.getSharedPreferences(PREF_NAME, PRIVATE_MODE)

        setupUI()
    }

    private fun setupUI() {
        password_btn.setOnClickListener {
            val email = resetpass_email.text.toString().trim()

            if (email.isEmpty()) {
                resetpass_email.error = getString(R.string.email_required)
                resetpass_email.requestFocus()
                return@setOnClickListener
            }

            if (!email.isEmail()) {
                resetpass_email.error = getString(R.string.email_bad_format)
                resetpass_email.requestFocus()
                return@setOnClickListener
            }

            resetpassProgressBar.visibility = View.VISIBLE

            var service = RetrofitClient.instance?.create(UserAPI::class.java)
            var call: Call<PasswordResponseDTO>? = service?.forgotPassword(email)

            call?.enqueue(object: Callback<PasswordResponseDTO> {
                override fun onFailure(call: Call<PasswordResponseDTO>, t: Throwable) {
                    Log.e("API Error", t.message)
                }

                override fun onResponse(
                    call: Call<PasswordResponseDTO>,
                    response: Response<PasswordResponseDTO>
                ) {
                    resetpassProgressBar.visibility = View.GONE
                    if(response?.code()!=200) {
                        val gson = Gson()
                        val type = object : TypeToken<ErrorDTO>() {}.type
                        var errorResponse: ErrorDTO? = gson.fromJson(response.errorBody()!!.charStream(), type)

                        Toast.makeText(applicationContext, errorResponse?.detail, Toast.LENGTH_LONG).show()
                        Log.e("API Error", errorResponse?.detail)

                    } else {
                        if (response?.body()?.error!=null) {
                            Toast.makeText(
                                applicationContext,
                                response?.body()?.message,
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Toast.makeText(
                                applicationContext,
                                response?.body()?.success,
                                Toast.LENGTH_LONG
                            ).show()

                            val editor = sharedPref!!.edit()
                            editor.putString(CODE, response?.body()?.code.toString())
                            editor.putString(EMAIL, response?.body()?.email)
                            editor.apply()

                            val intent = Intent(applicationContext, PasswordResetActivity::class.java)
                            startActivity(intent)
                        }
                    }
                }
            })
        }
    }
}
