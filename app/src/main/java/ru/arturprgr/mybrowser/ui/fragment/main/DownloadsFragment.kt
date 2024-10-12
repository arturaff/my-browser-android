package ru.arturprgr.mybrowser.ui.fragment.main

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import ru.arturprgr.mybrowser.adapter.DownloadsAdapter
import ru.arturprgr.mybrowser.adapter.HistoryAdapter
import ru.arturprgr.mybrowser.data.FirebaseHelper
import ru.arturprgr.mybrowser.data.SavesHelper
import ru.arturprgr.mybrowser.data.Singleton
import ru.arturprgr.mybrowser.databinding.FragmentDownloadsBinding
import ru.arturprgr.mybrowser.model.Card

class DownloadsFragment : Fragment() {
    private lateinit var binding: FragmentDownloadsBinding
    private lateinit var savesHelper: SavesHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentDownloadsBinding.inflate(inflater, container, false)
        savesHelper = SavesHelper(requireContext())
        DownloadsFragment.context = requireContext()

        binding.apply {
            listDownloads.layoutManager = LinearLayoutManager(requireContext())
            listDownloads.adapter = Singleton.downloadsAdapter
            buttonClearDownloads.setOnClickListener {
                FirebaseHelper("${savesHelper.getAccount()}/downloads")
                    .setValue(false)
                FirebaseHelper("${savesHelper.getAccount()}/downloads/quantity")
                    .setValue(0)
                savesHelper.setQuantityDownloads(0)
                Singleton.downloadsAdapter = DownloadsAdapter()
                listDownloads.adapter = null
                listDownloads.adapter = Singleton.downloadsAdapter
            }
        }

        return binding.root
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }
}