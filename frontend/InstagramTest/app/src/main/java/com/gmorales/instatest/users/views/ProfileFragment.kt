package com.gmorales.instatest.users.views

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

import com.gmorales.instatest.R
import com.gmorales.instatest.core.RetrofitClient
import com.gmorales.instatest.core.models.ErrorDTO
import com.gmorales.instatest.users.controllers.UserAPI
import com.gmorales.instatest.users.models.SignUpResponseDTO
import com.gmorales.instatest.users.models.TokenDTO
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileFragment : Fragment() {
    lateinit var mContext: Context

    private val PREF_NAME = "com.gmorales.instatest.prefs"
    private val TOKEN = "instagram-token"
    private val ID= "instagram-id"
    private val FIRST_NAME = "instagram-first-name"
    private val LAST_NAME = "instagram-last-name"
    private val EMAIL = "instagram-email"

    var sharedPref: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.user_profile_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI(view)
    }

    fun setupUI(view: View) {
        sharedPref =  mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        var token: String = sharedPref!!.getString(TOKEN, "").toString()
        var id: Int = sharedPref!!.getInt(ID, 0)

        val llProfileEditor = view.findViewById(R.id.llProfileEditor) as LinearLayout
        val llProfileViewer = view.findViewById(R.id.llProfileViewer) as LinearLayout

        val tvProfileEmail = view.findViewById(R.id.tv_profile_email) as TextView
        val tvProfileName = view.findViewById(R.id.tv_profile_name) as TextView
        val btnProfileEdit = view.findViewById(R.id.profile_edit) as Button

        val txtProfileEmail = view.findViewById(R.id.profile_email) as EditText
        val txtProfileFirstName = view.findViewById(R.id.profile_first_name) as EditText
        val txtProfileLastName = view.findViewById(R.id.profile_last_name) as EditText
        val txtProfilePassword = view.findViewById(R.id.profile_password) as EditText
        val txtProfileConfirmPassword = view.findViewById(R.id.profile_confirm_password) as EditText
        val pbProgressBar = view.findViewById(R.id.profileProgressBar) as ProgressBar
        val btnProfileSave = view.findViewById(R.id.profile_save) as Button

        val name: String = "${sharedPref!!.getString(FIRST_NAME, "")} " +
                           "${sharedPref!!.getString(LAST_NAME, "")}"
        tvProfileEmail.text = sharedPref!!.getString(EMAIL, "")
        tvProfileName.text = name
        txtProfileEmail.setText(sharedPref!!.getString(EMAIL, ""))
        txtProfileFirstName.setText(sharedPref!!.getString(FIRST_NAME, ""))
        txtProfileLastName.setText(sharedPref!!.getString(LAST_NAME, ""))

        btnProfileEdit.setOnClickListener {
            llProfileEditor.visibility = View.VISIBLE
            llProfileViewer.visibility = View.GONE
        }

        btnProfileSave.setOnClickListener {
            pbProgressBar.visibility = View.VISIBLE

            var service = RetrofitClient.instance?.create(UserAPI::class.java)
            var call: Call<SignUpResponseDTO>? =
                service?.updateUser(
                    token, id,
                    txtProfileFirstName.text.toString(),
                    txtProfileLastName.text.toString(),
                    txtProfileEmail.text.toString(),
                    if (txtProfilePassword.text.toString().trim().equals("")) "________" else txtProfilePassword.text.toString(),
                    if (txtProfileConfirmPassword.text.toString().trim().equals(""))"________" else txtProfileConfirmPassword.text.toString()
                )

            call?.enqueue(object: Callback<SignUpResponseDTO> {
                override fun onFailure(call: Call<SignUpResponseDTO>, t: Throwable) {
                    Log.e("APIError", t.message)
                }

                override fun onResponse(
                    call: Call<SignUpResponseDTO>,
                    response: Response<SignUpResponseDTO>
                ) {
                    //loginProgressBar.visibility = View.GONE
                    if(response?.code()!=200) {
                        val gson = Gson()
                        val type = object : TypeToken<ErrorDTO>() {}.type
                        var errorResponse: ErrorDTO? = gson.fromJson(response.errorBody()!!.charStream(), type)

                        Toast.makeText(mContext, "ERROR", Toast.LENGTH_LONG).show()
                        Log.e("API Error", "ERROR")
                    } else {
                        Log.e("ERROR", response?.body()?.toString())
                        val name: String = "${txtProfileFirstName.text.toString()} " +
                                           "${txtProfileLastName.text.toString()}"
                        tvProfileName.text = name
                        val editor = sharedPref!!.edit()
                        editor.putString(FIRST_NAME, txtProfileFirstName.text.toString())
                        editor.putString(LAST_NAME, txtProfileLastName.text.toString())
                        editor.apply()
                        pbProgressBar.visibility = View.GONE
                    }
                }
            })


            llProfileEditor.visibility = View.GONE
            llProfileViewer.visibility = View.VISIBLE
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(context: Context) =
            ProfileFragment().apply {
                mContext = context
                arguments = Bundle().apply {}
            }
    }
}
