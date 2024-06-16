package ru.arturprgr.mybrowser.ui.fragments

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
import ru.arturprgr.mybrowser.databinding.FragmentRegisterBinding
import ru.arturprgr.mybrowser.viewToast

class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)

        binding.apply {
            buttonRegister.setOnClickListener {
                val email = "${editEmail.text}"
                val name = "${editName.text}"
                val password = "${editPassword.text}"
                val secondPassword = "${editPassword.text}"

                if (email != "" && name != "" && password != "" && secondPassword != "") {
                    if (password == secondPassword) {
                        Firebase.auth.createUserWithEmailAndPassword(email, password)
                            .addOnSuccessListener {
                                val editedEmail = email.replace(".", "")
                                Database("$editedEmail/name").setValue(name)
                                Database("$editedEmail/browser/bookmarks/quantity").setValue(0)
                                Database("$editedEmail/browser/history/quantity").setValue(0)
                                Database("$editedEmail/browser/downloads/quantity").setValue(0)
                                Database("$editedEmail}/goals/goals/quantity").setValue(0)
                                findNavController().navigate(R.id.action_registerFragment_to_entranceFragment)
                            }
                            .addOnFailureListener {
                                viewToast(requireContext(), "Что-то пошло не так!")
                            }
                    } else viewToast(requireContext(), "Пароли не совпадают")
                } else viewToast(requireContext(), "Заполните все поля!")

            }

            buttonEntrance.setOnClickListener {
                findNavController().navigate(R.id.action_registerFragment_to_entranceFragment)
            }
        }

        return binding.root
    }
}