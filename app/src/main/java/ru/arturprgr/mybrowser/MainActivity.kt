package ru.arturprgr.mybrowser

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentContainerView
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import ru.arturprgr.mybrowser.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Firebase.auth.currentUser != null) {
        } else {
            startActivity(Intent(this@MainActivity, AuthActivity::class.java))
        }

        binding.apply {
            selectFragment.setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.main_item -> {
                        setFragment(binding.fragmentMain)
                    }

                    R.id.bookmarks_item -> {
                        if (Firebase.auth.currentUser == null) {
                            startActivity(Intent(this@MainActivity, AuthActivity::class.java))
                        } else {
                            setFragment(binding.fragmentBookmarks)
                        }
                    }

                    R.id.history_item -> {
                        if (Firebase.auth.currentUser == null) {
                            startActivity(Intent(this@MainActivity, AuthActivity::class.java))
                        } else {
                            setFragment(binding.fragmentHistory)
                        }
                    }

                    R.id.downloads_item -> {
                        if (Firebase.auth.currentUser == null) {
                            startActivity(Intent(this@MainActivity, AuthActivity::class.java))
                        } else {
                            setFragment(binding.fragmentDownloads)
                        }
                    }
                }
                true
            }
        }
    }

    private fun setFragment(fragment: FragmentContainerView) = with(binding) {
        fragmentMain.visibility = View.GONE
        fragmentBookmarks.visibility = View.GONE
        fragmentHistory.visibility = View.GONE
        fragmentDownloads.visibility = View.GONE
        fragment.visibility = View.VISIBLE
    }
}