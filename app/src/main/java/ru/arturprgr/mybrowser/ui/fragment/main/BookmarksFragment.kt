package ru.arturprgr.mybrowser.ui.fragment.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import ru.arturprgr.mybrowser.adapter.BookmarksAdapter
import ru.arturprgr.mybrowser.data.FirebaseHelper
import ru.arturprgr.mybrowser.data.Preferences
import ru.arturprgr.mybrowser.databinding.FragmentBookmarksBinding
import ru.arturprgr.mybrowser.model.Card

class BookmarksFragment : Fragment() {
    private lateinit var binding: FragmentBookmarksBinding
    private val adapter = BookmarksAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentBookmarksBinding.inflate(inflater, container, false)

        val reference = "${Preferences(requireContext()).getAccount()}/bookmarks"
        val quantity = Preferences(requireContext()).getQuantityBookmarks()

        if (quantity != 0) for (q in quantity downTo 0)
            FirebaseHelper("${reference}/$q/usage").getValue { usage ->
                if (usage.toBoolean()) FirebaseHelper("$reference/$q/name").getValue { name ->
                    FirebaseHelper("$reference/$q/url").getValue { url ->
                        adapter.addBookmark(Card(requireContext(), name, url, q))
                    }
                }
            }

        binding.apply {
            listBookmarks.layoutManager = LinearLayoutManager(requireContext())
            listBookmarks.adapter = adapter
        }

        return binding.root
    }
}