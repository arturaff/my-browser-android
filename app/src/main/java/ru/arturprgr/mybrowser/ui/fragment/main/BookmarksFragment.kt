package ru.arturprgr.mybrowser.ui.fragment.main

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.arturprgr.mybrowser.adapter.BookmarksAdapter
import ru.arturprgr.mybrowser.data.Singleton
import ru.arturprgr.mybrowser.databinding.FragmentBookmarksBinding

class BookmarksFragment : Fragment() {
    private lateinit var binding: FragmentBookmarksBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentBookmarksBinding.inflate(inflater, container, false)
        BookmarksFragment.context = requireContext()

        binding.apply {
            listBookmarks.layoutManager = LinearLayoutManager(requireContext())
            listBookmarks.adapter = Singleton.bookmarksAdapter
        }

        return binding.root
    }

    @SuppressLint("StaticFieldLeak")
    companion object {
        lateinit var context: Context
    }
}