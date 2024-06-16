package ru.arturprgr.mybrowser.ui.fragments

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
import ru.arturprgr.mybrowser.classes.Database
import ru.arturprgr.mybrowser.classes.Preferences
import ru.arturprgr.mybrowser.databinding.FragmentEntranceBinding
import ru.arturprgr.mybrowser.ui.activities.MainActivity
import ru.arturprgr.mybrowser.viewToast

class EntranceFragment : Fragment() {
    private lateinit var binding: FragmentEntranceBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentEntranceBinding.inflate(inflater, container, false)

        val preferences = Preferences(requireContext())

        binding.apply {
            buttonEntrance.setOnClickListener {
                val email = "${editEmail.text}"
                val password = "${editPassword.text}"

                if (email != "" && password != "") {
                    Firebase.auth.signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener {
                            val editedMail = email.replace(".", "")
                            Database("$editedMail/name").getValue { name ->
                                preferences.setEmail(email)
                                preferences.setName(name)
                                preferences.setAccount("${editedMail}/browser")
                                val reference = preferences.getAccount()
                                Database("$reference/bookmarks/quantity").getValue { bookmarksQuantity ->
                                    preferences.setQuantityBookmarks(bookmarksQuantity.toInt())
                                    Database("$reference/history/quantity").getValue { historyQuantity ->
                                        preferences.setQuantityHistory(historyQuantity.toInt())
                                        Database("$reference/downloads/quantity").getValue { downloadsQuantity ->
                                            preferences.setQuantityDownloads(downloadsQuantity.toInt())
                                            requireContext().startActivity(
                                                Intent(requireContext(), MainActivity::class.java)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        .addOnFailureListener {
                            viewToast(requireContext(), "Что-то пошло не так!")
                        }

                } else viewToast(requireContext(), "Заполните все поля!")
            }

            buttonRegister.setOnClickListener {
                findNavController().navigate(R.id.action_entranceFragment_to_registerFragment)
            }
        }

        return binding.root
    }
}