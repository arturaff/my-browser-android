package ru.arturprgr.mybrowser.ui.bookmarks

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.database.database
import ru.arturprgr.mybrowser.Preferences
import ru.arturprgr.mybrowser.R
import ru.arturprgr.mybrowser.WebActivity
import ru.arturprgr.mybrowser.databinding.LayoutLinkBinding

class BookmarksAdapter : RecyclerView.Adapter<BookmarksAdapter.ViewHolder>() {
    private val adapter = arrayListOf<Bookmark>()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(bookmark: Bookmark) = with(LayoutLinkBinding.bind(itemView)) {
            textName.text = bookmark.name
            textLink.text = bookmark.url

            click.setOnClickListener {
                bookmark.context.startActivity(
                    Intent(
                        bookmark.context,
                        WebActivity::class.java
                    ).putExtra("url", bookmark.url).putExtra("name", bookmark.name)
                )
            }

            buttonDelete.setOnClickListener {
                AlertDialog.Builder(bookmark.context)
                    .setTitle("Удаление закладки")
                    .setMessage("Вы точно хотите удалить эту вкладку из закладок")
                    .setNegativeButton("Ну уж нет!") { _: DialogInterface, _: Int -> }
                    .setPositiveButton("Да") { _: DialogInterface, _: Int ->
                        val reference = "${Preferences(bookmark.context).getAccount()}/bookmarks"
                        Firebase.database.getReference("$reference/${bookmark.index}/usage")
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

    fun addBookmark(bookmark: Bookmark) {
        adapter.add(bookmark)
        notifyItemInserted(bookmark.index)
        notifyItemRangeChanged(bookmark.index, adapter.size)
    }
}