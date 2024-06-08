package ru.arturprgr.mybrowser.ui.entrance

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import ru.arturprgr.mybrowser.MainActivity
import ru.arturprgr.mybrowser.Preferences
import ru.arturprgr.mybrowser.R
import ru.arturprgr.mybrowser.databinding.FragmentEntranceBinding
import ru.arturprgr.mybrowser.model.viewToast

class EntranceFragment : Fragment() {
    private lateinit var binding: FragmentEntranceBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEntranceBinding.inflate(inflater, container, false)

        val preferences = Preferences(requireContext())

        binding.apply {
            buttonEntrance.setOnClickListener {
                val email = "${editEmail.text}"
                val password = "${editPassword.text}"

                if (email != "" && password != "") {
                    val auth = Firebase.auth.signInWithEmailAndPassword(email, password)
                    auth.addOnSuccessListener {
                        val editedMail = email.replace(".", "")
                        Firebase.database.getReference("$editedMail/name")
                            .addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(name: DataSnapshot) {
                                    preferences.setEmail(email)
                                    preferences.setName("${name.value}")
                                    preferences.setAccount("${editedMail}/browser")
                                    val reference = preferences.getAccount()
                                    Firebase.database.getReference("$reference/bookmarks/quantity")
                                        .addValueEventListener(object : ValueEventListener {
                                            override fun onDataChange(snapshot: DataSnapshot) {
                                                preferences.setQuantityBookmarks("${snapshot.value}".toInt())

                                                Firebase.database.getReference("$reference/history/quantity")
                                                    .addValueEventListener(object :
                                                        ValueEventListener {
                                                        override fun onDataChange(snapshot: DataSnapshot) {
                                                            preferences.setQuantityHistory("${snapshot.value}".toInt())

                                                            Firebase.database.getReference("$reference/downloads/quantity")
                                                                .addValueEventListener(object :
                                                                    ValueEventListener {
                                                                    override fun onDataChange(
                                                                        snapshot: DataSnapshot
                                                                    ) {
                                                                        preferences.setQuantityDownloads("${snapshot.value}".toInt())

                                                                        requireContext().startActivity(
                                                                            Intent(
                                                                                requireContext(),
                                                                                MainActivity::class.java
                                                                            )
                                                                        )
                                                                    }

                                                                    override fun onCancelled(error: DatabaseError) {
                                                                        Firebase.auth.signOut()
                                                                        viewToast(
                                                                            requireContext(),
                                                                            "Что-то пошло не так!"
                                                                        )
                                                                    }
                                                                })
                                                        }

                                                        override fun onCancelled(error: DatabaseError) {
                                                            Firebase.auth.signOut()
                                                            viewToast(
                                                                requireContext(),
                                                                "Что-то пошло не так!"
                                                            )
                                                        }
                                                    })
                                            }

                                            override fun onCancelled(error: DatabaseError) {
                                                Firebase.auth.signOut()
                                                viewToast(requireContext(), "Что-то пошло не так!")
                                            }
                                        })
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Firebase.auth.signOut()
                                    viewToast(requireContext(), "Что-то пошло не так!")
                                }
                            })
                    }

                    auth.addOnFailureListener {
                        viewToast(requireContext(), "Что-то пошло не так!")
                    }

                } else {
                    viewToast(requireContext(), "Заполните все поля!")
                }
            }

            buttonRegister.setOnClickListener {
                findNavController().navigate(R.id.action_entranceFragment_to_registerFragment)
            }
        }

        return binding.root
    }
}