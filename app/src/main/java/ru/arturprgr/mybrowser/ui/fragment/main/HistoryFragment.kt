package ru.arturprgr.mybrowser.ui.fragment.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import ru.arturprgr.mybrowser.adapter.HistoryAdapter
import ru.arturprgr.mybrowser.data.FirebaseHelper
import ru.arturprgr.mybrowser.data.Preferences
import ru.arturprgr.mybrowser.databinding.FragmentHistoryBinding
import ru.arturprgr.mybrowser.model.Card

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
            FirebaseHelper("${reference}/$q/usage").getValue { usage ->
                if (usage.toBoolean()) FirebaseHelper("$reference/$q/name").getValue { name ->
                    FirebaseHelper("$reference/$q/url").getValue { url ->
                        adapter.addQuery(Card(requireContext(), name, url, q))
                    }
                }
            }

        binding.apply {
            listHistory.layoutManager = LinearLayoutManager(requireContext())
            listHistory.adapter = adapter
            buttonClearHistory.setOnClickListener {
                for (i in 1..quantity) FirebaseHelper("${preferences.getAccount()}/history/$i/usage")
                    .setValue(false)
            }
        }

        return binding.root
    }
}