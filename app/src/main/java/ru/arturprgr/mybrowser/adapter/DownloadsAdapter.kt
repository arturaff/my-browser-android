package ru.arturprgr.mybrowser.adapter

import android.app.AlertDialog
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.database.database
import ru.arturprgr.mybrowser.classes.Preferences
import ru.arturprgr.mybrowser.R
import ru.arturprgr.mybrowser.classes.Database
import ru.arturprgr.mybrowser.databinding.LayoutLinkBinding
import ru.arturprgr.mybrowser.model.Download


class DownloadsAdapter : RecyclerView.Adapter<DownloadsAdapter.ViewHolder>() {
    private val adapter = arrayListOf<Download>()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(download: Download) = with(LayoutLinkBinding.bind(itemView)) {
            textName.text = download.name
            textLink.text = download.path

            buttonDelete.setOnClickListener {
                AlertDialog.Builder(download.context)
                    .setTitle("Удаление файла")
                    .setMessage("Вы точно хотите удалить этот файл из истории (не с устройства)")
                    .setPositiveButton("Да") { _: DialogInterface, _: Int ->
                        Database("${Preferences(download.context).getAccount()}/downloads/${download.index}/usage")
                            .setValue(false)
                    }
                    .create()
                    .show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_link, parent, false
            )
        )

    override fun getItemCount(): Int = adapter.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(adapter[position])

    fun addDownload(download: Download) {
        adapter.add(download)
        notifyItemChanged(download.index)
        notifyItemRangeChanged(download.index, adapter.size)
    }
}