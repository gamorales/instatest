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
import com.gmorales.instatest.users.models.PasswordResetResponseDTO
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
    private val CODE = "instagram-code"
    var sharedPref: SharedPreferences? = null
    lateinit var email: String
    lateinit var code: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        supportActionBar?.hide()
        setContentView(R.layout.password_reset_activity)

        sharedPref = this.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        email = sharedPref!!.getString(EMAIL, "").toString()
        code = sharedPref!!.getString(CODE, "").toString()

        txtCode.setText(code)
        setupUI()
    }

    private fun setupUI() {
        password_save_btn.setOnClickListener {
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
            var call: Call<PasswordResetResponseDTO>? =
                service?.resetPassword(
                    email,
                    code.toInt(),
                    password,
                    confirm_password
                )

            call?.enqueue(object: Callback<PasswordResetResponseDTO> {
                override fun onFailure(call: Call<PasswordResetResponseDTO>, t: Throwable) {
                    Log.e("ERROR", t.message)
                }

                override fun onResponse(
                    call: Call<PasswordResetResponseDTO>,
                    response: Response<PasswordResetResponseDTO>
                ) {
                    if(response?.code()!=200) {
                        val gson = Gson()
                        val type = object : TypeToken<ErrorDTO>() {}.type
                        var errorResponse: ErrorDTO? = gson.fromJson(response.errorBody()!!.charStream(), type)

                        Toast.makeText(applicationContext, errorResponse?.detail, Toast.LENGTH_LONG).show()
                        Log.e("APIError", errorResponse?.detail)

                        codepassProgressBar.visibility = View.GONE

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
                                "${response.body()?.success}",
                                Toast.LENGTH_LONG
                            ).show()
                            Toast.makeText(applicationContext, getString(R.string.login_now), Toast.LENGTH_LONG).show()
                            val intent = Intent(applicationContext, LoginActivity::class.java)
                            startActivity(intent)
                        }
                        codepassProgressBar.visibility = View.GONE

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
