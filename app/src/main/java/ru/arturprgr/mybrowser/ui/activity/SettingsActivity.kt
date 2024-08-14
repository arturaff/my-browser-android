package ru.arturprgr.mybrowser.ui.activity

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.updateLayoutParams
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import ru.arturprgr.mybrowser.data.FirebaseHelper
import ru.arturprgr.mybrowser.data.Preferences
import ru.arturprgr.mybrowser.databinding.ActivitySettingsBinding
import ru.arturprgr.mybrowser.makeMessage

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val preferences = Preferences(this@SettingsActivity)

        binding.apply {
            if (Firebase.auth.currentUser != null) {
                buttonEditPassword.setOnClickListener {
                    Firebase.auth.sendPasswordResetEmail(preferences.getEmail())
                    makeMessage(
                        this@SettingsActivity,
                        "Инструкция по восстановлению отправлена на ваш Email"
                    )
                }

                buttonEditName.setOnClickListener {
                    val editName = EditText(this@SettingsActivity)
                    editName.hint = "Введите новое имя"
                    AlertDialog.Builder(this@SettingsActivity)
                        .setTitle("Изменить имя")
                        .setMessage("Введите имя и нажмите сохранить")
                        .setView(editName)
                        .setPositiveButton("Сохранить") { _: DialogInterface, _: Int ->
                            FirebaseHelper("${preferences.getAccount()}/name").setValue("${editName.text}")
                            preferences.setName("${editName.text}")
                        }
                        .create()
                        .show()

                    editName.updateLayoutParams<FrameLayout.LayoutParams> {
                        this.leftMargin = 48
                        this.rightMargin = 48
                        this.topMargin = 16
                        this.bottomMargin = 16
                    }
                }

                buttonSignOut.setOnClickListener {
                    Firebase.auth.signOut()
                    AlertDialog.Builder(this@SettingsActivity)
                        .setTitle("Подтвердите действие")
                        .setMessage("Вы точно хотите выйти из аккаунта? У вас не будет сохраняться история и не будет быстрого доступа к любимым сайтам")
                        .setPositiveButton("Да") { _: DialogInterface, _: Int ->
                            Firebase.auth.signOut()
                            startActivity(Intent(this@SettingsActivity, MainActivity::class.java))
                            makeMessage(this@SettingsActivity, "Вы вышли из аккаунта!")
                        }
                        .create()
                        .show()
                }

                textEmail.text = "Ваша почта: ${preferences.getEmail()}"
                textName.text = "Ваше имя: ${preferences.getName()}"
            }
        }
    }
}