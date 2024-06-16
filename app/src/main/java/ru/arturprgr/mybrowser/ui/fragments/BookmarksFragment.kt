package ru.arturprgr.mybrowser.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import ru.arturprgr.mybrowser.adapter.BookmarksAdapter
import ru.arturprgr.mybrowser.classes.Database
import ru.arturprgr.mybrowser.classes.Preferences
import ru.arturprgr.mybrowser.databinding.FragmentBookmarksBinding
import ru.arturprgr.mybrowser.model.Bookmark

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
            Database("${reference}/$q/usage").getValue { usage ->
                if (usage.toBoolean()) Database("$reference/$q/name").getValue { name ->
                    Database("$reference/$q/url").getValue { url ->
                        adapter.addBookmark(
                            Bookmark(requireContext(), name, url, q)
                        )
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