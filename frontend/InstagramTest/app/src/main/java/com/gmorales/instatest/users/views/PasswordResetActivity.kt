package com.gmorales.instatest.users.views

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.core.view.isVisible
import com.gmorales.instatest.R
import com.gmorales.instatest.core.RetrofitClient
import com.gmorales.instatest.core.models.ErrorDTO
import com.gmorales.instatest.users.controllers.UserAPI
import com.gmorales.instatest.users.models.PasswordResponseDTO
import com.gmorales.instatest.users.models.SignUpResponseDTO
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.password_reset_activity.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PasswordResetActivity : AppCompatActivity() {

    var change: Boolean = false
    private var PRIVATE_MODE = 0
    private val PREF_NAME = "com.gmorales.instatest.prefs"
    private val EMAIL = "instagram-email"
    var sharedPref: SharedPreferences? = null
    lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        supportActionBar?.hide()
        setContentView(R.layout.password_reset_activity)

        sharedPref = this.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        email = sharedPref!!.getString(EMAIL, "").toString()

        setupUI()
    }

    private fun setupUI() {
        password_save_btn.setOnClickListener {
            val code = txtCode.text.toString().trim()
            val password = txtNewPass.text.toString().trim()
            val confirm_password = txtConfirmPass.text.toString().trim()

            if (code.isEmpty()) {
                txtCode.error = getString(R.string.code_required)
                txtCode.requestFocus()
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                txtNewPass.error = getString(R.string.password_required)
                txtNewPass.requestFocus()
                return@setOnClickListener
            }
            if (confirm_password.isEmpty()) {
                txtConfirmPass.error = getString(R.string.password_required)
                txtConfirmPass.requestFocus()
                return@setOnClickListener
            }
            if (password.length<8) {
                txtNewPass.error = getString(R.string.password_min_8)
                txtNewPass.requestFocus()
                return@setOnClickListener
            }
            if (confirm_password.length<8) {
                txtConfirmPass.error = getString(R.string.password_min_8)
                txtConfirmPass.requestFocus()
                return@setOnClickListener
            }
            if (!confirm_password.equals(password)) {
                txtConfirmPass.error = getString(R.string.password_different)
                txtConfirmPass.requestFocus()
                return@setOnClickListener
            }

            codepassProgressBar.visibility = View.VISIBLE

            var service = RetrofitClient.instance?.create(UserAPI::class.java)
            var call: Call<PasswordResponseDTO>? = service?.checkCode(email, code)

            call?.enqueue(object: Callback<PasswordResponseDTO> {
                override fun onFailure(call: Call<PasswordResponseDTO>, t: Throwable) {
                    Log.e("ERROR", t.message)
                }

                override fun onResponse(
                    call: Call<PasswordResponseDTO>,
                    response: Response<PasswordResponseDTO>
                ) {
                    if(response?.code()!=200) {
                        val gson = Gson()
                        val type = object : TypeToken<ErrorDTO>() {}.type
                        var errorResponse: ErrorDTO? = gson.fromJson(response.errorBody()!!.charStream(), type)

                        Toast.makeText(applicationContext, errorResponse?.detail, Toast.LENGTH_LONG).show()
                        Log.e("ALGO", errorResponse?.detail)

                        codepassProgressBar.visibility = View.GONE

                    } else {
                        var call2: Call<SignUpResponseDTO>? = service?.resetPassword(email, code, password)

                        call2?.enqueue(object: Callback<SignUpResponseDTO> {
                            override fun onFailure(call: Call<SignUpResponseDTO>, t: Throwable) {
                                Log.e("ERROR", t.message)
                            }

                            override fun onResponse(
                                call: Call<SignUpResponseDTO>,
                                response: Response<SignUpResponseDTO>
                            ) {
                                codepassProgressBar.visibility = View.GONE
                                if(response?.code()!=200) {
                                    val gson = Gson()
                                    val type = object : TypeToken<ErrorDTO>() {}.type
                                    var errorResponse: ErrorDTO? =
                                        gson.fromJson(response.errorBody()!!.charStream(), type)

                                    Toast.makeText(
                                        applicationContext,
                                        errorResponse?.detail,
                                        Toast.LENGTH_LONG
                                    ).show()
                                    Log.e("ALGO", errorResponse?.detail)
                                } else {
                                    Toast.makeText(
                                        applicationContext,
                                        getString(R.string.password_successfull),
                                        Toast.LENGTH_LONG
                                    ).show()

                                    val intent = Intent(applicationContext, LoginActivity::class.java)
                                    startActivity(intent)
                                }
                            }

                        })
                    }

                }

            })

            if (!change) {
                tv_text_ok.text =  getString(R.string.yeah)
                tv_text_extra.text =  getString(R.string.password_long_text)

                txtCode.isVisible = false
                txtNewPass.isVisible = false
                txtConfirmPass.isVisible = false

                change = true
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onBackPressed() {
        // super.onBackPressed()
        Log.i("BACK", "NO")
    }
}
