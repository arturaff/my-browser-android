package ru.arturprgr.mybrowser.classes

import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class Database(val path: String) {
    private val reference = Firebase.database.getReference(path)

    fun setValue(value: Any) = reference.setValue(value)

    fun getValue(onGet: (value: String) -> Unit) =
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) = onGet(snapshot.value.toString())
            override fun onCancelled(error: DatabaseError) {}
        })
}