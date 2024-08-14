package ru.arturprgr.mybrowser

import android.widget.Toast

fun makeMessage(context: android.content.Context, text: String) {
    Toast.makeText(context, text, Toast.LENGTH_LONG).show()
}