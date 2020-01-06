package com.gmorales.instatest.users.views

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

import com.gmorales.instatest.R
import com.gmorales.instatest.core.Constants
import com.gmorales.instatest.core.RetrofitClient
import com.gmorales.instatest.core.models.ErrorDTO
import com.gmorales.instatest.users.controllers.UserAPI
import com.gmorales.instatest.users.models.SignUpResponseDTO
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.user_profile_fragment.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class ProfileFragment : Fragment() {
    lateinit var mContext: Context

    private val TAG = "ProfileFragment"

    private val GALLERY = 1
    private val CAMERA = 2

    private val PREF_NAME = "com.gmorales.instatest.prefs"
    private val TOKEN = "instagram-token"
    private val ID= "instagram-id"
    private val FIRST_NAME = "instagram-first-name"
    private val LAST_NAME = "instagram-last-name"
    private val EMAIL = "instagram-email"
    private val PHOTO = "instagram-profile-photo"

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GALLERY) {
            if (data != null) {
                val contentURI = data!!.data
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, contentURI)
                    val path = saveImage(bitmap)
                    Toast.makeText(context, "Image Saved!", Toast.LENGTH_SHORT).show()
                    iv_profile_picture!!.setImageBitmap(bitmap)

                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show()
                }

            }

        } else if (requestCode == CAMERA) {
            val thumbnail = data!!.extras!!.get("data") as Bitmap
            iv_profile_picture!!.setImageBitmap(thumbnail)
            saveImage(thumbnail)
            Toast.makeText(context, "Image Saved!", Toast.LENGTH_SHORT).show()
        }
    }

    fun saveImage(myBitmap: Bitmap):String {
        val bytes = ByteArrayOutputStream()
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
        val wallpaperDirectory = File(
            (Environment.getExternalStorageDirectory()).toString() + IMAGE_DIRECTORY)
        // have the object build the directory structure, if needed.
        Log.d("fee",wallpaperDirectory.toString())
        if (!wallpaperDirectory.exists())
        {

            wallpaperDirectory.mkdirs()
        }

        try
        {
            Log.d("heel",wallpaperDirectory.toString())
            val f = File(wallpaperDirectory, ((Calendar.getInstance()
                .getTimeInMillis()).toString() + ".jpg"))
            f.createNewFile()
            val fo = FileOutputStream(f)
            fo.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(this,
                arrayOf(f.getPath()),
                arrayOf("image/jpeg"), null)
            fo.close()
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath())

            return f.getAbsolutePath()
        }
        catch (e1: IOException) {
            e1.printStackTrace()
        }

        return ""
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
            val pictureDialog = AlertDialog.Builder(context)
            pictureDialog.setTitle("Select Action")
            val pictureDialogItems = arrayOf(
                getString(R.string.picture_gallery),
                getString(R.string.picture_camera)
            )
            pictureDialog.setItems(pictureDialogItems
            ) {
                dialog,
                which ->
                    when (which) {
                        0 -> pictureFromGallery()
                        1 -> photoFromCamera()
                    }
            }
            pictureDialog.show()
        }

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


            llProfileEditor.visibility = View.GONE
            llProfileViewer.visibility = View.VISIBLE
        }
    }

    private fun pictureFromGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        startActivityForResult(galleryIntent, GALLERY)
    }

    private fun photoFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA)
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
