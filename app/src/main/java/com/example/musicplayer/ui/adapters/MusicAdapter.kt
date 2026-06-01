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
import com.example.musicplayer.databinding.MusicItemBinding
import com.example.musicplayer.models.Music
import com.example.musicplayer.models.formatDuration
import com.example.musicplayer.ui.activities.PlayerActivity

class MusicAdapter(private val context: Context, private var musicList: List<Music>) : RecyclerView.Adapter<MusicAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = MusicItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val song = musicList[position]

        holder.title.text = song.title
        holder.album.text = song.album
        holder.duration.text = formatDuration(song.duration)

        Glide.with(context)
            .load(song.imgUri)
            .apply(RequestOptions().placeholder(R.drawable.ic_music_default))
            .into(holder.image)

        holder.root.setOnClickListener {
            val intent = Intent(context, PlayerActivity::class.java)
            intent.putExtra("index", position)
            intent.putExtra("class", "MusicAdapter")
            ContextCompat.startActivity(context, intent, null)
        }
    }

    override fun getItemCount(): Int = musicList.size

    class ViewHolder(binding: MusicItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val title = binding.tvMusic
        val album = binding.tvAlbum
        val image = binding.ivMusic
        val duration = binding.tvDuration
        val root = binding.root
    }

    fun updateList(newList: List<Music>) {
        musicList = newList
        notifyDataSetChanged()
    }
}
