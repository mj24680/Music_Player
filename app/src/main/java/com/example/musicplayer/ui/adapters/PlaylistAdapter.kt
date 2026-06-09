package com.example.musicplayer.ui.adapters

import android.app.AlertDialog
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
import com.example.musicplayer.databinding.PlaylistItemBinding
import com.example.musicplayer.models.Playlist
import com.example.musicplayer.ui.activities.PlaylistDetails
import com.example.musicplayer.ui.activities.PlaylistDetails.Companion.currentPlaylistPos
import com.example.musicplayer.ui.fragments.PlaylistFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PlaylistAdapter(private val context: Context, private var playlistList: ArrayList<Playlist>) :
    RecyclerView.Adapter<PlaylistAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = PlaylistItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = playlistList[position].name
        holder.name.isSelected = true
        holder.delete.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(context)
            builder.setTitle(playlistList[position].name)
                .setMessage("Do You Want to Delete Playlist?")
                .setPositiveButton("Yes"){ dialog, _ ->
                    PlaylistFragment.musicPlaylist.ref.removeAt(position)
                    refreshPlaylist()
                    dialog.dismiss()
                }
                .setNegativeButton("No"){dialog, _ ->
                    dialog.dismiss()
                }
            val customDialog = builder.create()
            customDialog.show()
            customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.DKGRAY)
            customDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)
        }
        holder.root.setOnClickListener {
            val intent = Intent(context, PlaylistDetails::class.java)
            intent.putExtra("index", position)
            ContextCompat.startActivity(context, intent, null)
        }
        if(PlaylistFragment.musicPlaylist.ref[position].playlist.size > 0){
            Glide.with(context)
                .load(PlaylistFragment.musicPlaylist.ref[position].playlist[0].imgUri)
                .apply ( RequestOptions().placeholder(R.drawable.ic_music_default) )
                .into(holder.image)
        } else {
            Glide.with(context)
                .load(R.drawable.ic_music_default)
                .apply ( RequestOptions().placeholder(R.drawable.ic_music_default) )
                .into(holder.image)
        }
    }

    override fun getItemCount(): Int = playlistList.size

    class ViewHolder(binding: PlaylistItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val image = binding.imgPlaylist
        val name = binding.tvPlaylistName
        val root = binding.root
        val delete = binding.btnDelete
    }
    fun refreshPlaylist(){
        playlistList = ArrayList()
        playlistList.addAll(PlaylistFragment.musicPlaylist.ref)
        notifyDataSetChanged()
    }
}