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
import com.gmorales.instatest.core.isPassword
import com.gmorales.instatest.core.models.ErrorDTO
import com.gmorales.instatest.users.controllers.UserAPI
import com.gmorales.instatest.users.models.PasswordResetResponseDTO
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
            val first_name = signup_first_name.text.toString().trim()
            val last_name = signup_last_name.text.toString().trim()
            val email = signup_email.text.toString().trim()
            val password = signup_password.text.toString().trim()
            val confirm_password = signup_confirm_password.text.toString().trim()

            if (first_name.isEmpty()) {
                signup_first_name.error = getString(R.string.first_name_required)
                signup_first_name.requestFocus()
                return@setOnClickListener
            }

            if (last_name.isEmpty()) {
                signup_last_name.error = getString(R.string.last_name_required)
                signup_last_name.requestFocus()
                return@setOnClickListener
            }

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

            if (password.isPassword()) {
                signup_password.error = getString(R.string.password_bad_format)
                signup_password.requestFocus()
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

            if (confirm_password.isEmpty()) {
                signup_confirm_password.error = getString(R.string.password_required)
                signup_confirm_password.requestFocus()
                return@setOnClickListener
            }

            if (confirm_password.isPassword()) {
                signup_confirm_password.error = getString(R.string.password_bad_format)
                signup_confirm_password.requestFocus()
                return@setOnClickListener
            }

            if (confirm_password.length<8) {
                signup_confirm_password.error = getString(R.string.password_min_8)
                signup_confirm_password.requestFocus()
                return@setOnClickListener
            }

            if (!password.equals(confirm_password)) {
                signup_password.error = getString(R.string.password_not_equals)
                signup_confirm_password.error = getString(R.string.password_not_equals)
                signup_password.requestFocus()
                return@setOnClickListener
            }

            signupProgressBar.visibility = View.VISIBLE

            var service = RetrofitClient.instance?.create(UserAPI::class.java)
            var call: Call<SignUpResponseDTO>? = service?.signupAuth(
                first_name, last_name,
                email, password, confirm_password
            )

            call?.enqueue(object: Callback<SignUpResponseDTO> {
                override fun onFailure(call: Call<SignUpResponseDTO>, t: Throwable) {
                    Log.e("API Error", t.message)
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

                        Toast.makeText(applicationContext, errorResponse?.detail?.trim(), Toast.LENGTH_LONG).show()
                        Log.e("API Error", errorResponse?.detail?.trim())
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "${response.body()?.success} ${response.body()?.name}",
                            Toast.LENGTH_LONG
                        ).show()
                        Toast.makeText(applicationContext, getString(R.string.login_now), Toast.LENGTH_LONG).show()
                        val intent = Intent(applicationContext, LoginActivity::class.java)
                        startActivity(intent)
                    }
                }

            })
        }

    }
}
