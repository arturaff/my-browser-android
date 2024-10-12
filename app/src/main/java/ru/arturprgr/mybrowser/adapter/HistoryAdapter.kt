package ru.arturprgr.mybrowser.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
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
import ru.arturprgr.mybrowser.ui.activity.WebActivity

class HistoryAdapter : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {
    private val adapter: ArrayList<Card> = arrayListOf()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(card: Card) = with(LayoutLinkBinding.bind(itemView)) {
            textName.text = card.name
            textLink.text = card.path
            click.setOnClickListener {
                card.context.startActivity(
                    Intent(card.context, WebActivity::class.java)
                        .putExtra("url", card.path).putExtra("name", card.name)
                )
            }
            buttonDelete.setOnClickListener {
                AlertDialog.Builder(card.context)
                    .setTitle("Удаление вкладки")
                    .setMessage("Вы точно хотите удалить эту вкладку из истории")
                    .setPositiveButton("Да") { _: DialogInterface, _: Int ->
                        Singleton.historyAdapter.removeQuery(card)
                        FirebaseHelper("${SavesHelper(card.context).getAccount()}/history/${card.index}/usage")
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

    fun getList(): ArrayList<Card> = adapter

    fun addQuery(card: Card) {
        adapter.add(card)
        notifyItemInserted(card.index)
        notifyItemRangeChanged(card.index, adapter.size)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun removeQuery(card: Card) {
        adapter.remove(card)
        notifyDataSetChanged()
    }

    fun removeQuery(index: Int) {
        adapter.removeAt(index)
        notifyItemRemoved(index)
        notifyItemRangeChanged(index, adapter.size)
    }

    fun getSize(): Int = adapter.size
}