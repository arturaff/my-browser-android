package ru.arturprgr.mybrowser.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import ru.arturprgr.mybrowser.adapter.HistoryAdapter
import ru.arturprgr.mybrowser.classes.Database
import ru.arturprgr.mybrowser.classes.Preferences
import ru.arturprgr.mybrowser.databinding.FragmentHistoryBinding
import ru.arturprgr.mybrowser.model.Query

class HistoryFragment : Fragment() {
    private lateinit var binding: FragmentHistoryBinding
    private val adapter = HistoryAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)

        val preferences = Preferences(requireContext())
        val reference = "${preferences.getAccount()}/history"
        val quantity = preferences.getQuantityHistory()

        if (quantity != 0) for (q in quantity downTo 0)
            Database("${reference}/$q/usage").getValue { usage ->
                if (usage.toBoolean()) Database("$reference/$q/name").getValue { name ->
                    Database("$reference/$q/url").getValue { url ->
                        adapter.addQuery(
                            Query(requireContext(), name, url, q)
                        )
                    }
                }
            }

        binding.apply {
            listHistory.layoutManager = LinearLayoutManager(requireContext())
            listHistory.adapter = adapter

            buttonClearHistory.setOnClickListener {
                for (i in 1..quantity) Database("${preferences.getAccount()}/history/$i/usage")
                    .setValue(false)
            }
        }

        return binding.root
    }
}