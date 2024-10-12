package ru.arturprgr.mybrowser.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import ru.arturprgr.mybrowser.R
import ru.arturprgr.mybrowser.adapter.BookmarksAdapter
import ru.arturprgr.mybrowser.adapter.MainViewPagerAdapter
import ru.arturprgr.mybrowser.data.FirebaseHelper
import ru.arturprgr.mybrowser.data.SavesHelper
import ru.arturprgr.mybrowser.data.Singleton
import ru.arturprgr.mybrowser.databinding.ActivityMainBinding
import ru.arturprgr.mybrowser.model.Card
import ru.arturprgr.mybrowser.ui.fragment.main.BookmarksFragment
import ru.arturprgr.mybrowser.ui.fragment.main.DownloadsFragment
import ru.arturprgr.mybrowser.ui.fragment.main.HistoryFragment
import ru.arturprgr.mybrowser.ui.fragment.main.SearchFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var savesHelper: SavesHelper
    private lateinit var viewPagerAdapter: MainViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        savesHelper = SavesHelper(this@MainActivity)
        viewPagerAdapter = MainViewPagerAdapter(supportFragmentManager)
        viewPagerAdapter.addFragment(SearchFragment())
        viewPagerAdapter.addFragment(HistoryFragment())
        viewPagerAdapter.addFragment(BookmarksFragment())
        viewPagerAdapter.addFragment(DownloadsFragment())

        val bookmarksReference = "${savesHelper.getAccount()}/bookmarks"
        val bookmarksQuantity = savesHelper.getQuantityBookmarks()
        if (bookmarksQuantity != 0) for (index in bookmarksQuantity downTo 0) FirebaseHelper("${bookmarksReference}/$index/usage").getValue { usage ->
            if (usage.toBoolean()) FirebaseHelper("$bookmarksReference/$index/name").getValue { name ->
                FirebaseHelper("$bookmarksReference/$index/url").getValue { url ->
                    Singleton.bookmarksAdapter.addBookmark(
                        Card(this@MainActivity, name, url, index)
                    )
                }
            }
        }

        val downloadsReference = "${savesHelper.getAccount()}/downloads"
        val downloadsQuantity = savesHelper.getQuantityDownloads()
        if (downloadsQuantity != 0) for (index in downloadsQuantity downTo 0) FirebaseHelper("$downloadsReference/$index/usage").getValue { usage ->
            if (usage.toBoolean()) FirebaseHelper("$downloadsReference/$index/name").getValue { name ->
                FirebaseHelper("$downloadsReference/$index/path").getValue { path ->
                    Singleton.downloadsAdapter.addDownload(
                        Card(this@MainActivity, name, path, index)
                    )
                }
            }
        }

        val historyReference = "${savesHelper.getAccount()}/history"
        val historyQuantity = savesHelper.getQuantityHistory()
        if (historyQuantity != 0) for (index in historyQuantity downTo 0)
            FirebaseHelper("${historyReference}/$index/usage").getValue { usage ->
                if (usage.toBoolean()) FirebaseHelper("$historyReference/$index/name").getValue { name ->
                    FirebaseHelper("$historyReference/$index/url").getValue { url ->
                        Singleton.historyAdapter.addQuery(
                            Card(this@MainActivity, name, url, index)
                        )
                    }
                }
            }

        setContentView(binding.root)

        if (Firebase.auth.currentUser != null) else startActivity(
            Intent(
                this@MainActivity, AuthActivity::class.java
            )
        )

        binding.apply {
            viewPager.adapter = viewPagerAdapter
            @Suppress("DEPRECATION") viewPager.setOnPageChangeListener(object :
                OnPageChangeListener {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int,
                ) {
                    //not init
                }

                override fun onPageSelected(position: Int) {
                    when (position) {
                        0 -> selectFragment.selectedItemId = R.id.main_item
                        1 -> selectFragment.selectedItemId = R.id.history_item
                        2 -> selectFragment.selectedItemId = R.id.bookmarks_item
                        3 -> selectFragment.selectedItemId = R.id.downloads_item
                    }
                }

                override fun onPageScrollStateChanged(state: Int) {
                    //not init
                }
            })
            selectFragment.setOnItemSelectedListener { listener ->
                when (listener.itemId) {
                    R.id.main_item -> viewPager.setCurrentItem(0)
                    R.id.history_item -> viewPager.setCurrentItem(1)
                    R.id.bookmarks_item -> viewPager.setCurrentItem(2)
                    R.id.downloads_item -> viewPager.setCurrentItem(3)
                }
                true
            }
        }
    }
}