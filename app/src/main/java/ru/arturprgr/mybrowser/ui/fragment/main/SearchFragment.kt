package ru.arturprgr.mybrowser.ui.fragment.main

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import ru.arturprgr.mybrowser.databinding.FragmentSearchBinding
import ru.arturprgr.mybrowser.getDefaultString
import ru.arturprgr.mybrowser.ui.activity.SettingsActivity
import ru.arturprgr.mybrowser.ui.activity.WebActivity

class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private var query: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        binding.apply {
            if (getDefaultString(requireContext(), "def_search_system") != "") spinnerSystem.visibility = View.GONE
            else editQuery.updateLayoutParams<ConstraintLayout.LayoutParams> {
                this.topMargin = 0
                this.bottomMargin = 0
                this.rightMargin = 0
                this.leftMargin = 0
            }

            buttonSettings.setOnClickListener {
                startActivity(Intent(requireContext(), SettingsActivity::class.java))
            }

            editQuery.setOnEditorActionListener { _, actionId, event ->
                if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_SEARCH) search()
                true
            }

            buttonQuery.setOnClickListener {
                search()
            }
        }

        return binding.root
    }

    private fun search() = with(binding) {
        query = editQuery.text.toString()
        if (query != "") if (spinnerSystem.visibility == View.VISIBLE) {
            when ("${spinnerSystem.selectedItem}") {
                "Google" -> search("https://www.google.com/search?q=")
                "Яндекс" -> search("https://ya.ru/search/?text=")
                "DuckDuckGo" -> search("https://duckduckgo.com/q=")
                "Bing" -> search("https://www.bing.com/search?q=")
                "Yahoo!" -> search("https://search.yahoo.com/search?p=")
            }
        } else search(getDefaultString(requireContext(), "def_search_system"))
    }

    private fun search(searchSystem: String) {
        requireContext().startActivity(
            Intent(context, WebActivity::class.java).putExtra("url", "$searchSystem$query")
                .putExtra("name", query)
        )
        binding.editQuery.setText("")
    }
}