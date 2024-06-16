package ru.arturprgr.mybrowser.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import ru.arturprgr.mybrowser.adapter.DownloadsAdapter
import ru.arturprgr.mybrowser.classes.Database
import ru.arturprgr.mybrowser.classes.Preferences
import ru.arturprgr.mybrowser.databinding.FragmentDownloadsBinding
import ru.arturprgr.mybrowser.model.Download

class DownloadsFragment : Fragment() {
    private lateinit var binding: FragmentDownloadsBinding
    private val adapter = DownloadsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentDownloadsBinding.inflate(inflater, container, false)

        val preferences = Preferences(requireContext())
        val reference = "${preferences.getAccount()}/downloads"
        val quantity = preferences.getQuantityDownloads()

        if (quantity != 0) for (q in quantity downTo 0)
            Database("$reference/$q/usage").getValue { usage ->
                if (usage.toBoolean()) Database("$reference/$q/name").getValue { name ->
                    Database("$reference/$q/path").getValue { path ->
                        adapter.addDownload(
                            Download(requireContext(), name, path, q)
                        )
                    }
                }
            }

        binding.apply {
            listDownloads.layoutManager = LinearLayoutManager(requireContext())
            listDownloads.adapter = adapter

            buttonClearDownloads.setOnClickListener {
                for (i in 1..quantity) Database("${preferences.getAccount()}/downloads/$i/usage")
                    .setValue(false)
            }
        }

        return binding.root
    }
}