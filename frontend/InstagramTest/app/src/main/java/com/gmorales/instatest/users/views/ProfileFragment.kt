package com.gmorales.instatest.users.views

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

import com.gmorales.instatest.R
import com.gmorales.instatest.core.Constants
import com.gmorales.instatest.core.ImageToBase64
import com.gmorales.instatest.core.RetrofitClient
import com.gmorales.instatest.core.models.ErrorDTO
import com.gmorales.instatest.users.controllers.UserAPI
import com.gmorales.instatest.users.models.PasswordResetResponseDTO
import com.gmorales.instatest.users.models.SignUpResponseDTO
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.user_profile_fragment.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class ProfileFragment : Fragment() {
    lateinit var mContext: Context

    private val TAG = "ProfileFragment"

    private val GALLERY = 1

    private val PREF_NAME = "com.gmorales.instatest.prefs"
    private val TOKEN = "instagram-token"
    private val ID= "instagram-id"
    private val FIRST_NAME = "instagram-first-name"
    private val LAST_NAME = "instagram-last-name"
    private val EMAIL = "instagram-email"
    private val PHOTO = "instagram-profile-photo"

    var sharedPref: SharedPreferences? = null

    lateinit var imgBase64: String

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GALLERY) {
            if (data != null) {
                val contentURI = data!!.data
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(
                        context?.contentResolver,
                        contentURI
                    )
                    iv_profile_picture!!.setImageBitmap(bitmap)

                    imgBase64 = ImageToBase64(bitmap)
                    Log.e(TAG, imgBase64)
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.setTitle(R.string.title_profile)
        setupUI(view)
    }

    fun setupUI(view: View) {
        sharedPref =  mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        var token: String = sharedPref!!.getString(TOKEN, "").toString()
        var id: Int = sharedPref!!.getInt(ID, 0)
        var profile_photo: String = sharedPref!!.getString(PHOTO, "").toString()

        val llProfileEditor = view.findViewById(R.id.llProfileEditor) as LinearLayout
        val llProfileViewer = view.findViewById(R.id.llProfileViewer) as LinearLayout

        val ivProfilePicture = view.findViewById(R.id.iv_profile_picture) as ImageView

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

        // Load profile picture into profile ImageView
        Picasso.with(context)
            .load("${Constants.BASE_URL}${profile_photo}")
            .resize(125, 125)
            .into(ivProfilePicture)

        val name: String = "${sharedPref!!.getString(FIRST_NAME, "")} " +
                           "${sharedPref!!.getString(LAST_NAME, "")}"
        tvProfileEmail.text = sharedPref!!.getString(EMAIL, "")
        tvProfileName.text = name
        txtProfileEmail.setText(sharedPref!!.getString(EMAIL, ""))
        txtProfileFirstName.setText(sharedPref!!.getString(FIRST_NAME, ""))
        txtProfileLastName.setText(sharedPref!!.getString(LAST_NAME, ""))

        ivProfilePicture.setOnClickListener {
            if (llProfileEditor.visibility==View.VISIBLE) {
                val intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, GALLERY)
            }
        }

        btnProfileEdit.setOnClickListener {
            llProfileEditor.visibility = View.VISIBLE
            llProfileViewer.visibility = View.GONE
        }

        btnProfileSave.setOnClickListener {
            pbProgressBar.visibility = View.VISIBLE

            imgBase64 = "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"

            var service = RetrofitClient.instance?.create(UserAPI::class.java)
            Log.e(TAG, token)
            Log.e(TAG, txtProfileEmail.text.toString())
            Log.e(TAG, imgBase64)
            var call: Call<PasswordResetResponseDTO>? =
                service?.updateProfilePhoto(token, txtProfileEmail.text.toString(), imgBase64)
            call?.enqueue(object: Callback<PasswordResetResponseDTO> {
                override fun onFailure(call: Call<PasswordResetResponseDTO>, t: Throwable) {
                    Log.e(TAG, t.message)
                }

                override fun onResponse(
                    call: Call<PasswordResetResponseDTO>,
                    response: Response<PasswordResetResponseDTO>
                ) {
                    if(response?.code()!=200) {
                        val gson = Gson()
                        val type = object : TypeToken<ErrorDTO>() {}.type
                        var errorResponse: ErrorDTO? = gson.fromJson(response.errorBody()!!.charStream(), type)

                        Toast.makeText(mContext, "ERROR", Toast.LENGTH_LONG).show()
                        Log.e(TAG, "ERROR")
                    } else {
                        Log.e(TAG, response?.body()?.toString())
                    }
                }
            })

/*
            val call2: Call<SignUpResponseDTO>? =
                service?.updateUser(
                    token, id,
                    txtProfileFirstName.text.toString(),
                    txtProfileLastName.text.toString(),
                    txtProfileEmail.text.toString(),
                    if (txtProfilePassword.text.toString().trim().equals("")) "________" else txtProfilePassword.text.toString(),
                    if (txtProfileConfirmPassword.text.toString().trim().equals(""))"________" else txtProfileConfirmPassword.text.toString()
                )
            call2?.enqueue(object: Callback<SignUpResponseDTO> {
                override fun onFailure(call: Call<SignUpResponseDTO>, t: Throwable) {
                    Log.e(TAG, t.message)
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
                        Log.e(TAG, "ERROR")
                    } else {
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
*/

            pbProgressBar.visibility = View.GONE

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
