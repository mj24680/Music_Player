package com.example.musicplayer.ui.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Color
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
import com.example.musicplayer.ui.activities.PlaylistDetails
import com.example.musicplayer.ui.fragments.PlaylistFragment

class MusicAdapter(private val context: Context, private var musicList: List<Music>, private val playlistDetails: Boolean = false,
private val selectionActivity: Boolean = false ) : RecyclerView.Adapter<MusicAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = MusicItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val song = musicList[position]

        // for selected song state handling
        if (selectionActivity) {
            if (isSongInPlaylist(song)) {
                holder.root.setBackgroundColor(
                    ContextCompat.getColor(context, R.color.nav_selected_tint)
                )
            } else {
                holder.root.setBackgroundColor(Color.TRANSPARENT)
            }
        }

        holder.title.text = song.title
        holder.album.text = song.album
        holder.duration.text = formatDuration(song.duration)

        Glide.with(context)
            .load(song.imgUri)
            .apply(RequestOptions().placeholder(R.drawable.ic_music_default))
            .into(holder.image)

        when{
            playlistDetails -> {
                holder.root.setOnClickListener {
                    val intent = Intent(context, PlayerActivity::class.java)
                    intent.putExtra("index", position)
                    intent.putExtra("class", "PlaylistDetailsAdapter")
                    ContextCompat.startActivity(context, intent, null)
                }
            }

            selectionActivity -> {
                holder.root.setOnClickListener {
                    addSong(song)
                    notifyItemChanged(holder.adapterPosition)
                }
            }

            else -> {
                holder.root.setOnClickListener {
                    if(song.id == PlayerActivity.nowPlayingId){
                        val intent = Intent(context, PlayerActivity::class.java)
                        intent.putExtra("index", PlayerActivity.songPosition)
                        intent.putExtra("class", "NowPlaying")
                        ContextCompat.startActivity(context, intent, null)
                    }
                    else{
                        val intent = Intent(context, PlayerActivity::class.java)
                        intent.putExtra("index", position)
                        intent.putExtra("class", "MusicAdapter")
                        ContextCompat.startActivity(context, intent, null)
                    }

                }
            }
        }


    }

    private fun addSong(song: Music): Boolean {
        PlaylistFragment.musicPlaylist.ref[PlaylistDetails.currentPlaylistPos].playlist.forEachIndexed { index, music ->
            if(song.id == music.id){
                PlaylistFragment.musicPlaylist.ref[PlaylistDetails.currentPlaylistPos].playlist.removeAt(index)
                return false
            }
        }
        PlaylistFragment.musicPlaylist.ref[PlaylistDetails.currentPlaylistPos].playlist.add(song)
        return true
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

    fun refreshPlaylist(){
        musicList = ArrayList()
        musicList = PlaylistFragment.musicPlaylist.ref[PlaylistDetails.currentPlaylistPos].playlist
        notifyDataSetChanged()
    }

    private fun isSongInPlaylist(song: Music): Boolean {
        return PlaylistFragment.musicPlaylist
            .ref[PlaylistDetails.currentPlaylistPos]
            .playlist
            .any { it.id == song.id }
    }
}
