package com.example.musicplayer.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayer.databinding.MusicItemBinding
import com.example.musicplayer.models.Music


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
        holder.duration.text = musicList[position].duration.toString()
    }

    override fun getItemCount(): Int {
        return musicList.size
    }

    class ViewHolder(val binding: MusicItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val title = binding.tvMusic
        val album = binding.tvAlbum
        val image = binding.ivMusic
        val duration = binding.tvDuration
    }
}