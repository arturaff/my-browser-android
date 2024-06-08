package ru.arturprgr.mybrowser.ui.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import ru.arturprgr.mybrowser.Preferences
import ru.arturprgr.mybrowser.R
import ru.arturprgr.mybrowser.databinding.FragmentRegisterBinding
import ru.arturprgr.mybrowser.model.setValue
import ru.arturprgr.mybrowser.model.viewToast

class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)

        binding.apply {
            buttonRegister.setOnClickListener {
                val email = "${editEmail.text}"
                val name ="${editName.text}"
                val password = "${editPassword.text}"
                val secondPassword = "${editPassword.text}"

                if (email != "" && name != "" && password != "" && secondPassword != "") {
                    if (password == secondPassword) {
                        val user = Firebase.auth.createUserWithEmailAndPassword(email, password)
                        user.addOnSuccessListener {
                            setValue(
                                "${email.replace(".", "")}/name",
                                name
                            )
                            setValue(
                                "${email.replace(".", "")}/browser/bookmarks/quantity",
                                0
                            )
                            setValue(
                                "${email.replace(".", "")}/browser/history/quantity",
                                0
                            )
                            setValue(
                                "${email.replace(".", "")}/browser/downloads/quantity",
                                0
                            )
                            setValue(
                                "${email.replace(".", "")}/goals/goals/quantity",
                                0
                            )

                            findNavController().navigate(R.id.action_registerFragment_to_entranceFragment)
                        }

                        user.addOnFailureListener {
                            viewToast(requireContext(), "Что-то пошло не так!")
                        }
                    } else {
                        viewToast(requireContext(), "Пароли не совпадают")
                    }
                } else {
                    viewToast(requireContext(), "Заполните все поля!")
                }
            }

            buttonEntrance.setOnClickListener {
                findNavController().navigate(R.id.action_registerFragment_to_entranceFragment)
            }
        }

        return binding.root
    }
}