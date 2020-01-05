package com.gmorales.instatest.users.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Toast
import com.gmorales.instatest.R
import com.gmorales.instatest.core.RetrofitClient
import com.gmorales.instatest.core.isEmail
import com.gmorales.instatest.core.models.ErrorDTO
import com.gmorales.instatest.users.controllers.UserAPI
import com.gmorales.instatest.users.models.SignUpResponseDTO
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.sign_up_activity.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        supportActionBar?.hide()
        setContentView(R.layout.sign_up_activity)

        setupUI()
    }

    private fun setupUI() {
        signup_btn.setOnClickListener {
            val email = signup_email.text.toString().trim()
            val password = signup_password.text.toString().trim()

            if (email.isEmpty()) {
                signup_email.error = getString(R.string.email_required)
                signup_email.requestFocus()
                return@setOnClickListener
            }

            if (!email.isEmail()) {
                signup_email.error = getString(R.string.email_bad_format)
                signup_email.requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                signup_password.error = getString(R.string.password_required)
                signup_password.requestFocus()
                return@setOnClickListener
            }

            if (password.length<8) {
                signup_password.error = getString(R.string.password_min_8)
                signup_password.requestFocus()
                return@setOnClickListener
            }

            signupProgressBar.visibility = View.VISIBLE

            var service = RetrofitClient.instance?.create(UserAPI::class.java)
            var call: Call<SignUpResponseDTO>? = service?.signupAuth(email, password)

            call?.enqueue(object: Callback<SignUpResponseDTO> {
                override fun onFailure(call: Call<SignUpResponseDTO>, t: Throwable) {
                    Log.e("ERROR", t.message)
                }

                override fun onResponse(
                    call: Call<SignUpResponseDTO>,
                    response: Response<SignUpResponseDTO>
                ) {
                    signupProgressBar.visibility = View.GONE
                    if (response?.code() != 200) {

                        val gson = Gson()
                        val type = object : TypeToken<ErrorDTO>() {}.type
                        var errorResponse: ErrorDTO? = gson.fromJson(response.errorBody()!!.charStream(), type)

                        Toast.makeText(applicationContext, errorResponse?.detail, Toast.LENGTH_LONG).show()
                        Log.e("ALGO", errorResponse?.detail)
                    } else {
                        Toast.makeText(applicationContext, getString(R.string.login_now), Toast.LENGTH_LONG).show()
                        Log.e("ALGO", "ALGO")
                        val intent = Intent(applicationContext, LoginActivity::class.java)
                        startActivity(intent)
                    }
                }

            })
        }

    }
}
