package com.example.musicplayer.adapters

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayer.R
import com.example.musicplayer.activities.PlayerActivity
import com.example.musicplayer.databinding.TrendingItemBinding
import com.example.musicplayer.models.TrendingModel

class TrendingAdapter(
    private val items: List<TrendingModel>,
    private val onItemClicked: (TrendingModel) -> Unit // callback to update playbar in MainActivity
) : RecyclerView.Adapter<TrendingAdapter.ViewHolder>() {

    // Keeps track of currently selected item
    private var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding = TrendingItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val currentItem = items[position]

        holder.title.text = currentItem.title
        holder.subtitle.text = currentItem.subtitle
        holder.image.setImageResource(currentItem.imageRes)

        // Highlight selected item
        if (position == selectedPosition) {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#2A1A3A"))
            holder.title.setTextColor(Color.parseColor("#FF00FF"))
        } else {
            holder.cardView.setCardBackgroundColor(Color.TRANSPARENT)
            holder.title.setTextColor(Color.WHITE)
        }

        holder.itemView.setOnClickListener {

            onItemClicked(currentItem)

            val context = holder.itemView.context
            val intent = Intent(context, PlayerActivity::class.java).apply {
                putExtra("itemTitle", currentItem.title)
                putExtra("itemSubtitle", currentItem.subtitle)
                putExtra("itemImage", currentItem.imageRes)
            }

            context.startActivity(intent)

            val oldPosition = selectedPosition
            selectedPosition = position

            // Refresh old and new selected items
            if (oldPosition != -1) {
                notifyItemChanged(oldPosition)
            }

            notifyItemChanged(selectedPosition)
        }
    }

    override fun getItemCount(): Int = items.size


    class ViewHolder(binding: TrendingItemBinding) : RecyclerView.ViewHolder(binding.root) {

        val cardView = binding.cvTrending
        val image = binding.itemImage
        val title = binding.itemTitle
        val subtitle = binding.itemSubtitle
    }
}