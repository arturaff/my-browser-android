package ru.arturprgr.mybrowser.model

import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.database.database

fun setValue(path: String, value: Any) {
    Firebase.database.getReference(path).setValue(value)
}

fun viewToast(context: android.content.Context, text: String) {
    Toast.makeText(context, text, Toast.LENGTH_LONG).show()
}