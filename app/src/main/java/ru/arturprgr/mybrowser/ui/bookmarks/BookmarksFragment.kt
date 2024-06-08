package ru.arturprgr.mybrowser.ui.bookmarks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import ru.arturprgr.mybrowser.Preferences
import ru.arturprgr.mybrowser.databinding.FragmentBookmarksBinding

class BookmarksFragment : Fragment() {
    private lateinit var binding: FragmentBookmarksBinding
    private val adapter = BookmarksAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBookmarksBinding.inflate(inflater, container, false)

        val reference = "${Preferences(requireContext()).getAccount()}/bookmarks"
        val quantity = Preferences(requireContext()).getQuantityBookmarks()

        if (quantity != 0) {
            for (q in quantity downTo 0) {
                Firebase.database.getReference("${reference}/$q/usage")
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.value.toString().toBoolean()) {
                                Firebase.database.getReference("$reference/$q/name")
                                    .addValueEventListener(object : ValueEventListener {
                                        override fun onDataChange(name: DataSnapshot) {
                                            Firebase.database.getReference("$reference/$q/url")
                                                .addValueEventListener(object : ValueEventListener {
                                                    override fun onDataChange(url: DataSnapshot) {
                                                        adapter.addBookmark(
                                                            Bookmark(
                                                                requireContext(),
                                                                name.value.toString(),
                                                                url.value.toString(),
                                                                q
                                                            )
                                                        )
                                                    }

                                                    override fun onCancelled(error: DatabaseError) {
                                                    }
                                                })
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                        }
                                    })
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                        }
                    })
            }
        }

        binding.apply {
            listBookmarks.layoutManager = LinearLayoutManager(requireContext())
            listBookmarks.adapter = adapter
        }

        return binding.root
    }
}