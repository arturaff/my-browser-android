package ru.arturprgr.mybrowser.ui.fragment.main

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import ru.arturprgr.mybrowser.data.FirebaseHelper
import ru.arturprgr.mybrowser.data.Preferences
import ru.arturprgr.mybrowser.databinding.FragmentCollectionsBinding
import ru.arturprgr.mybrowser.makeMessage

class CollectionsFragment : Fragment() {
    private lateinit var binding: FragmentCollectionsBinding
    private lateinit var preferences: Preferences
    private lateinit var collectionsArrayList: MutableSet<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentCollectionsBinding.inflate(inflater, container, false)
        preferences = Preferences(requireContext())
        collectionsArrayList = mutableSetOf("")
        Log.d("Attempt", preferences.getQuantityCollections().toString())

        binding.apply {
            for (index in 0..preferences.getQuantityCollections()) {
                if (preferences.getQuantityCollections() == 0) {
                    layoutMain.visibility = View.GONE
                    textCollectionsInfo.visibility = View.VISIBLE
                    break
                } else {
                    val reference = "${preferences.getAccount()}/collections/$index"
                    FirebaseHelper("$reference/usage").getValue { usage ->
                        if (usage.toBoolean()) {
                            FirebaseHelper("$reference/name").getValue { name ->
                                tabLayout.addTab(tabLayout.newTab().setText(name))
                            }
                        }
                    }
                }
            }

            buttonAdd.setOnClickListener {
                val editName = EditText(requireContext())
                editName.maxLines = 12

                AlertDialog.Builder(requireContext())
                    .setTitle("Создание коллекции")
                    .setMessage("Введите название коллекции (менее 12 символов)")
                    .setView(editName)
                    .setPositiveButton("Создать") { dialog: DialogInterface, _ ->
                        val name = editName.text.toString()
                        if (collectionsArrayList.indexOf(name) == -1) {
                            val quantity = preferences.getQuantityCollections() + 1
                            preferences.setQuantityCollections(quantity)
                            preferences.setPreference("quantity$name", 0)
                            FirebaseHelper("${preferences.getAccount()}/collections/$quantity/name")
                                .setValue(name)
                            FirebaseHelper("${preferences.getAccount()}/collections/$quantity/usage")
                                .setValue(true)
                            FirebaseHelper("${preferences.getAccount()}/collections/$quantity/quantity")
                                .setValue(0)
                            FirebaseHelper("${preferences.getAccount()}/collections/quantity")
                                .setValue(quantity)
                            tabLayout.addTab(tabLayout.newTab().setText(name))
                        } else {
                            makeMessage(requireContext(), "У вас уже есть коллекция с таким названием!")
                            dialog.cancel()
                        }
                    }
                    .create()
                    .show()

                editName.updateLayoutParams<FrameLayout.LayoutParams> {
                    this.topMargin = 16
                    this.leftMargin = 48
                    this.rightMargin = 48
                    this.bottomMargin = 16
                }
            }
        }

        return binding.root
    }
}