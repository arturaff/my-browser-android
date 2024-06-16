package ru.arturprgr.mybrowser

import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

fun viewToast(context: android.content.Context, text: String) {
    Toast.makeText(context, text, Toast.LENGTH_LONG).show()
}