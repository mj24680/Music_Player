package com.example.musicplayer.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.R
import com.example.musicplayer.activities.PlayerActivity
import com.example.musicplayer.databinding.MusicItemBinding
import com.example.musicplayer.models.Music
import com.example.musicplayer.models.formatDuration


class MusicAdapter(private val context: Context, private val musicList : ArrayList<Music>) : RecyclerView.Adapter<MusicAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = MusicItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.title.text = musicList[position].title
        holder.album.text = musicList[position].album
        holder.duration.text = formatDuration(musicList[position].duration)

        Glide.with(context)
            .load(musicList[position].imgUri)
            .apply(RequestOptions().placeholder(R.drawable.ic_music_default))
            .into(holder.image)

        holder.root.setOnClickListener {
            val intent = Intent(context, PlayerActivity::class.java)
            intent.putExtra("index", position)
            intent.putExtra("class", "MusicAdapter") // because player activity receive intent from different Classes
            ContextCompat.startActivity(context, intent, null)
        }
    }

    override fun getItemCount(): Int {
        return musicList.size
    }

    class ViewHolder(val binding: MusicItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val title = binding.tvMusic
        val album = binding.tvAlbum
        val image = binding.ivMusic
        val duration = binding.tvDuration
        val root = binding.root
    }
}