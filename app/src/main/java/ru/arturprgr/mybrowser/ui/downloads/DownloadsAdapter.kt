package ru.arturprgr.mybrowser.ui.downloads

import android.app.AlertDialog
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.database.database
import ru.arturprgr.mybrowser.Preferences
import ru.arturprgr.mybrowser.R
import ru.arturprgr.mybrowser.databinding.LayoutLinkBinding


class DownloadsAdapter : RecyclerView.Adapter<DownloadsAdapter.ViewHolder>() {
    private val adapter = arrayListOf<Download>()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(download: Download) = with(LayoutLinkBinding.bind(itemView)) {
            textName.text = download.name
            textLink.text = download.path
            click.setOnClickListener {

            }
            buttonDelete.setOnClickListener {
                AlertDialog.Builder(download.context)
                    .setTitle("Удаление файла")
                    .setMessage("Вы точно хотите удалить этот файл")
                    .setNegativeButton("Ну уж нет!") { _: DialogInterface, _: Int -> }
                    .setPositiveButton("Да") { _: DialogInterface, _: Int ->
                        val reference = "${Preferences(download.context).getAccount()}/downloads"
                        Firebase.database.getReference("$reference/${download.index}/usage")
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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(adapter[position])
    }

    fun addDownload(download: Download) {
        adapter.add(download)
        notifyItemChanged(download.index)
        notifyItemRangeChanged(download.index, adapter.size)
    }
}