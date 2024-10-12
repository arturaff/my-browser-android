package ru.arturprgr.mybrowser.data

import ru.arturprgr.mybrowser.adapter.BookmarksAdapter
import ru.arturprgr.mybrowser.adapter.DownloadsAdapter
import ru.arturprgr.mybrowser.adapter.HistoryAdapter

class Singleton {
    companion object {
        var historyAdapter = HistoryAdapter()
        val bookmarksAdapter = BookmarksAdapter()
        var downloadsAdapter = DownloadsAdapter()
    }
}