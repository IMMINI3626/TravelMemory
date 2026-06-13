package com.example.travelmemory

import android.net.Uri
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TravelAdapter(
    private var records: MutableList<TravelRecord>,
    private val onItemClick: (TravelRecord) -> Unit,
    private val onEditClick: (TravelRecord) -> Unit,
    private val onDeleteClick: (TravelRecord) -> Unit
) : RecyclerView.Adapter<TravelAdapter.TravelViewHolder>() {

    private var selectedRecord: TravelRecord? = null

    inner class TravelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnCreateContextMenuListener {

        val imgThumbnail: ImageView = itemView.findViewById(R.id.imgThumbnail)
        val tvPlace: TextView = itemView.findViewById(R.id.tvPlace)
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val tvMemo: TextView = itemView.findViewById(R.id.tvMemo)
        val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)

        init {
            itemView.setOnClickListener {
                onItemClick(records[adapterPosition])
            }
            itemView.setOnLongClickListener {
                selectedRecord = records[adapterPosition]
                false
            }
            itemView.setOnCreateContextMenuListener(this)
        }

        override fun onCreateContextMenu(
            menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?
        ) {
            menu.setHeaderTitle("선택하세요")
            menu.add(Menu.NONE, CONTEXT_EDIT, Menu.NONE, "수정")
            menu.add(Menu.NONE, CONTEXT_DELETE, Menu.NONE, "삭제")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TravelViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_travel, parent, false)
        return TravelViewHolder(view)
    }

    override fun onBindViewHolder(holder: TravelViewHolder, position: Int) {
        val record = records[position]
        holder.tvPlace.text = record.place
        holder.tvDate.text = record.visitDate
        holder.tvMemo.text = record.memo.ifEmpty { "메모 없음" }

        if (!record.photoUri.isNullOrEmpty()) {
            holder.progressBar.visibility = View.VISIBLE
            holder.imgThumbnail.visibility = View.INVISIBLE

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val uri = Uri.parse(record.photoUri)
                    withContext(Dispatchers.Main) {
                        holder.imgThumbnail.setImageURI(uri)
                        holder.progressBar.visibility = View.GONE
                        holder.imgThumbnail.visibility = View.VISIBLE
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        holder.imgThumbnail.setImageResource(android.R.drawable.ic_menu_gallery)
                        holder.progressBar.visibility = View.GONE
                        holder.imgThumbnail.visibility = View.VISIBLE
                    }
                }
            }
        } else {
            holder.progressBar.visibility = View.GONE
            holder.imgThumbnail.visibility = View.VISIBLE
            holder.imgThumbnail.setImageResource(android.R.drawable.ic_menu_gallery)
        }
    }

    override fun getItemCount() = records.size

    fun updateList(newList: List<TravelRecord>) {
        records.clear()
        records.addAll(newList)
        notifyDataSetChanged()
    }

    fun getSelectedRecord() = selectedRecord

    fun handleContextMenu(itemId: Int): Boolean {
        val record = selectedRecord ?: return false
        return when (itemId) {
            CONTEXT_EDIT -> { onEditClick(record); true }
            CONTEXT_DELETE -> { onDeleteClick(record); true }
            else -> false
        }
    }

    companion object {
        const val CONTEXT_EDIT = 1001
        const val CONTEXT_DELETE = 1002
    }
}