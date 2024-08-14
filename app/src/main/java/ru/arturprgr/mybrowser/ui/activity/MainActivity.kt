package ru.arturprgr.mybrowser.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentContainerView
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import ru.arturprgr.mybrowser.R
import ru.arturprgr.mybrowser.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Firebase.auth.currentUser != null) else startActivity(
            Intent(
                this@MainActivity,
                AuthActivity::class.java
            )
        )

        binding.apply {
            selectFragment.setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.main_item -> setFragment(fragmentMain)

                    R.id.bookmarks_item -> if (Firebase.auth.currentUser == null)
                        startActivity(Intent(this@MainActivity, AuthActivity::class.java))
                    else setFragment(fragmentBookmarks)

                    R.id.history_item -> if (Firebase.auth.currentUser == null)
                        startActivity(Intent(this@MainActivity, AuthActivity::class.java))
                    else setFragment(fragmentHistory)

                    R.id.downloads_item -> if (Firebase.auth.currentUser == null)
                        startActivity(Intent(this@MainActivity, AuthActivity::class.java))
                    else setFragment(fragmentDownloads)

                    R.id.collections_item -> if (Firebase.auth.currentUser == null)
                        startActivity(Intent(this@MainActivity, AuthActivity::class.java))
                    else setFragment(fragmentCollections)
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
        fragmentCollections.visibility = View.GONE
        fragment.visibility = View.VISIBLE
    }
}