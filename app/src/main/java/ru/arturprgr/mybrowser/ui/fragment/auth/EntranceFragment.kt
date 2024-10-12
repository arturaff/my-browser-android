package ru.arturprgr.mybrowser.ui.fragment.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences.Editor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import ru.arturprgr.mybrowser.R
import ru.arturprgr.mybrowser.data.FirebaseHelper
import ru.arturprgr.mybrowser.data.SavesHelper
import ru.arturprgr.mybrowser.databinding.FragmentEntranceBinding
import ru.arturprgr.mybrowser.makeMessage
import ru.arturprgr.mybrowser.ui.activity.MainActivity

class EntranceFragment : Fragment() {
    private lateinit var binding: FragmentEntranceBinding
    private lateinit var preferenceManager: Editor
    private lateinit var savesHelper: SavesHelper

    @SuppressLint("RestrictedApi")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentEntranceBinding.inflate(inflater, container, false)
        preferenceManager = PreferenceManager.getDefaultSharedPreferences(requireContext()).edit()
        savesHelper = SavesHelper(requireContext())

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
                                savesHelper.setEmail(email)
                                savesHelper.setName(name)
                                savesHelper.setAccount(account)

                                FirebaseHelper("$account/preferences/auto_location").getValue { autoLocation ->
                                    preferenceManager.putBoolean("auto_location", autoLocation.toBoolean()).apply()
                                }

                                FirebaseHelper("$account/preferences/bottom_panel_tools").getValue { bottomPanelTools ->
                                    preferenceManager.putBoolean("bottom_panel_tools", bottomPanelTools.toBoolean()).apply()
                                }

                                FirebaseHelper("$account/preferences/def_location").getValue { defLocation ->
                                    preferenceManager.putString("def_location", defLocation).apply()
                                }

                                FirebaseHelper("$account/preferences/def_search_system").getValue { defSearchSystem ->
                                    preferenceManager.putString("def_search_system", defSearchSystem).apply()
                                }

                                FirebaseHelper("$account/preferences/disabled_weather").getValue { disabledWeather ->
                                    preferenceManager.putBoolean("disabled_weather", disabledWeather.toBoolean()).apply()
                                }

                                FirebaseHelper("$account/bookmarks/quantity").getValue { bookmarksQuantity ->
                                    savesHelper.setQuantityBookmarks(bookmarksQuantity.toInt())
                                }

                                FirebaseHelper("$account/history/quantity").getValue { historyQuantity ->
                                    savesHelper.setQuantityHistory(historyQuantity.toInt())
                                }

                                FirebaseHelper("$account/downloads/quantity").getValue { downloadsQuantity ->
                                    savesHelper.setQuantityDownloads(downloadsQuantity.toInt())
                                }
                            }
                            startActivity(Intent(requireContext(), MainActivity::class.java))
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