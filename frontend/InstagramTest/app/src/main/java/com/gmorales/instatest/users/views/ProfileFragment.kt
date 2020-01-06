package com.gmorales.instatest.users.views

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

import com.gmorales.instatest.R

class ProfileFragment : Fragment() {
    lateinit var mContext: Context

    private val PREF_NAME = "com.gmorales.instatest.prefs"
    private val TOKEN = "instagram-token"
    private val ID= "instagram-id"
    private val FIRST_NAME = "instagram-first-name"
    private val LAST_NAME = "instagram-last-name"
    private val EMAIL = "instagram-email"

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
        val sharedPreference =  mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        val txtProfileEmail = view.findViewById(R.id.profile_email) as EditText
        val txtProfileFirstName = view.findViewById(R.id.profile_first_name) as EditText
        val txtProfileLastName = view.findViewById(R.id.profile_last_name) as EditText
        val txtProfilePassword = view.findViewById(R.id.profile_password) as EditText
        val txtProfileConfirmPassword = view.findViewById(R.id.profile_confirm_password) as EditText
        val btnProfileSave = view.findViewById(R.id.profile_save) as Button

        txtProfileEmail.setText(sharedPreference.getString(EMAIL, ""))
        txtProfileFirstName.setText(sharedPreference.getString(FIRST_NAME, ""))
        txtProfileLastName.setText(sharedPreference.getString(LAST_NAME, ""))

        btnProfileSave.setOnClickListener {
            Toast.makeText(mContext, "lerolero", Toast.LENGTH_LONG).show()
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
