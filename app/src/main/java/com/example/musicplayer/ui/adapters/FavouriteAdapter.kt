package com.example.musicplayer.ui.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.R
import com.example.musicplayer.databinding.FavouriteItemBinding
import com.example.musicplayer.databinding.FragmentFavouriteBinding
import com.example.musicplayer.databinding.MusicItemBinding
import com.example.musicplayer.models.Music
import com.example.musicplayer.models.formatDuration
import com.example.musicplayer.ui.activities.PlayerActivity
import com.example.musicplayer.ui.activities.PlayerActivity.Companion.binding
import com.example.musicplayer.ui.activities.PlayerActivity.Companion.musicListPA
import com.example.musicplayer.ui.activities.PlayerActivity.Companion.songPosition

class FavouriteAdapter(private val context: Context, private var musicList: List<Music>) :
    RecyclerView.Adapter<FavouriteAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = FavouriteItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = musicList[position].title
        Glide.with(context)
            .load(musicList[position].imgUri)
            .apply(RequestOptions().placeholder(R.drawable.ic_music_default)).centerCrop()
            .into(holder.image)
        holder.root.setOnClickListener {
            if (musicList[position].id == PlayerActivity.nowPlayingId) {
                val intent = Intent(context, PlayerActivity::class.java)
                intent.putExtra("index", position)
                intent.putExtra("class", "NowPlaying")
                ContextCompat.startActivity(context, intent, null)
            } else {
                val intent = Intent(context, PlayerActivity::class.java)
                intent.putExtra("index", position)
                intent.putExtra("class", "FavouriteAdapter")
                ContextCompat.startActivity(context, intent, null)
            }
        }
    }

    override fun getItemCount(): Int = musicList.size

    class ViewHolder(binding: FavouriteItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val image = binding.fvrtImage
        val name = binding.fvrtTitle
        val root = binding.root
    }
}