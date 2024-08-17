package ru.arturprgr.mybrowser.ui.fragment.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import ru.arturprgr.mybrowser.R
import ru.arturprgr.mybrowser.data.FirebaseHelper
import ru.arturprgr.mybrowser.data.Preferences
import ru.arturprgr.mybrowser.databinding.FragmentEntranceBinding
import ru.arturprgr.mybrowser.ui.activity.MainActivity
import ru.arturprgr.mybrowser.makeMessage

class EntranceFragment : Fragment() {
    private lateinit var binding: FragmentEntranceBinding
    private lateinit var preferences: Preferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentEntranceBinding.inflate(inflater, container, false)
        preferences = Preferences(requireContext())

        binding.apply {
            buttonEntrance.setOnClickListener {
                val email = "${editEmail.text}"
                val password = "${editPassword.text}"

                if (email != "" && password != "") {
                    Firebase.auth.signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener {
                            val editedMail = email.replace(".", "")
                            FirebaseHelper("$editedMail/name").getValue { name ->
                                val account = "${editedMail}/browser"
                                preferences.setEmail(email)
                                preferences.setName(name)
                                preferences.setAccount(account)
                                FirebaseHelper("$account/bookmarks/quantity").getValue { bookmarksQuantity ->
                                    preferences.setQuantityBookmarks(bookmarksQuantity.toInt())
                                    FirebaseHelper("$account/history/quantity").getValue { historyQuantity ->
                                        preferences.setQuantityHistory(historyQuantity.toInt())
                                        FirebaseHelper("$account/downloads/quantity").getValue { downloadsQuantity ->
                                            preferences.setQuantityDownloads(downloadsQuantity.toInt())
                                            FirebaseHelper("$account/collections/quantity").getValue { collectionsQuantity ->
                                                preferences.setQuantityCollections(collectionsQuantity.toInt())
                                                startActivity(
                                                    Intent(
                                                        requireContext(),
                                                        MainActivity::class.java
                                                    )
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        .addOnFailureListener {
                            makeMessage(requireContext(), "Что-то пошло не так!")
                        }
                } else makeMessage(requireContext(), "Заполните все поля!")
            }

            buttonRegister.setOnClickListener {
                findNavController().navigate(R.id.action_entranceFragment_to_registerFragment)
            }
        }

        return binding.root
    }
}