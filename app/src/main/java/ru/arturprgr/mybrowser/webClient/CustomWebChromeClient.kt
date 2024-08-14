package ru.arturprgr.mybrowser.webClient

import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import ru.arturprgr.mybrowser.databinding.ActivityWebBinding
import ru.arturprgr.mybrowser.ui.activity.WebActivity

class CustomWebChromeClient(val binding: ActivityWebBinding) :
    WebChromeClient() {
    private var customView: View? = null
    private var customViewCallback: CustomViewCallback? = null

    override fun onShowCustomView(view: View, callback: CustomViewCallback) = with(binding) {
        webView.visibility = View.GONE
        fullscreenContainer.visibility = View.VISIBLE
        fullscreenContainer.addView(view)
        customView = view
        customViewCallback = callback
    }

    override fun onHideCustomView() = with(binding) {
        fullscreenContainer.removeView(customView)
        customViewCallback!!.onCustomViewHidden()
        customView = null
        webView.visibility = View.VISIBLE
        fullscreenContainer.visibility = View.GONE
    }

    override fun onProgressChanged(view: WebView?, newProgress: Int) {
        binding.textQuery.text = view!!.title
        binding.progressBar.progress = newProgress
        if (binding.progressBar.progress == 100) binding.progressBar.visibility = View.GONE
        else binding.progressBar.visibility = View.VISIBLE
        super.onProgressChanged(view, newProgress)
    }
}