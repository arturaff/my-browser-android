package ru.arturprgr.mybrowser.ui.fragment.main

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import ru.arturprgr.mybrowser.adapter.HistoryAdapter
import ru.arturprgr.mybrowser.data.FirebaseHelper
import ru.arturprgr.mybrowser.data.SavesHelper
import ru.arturprgr.mybrowser.data.Singleton
import ru.arturprgr.mybrowser.databinding.FragmentHistoryBinding
import ru.arturprgr.mybrowser.model.Card

class HistoryFragment : Fragment() {
    private lateinit var binding: FragmentHistoryBinding
    private lateinit var savesHelper: SavesHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        savesHelper = SavesHelper(requireContext())
        HistoryFragment.context = requireContext()

        binding.apply {
            listHistory.layoutManager = LinearLayoutManager(requireContext())
            listHistory.adapter = Singleton.historyAdapter
            buttonClearHistory.setOnClickListener {
                FirebaseHelper("${savesHelper.getAccount()}/history")
                    .setValue(false)
                FirebaseHelper("${savesHelper.getAccount()}/history/quantity")
                    .setValue(0)
                savesHelper.setQuantityHistory(0)
                Singleton.historyAdapter = HistoryAdapter()
                listHistory.adapter = Singleton.historyAdapter
            }
        }

        return binding.root
    }
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }
}