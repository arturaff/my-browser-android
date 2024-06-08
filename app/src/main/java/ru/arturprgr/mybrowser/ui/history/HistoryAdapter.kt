package ru.arturprgr.mybrowser.ui.history

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

private val adapter: ArrayList<Query> = arrayListOf()

class HistoryAdapter : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(query: Query) = with(LayoutLinkBinding.bind(itemView)) {
            textName.text = query.name
            textLink.text = query.url
            click.setOnClickListener {
                query.context.startActivity(
                    Intent(
                        query.context,
                        WebActivity::class.java
                    ).putExtra("url", query.url).putExtra("name", query.name)
                )
            }
            buttonDelete.setOnClickListener {
                AlertDialog.Builder(query.context)
                    .setTitle("Удаление вкладки")
                    .setMessage("Вы точно хотите удалить эту вкладку из истории")
                    .setNegativeButton("Ну уж нет!") { _: DialogInterface, _: Int -> }
                    .setPositiveButton("Да") { _: DialogInterface, _: Int ->
                        val reference = "${Preferences(query.context).getAccount()}/history"
                        Firebase.database.getReference("$reference/${query.index}/usage")
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

    fun addQuery(query: Query) {
        adapter.add(query)
        notifyItemInserted(query.index)
        notifyItemRangeChanged(query.index, adapter.size)
    }
}