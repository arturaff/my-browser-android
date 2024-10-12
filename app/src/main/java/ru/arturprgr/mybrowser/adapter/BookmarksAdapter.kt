package ru.arturprgr.mybrowser.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
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

class BookmarksAdapter : RecyclerView.Adapter<BookmarksAdapter.ViewHolder>() {
    private val adapter = arrayListOf<Card>()

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
                AlertDialog.Builder(card.context).setTitle("Удаление закладки")
                    .setMessage("Вы точно хотите удалить эту вкладку из закладок")
                    .setPositiveButton("Да") { _, _ ->
                        Singleton.bookmarksAdapter.removeBookmark(card)
                        FirebaseHelper("${SavesHelper(card.context).getAccount()}/bookmarks/${card.index}/usage")
                            .setValue(false)
                    }.create().show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_link, parent, false))

    override fun getItemCount(): Int = getSize()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(adapter[position])

    fun addBookmark(card: Card) {
        adapter.add(card)
        notifyItemInserted(card.index)
        notifyItemRangeChanged(card.index, adapter.size)
    }

    fun removeBookmark(card: Card) {
        adapter.remove(card)
        notifyItemRemoved(card.index)
        notifyItemRangeChanged(card.index, adapter.size)
    }

    fun getSize(): Int = adapter.size
}