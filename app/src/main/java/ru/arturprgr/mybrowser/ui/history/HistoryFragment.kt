package ru.arturprgr.mybrowser.ui.history

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
import ru.arturprgr.mybrowser.databinding.FragmentHistoryBinding
import ru.arturprgr.mybrowser.model.setValue

class HistoryFragment : Fragment() {
    private lateinit var binding: FragmentHistoryBinding
    private val adapter = HistoryAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)

        val preferences = Preferences(requireContext())
        val reference = "${preferences.getAccount()}/history"
        val quantity = preferences.getQuantityHistory()

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
                                                        adapter.addQuery(
                                                            Query(
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
            listHistory.layoutManager = LinearLayoutManager(requireContext())
            listHistory.adapter = adapter

            buttonClearHistory.setOnClickListener {
                for (i in 1..quantity) {
                    setValue("${preferences.getAccount()}/bookmarks/$i/usage", false)
                }
            }
        }

        return binding.root
    }
}