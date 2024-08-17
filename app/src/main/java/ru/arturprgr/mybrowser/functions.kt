package ru.arturprgr.mybrowser

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast

var weather: ArrayList<String> = arrayListOf()

@SuppressLint("ResourceAsColor")
fun makeMessage(context: Context, text: String) =
    Toast.makeText(context, text, Toast.LENGTH_LONG).show()

fun getDefaultString(context: Context, key: String): String =
    androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
        .getString(key, "").toString()

fun getDefaultBoolean(context: Context, key: String): Boolean =
    androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
        .getBoolean(key, false)