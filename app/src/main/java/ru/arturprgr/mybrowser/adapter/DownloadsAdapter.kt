package ru.arturprgr.mybrowser.adapter

import android.app.AlertDialog
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.arturprgr.mybrowser.R
import ru.arturprgr.mybrowser.data.FirebaseHelper
import ru.arturprgr.mybrowser.data.SavesHelper
import ru.arturprgr.mybrowser.data.Singleton
import ru.arturprgr.mybrowser.databinding.LayoutLinkBinding
import ru.arturprgr.mybrowser.model.Card

class DownloadsAdapter : RecyclerView.Adapter<DownloadsAdapter.ViewHolder>() {
    private val adapter = arrayListOf<Card>()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(card: Card) = with(LayoutLinkBinding.bind(itemView)) {
            textName.text = card.name
            textLink.text = card.path
            buttonDelete.setOnClickListener {
                AlertDialog.Builder(card.context)
                    .setTitle("Удаление файла")
                    .setMessage("Вы точно хотите удалить этот файл из истории (не с устройства)")
                    .setPositiveButton("Да") { _: DialogInterface, _: Int ->
                        Singleton.downloadsAdapter.removeDownload(card)
                        FirebaseHelper("${SavesHelper(card.context).getAccount()}/downloads/${card.index}/usage")
                            .setValue(false)
                    }
                    .create()
                    .show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_link, parent, false))

    override fun getItemCount(): Int = adapter.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(adapter[position])

    fun addDownload(card: Card) {
        adapter.add(card)
        notifyItemInserted(card.index)
        notifyItemRangeChanged(card.index, adapter.size)
    }

    fun removeDownload(card: Card) {
        adapter.remove(card)
        notifyItemRemoved(card.index)
        notifyItemRangeChanged(card.index, adapter.size)
    }

    fun removeDownload(index: Int) {
        adapter.removeAt(index)
        notifyItemRemoved(index)
        notifyItemRangeChanged(index, adapter.size)
    }

    fun getSize(): Int = adapter.size
}