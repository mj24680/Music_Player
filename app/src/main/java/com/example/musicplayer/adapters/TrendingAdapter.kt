package com.example.musicplayer.adapters

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayer.activities.PlayerActivity
import com.example.musicplayer.databinding.TrendingItemBinding
import com.example.musicplayer.models.TrendingModel

class TrendingAdapter(
    private val items: List<TrendingModel>,
    private val onItemClicked: (TrendingModel) -> Unit
) : RecyclerView.Adapter<TrendingAdapter.ViewHolder>() {

    private var selectedPosition = -1

    class ViewHolder(val binding: TrendingItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = TrendingItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.binding.apply {
            itemTitle.text = item.title
            itemSubtitle.text = item.subtitle
            itemImage.setImageResource(item.imageRes)

            // Change color if this item is selected
            if (position == selectedPosition) {
                cvTrending.setCardBackgroundColor(Color.parseColor("#2A1A3A"))
                itemTitle.setTextColor(Color.parseColor("#FF00FF"))
            } else {
                cvTrending.setCardBackgroundColor(Color.TRANSPARENT)
                itemTitle.setTextColor(Color.WHITE)
            }

            root.setOnClickListener {
                // Update MainActivity playbar
                onItemClicked(item)

                // Open PlayerActivity
                val context = root.context
                val intent = Intent(context, PlayerActivity::class.java).apply {
                    putExtra("itemTitle", item.title)
                    putExtra("itemSubtitle", item.subtitle)
                    putExtra("itemImage", item.imageRes)
                }
                context.startActivity(intent)

                // Update selected item highlighting
                val oldPosition = selectedPosition
                selectedPosition = holder.adapterPosition

                if (oldPosition != -1) notifyItemChanged(oldPosition)
                notifyItemChanged(selectedPosition)
            }
        }
    }

    override fun getItemCount() = items.size
}
