package com.gmorales.instatest.core

import android.R.attr
import android.content.Context
import android.graphics.Bitmap
import android.util.Base64
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import java.io.ByteArrayOutputStream
import java.util.regex.Pattern.compile


object Constants {
    val BASE_URL = "http://35.199.123.208:8000"
}

fun ImageToBase64(bitmap: Bitmap): String {
    if (bitmap != null) {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    return ""
}

private val emailRegex = compile(
    "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
            "\\@" +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
            "(" +
            "\\." +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
            ")+"
)
private val passwordRegex = compile("^(?=\\S{6,20}\$)(?=.*?\\d)(?=.*?[a-z])(?=.*?[A-Z])(?=.*?[^A-Za-z\\s0-9])")


/**
 * Validate password strings
 */
fun String.isPassword() : Boolean {
    return passwordRegex.matcher(this).matches()
}

/**
 * Validate email strings
 */
fun String.isEmail() : Boolean {
    return emailRegex.matcher(this).matches()
}

/**
 * Shows a Dialog Box
 *
 * @param title
 * @param message
 * @param mContext
 */
fun showDialogBox(title: String, message: String?, view: View, mContext: Context){

    val builder = AlertDialog.Builder(mContext)

    with(builder)
    {
        setTitle(title)
        setMessage(message)
        setPositiveButton(android.R.string.yes) { dialog, which ->
            Toast.makeText(mContext,
                android.R.string.yes, Toast.LENGTH_SHORT).show()
        }
        show()
    }

}