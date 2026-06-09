package com.example.musicplayer.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicplayer.R
import com.example.musicplayer.databinding.AddPlaylistDialogBinding
import com.example.musicplayer.databinding.FragmentPlaylistBinding
import com.example.musicplayer.models.MusicPlaylist
import com.example.musicplayer.models.Playlist
import com.example.musicplayer.ui.adapters.PlaylistAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class PlaylistFragment : Fragment() {

    companion object{
        var musicPlaylist = MusicPlaylist()
    }

    private lateinit var binding: FragmentPlaylistBinding
    private lateinit var playlistAdapter: PlaylistAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvPlaylist.apply {
            setHasFixedSize(true)
            setItemViewCacheSize(12)
            layoutManager = LinearLayoutManager(requireContext())
            playlistAdapter = PlaylistAdapter(requireContext(), musicPlaylist.ref)
            adapter = playlistAdapter
        }

        binding.floatingCreatePlaylist.setOnClickListener { customAlertDialog() }
    }

    private fun customAlertDialog(){
        val customDialog = layoutInflater.inflate(R.layout.add_playlist_dialog, null)
        val binder = AddPlaylistDialogBinding.bind(customDialog)
        val  builder = MaterialAlertDialogBuilder(requireContext())
        builder.setView(customDialog)
            .setPositiveButton("Create"){ dialog,_  ->
                val playlistName = binder.etPlaylistName.text
                val createdBy = binder.etCreatedBy.text
                if(playlistName!=null && createdBy!= null){
                    if(playlistName.isNotEmpty() && createdBy.isNotEmpty()){
                        addPlaylist(playlistName.toString(), createdBy.toString())
                    }
                }
                dialog.dismiss()
            }
            .show()
    }

    private fun addPlaylist(name: String, createdBy: String) {
        var playListExists = false
        for(i in musicPlaylist.ref){
            if(name.equals(i.name)){
                playListExists = true
            }
        }
        if(playListExists){
            Toast.makeText(requireContext(), "Playlist Already Exists", Toast.LENGTH_SHORT).show()
        }else{
            val tempPlaylist = Playlist()

            tempPlaylist.name = name
            tempPlaylist.playlist = ArrayList()
            tempPlaylist.createdBy = createdBy

            val calender = Calendar.getInstance().time
            val sdf = SimpleDateFormat("dd MMM yyy", Locale.ENGLISH)
            tempPlaylist.createdOn = sdf.format(calender)

            musicPlaylist.ref.add(tempPlaylist)
            playlistAdapter.refreshPlaylist()
        }
    }

    override fun onResume() {
        super.onResume()
        playlistAdapter.notifyDataSetChanged()
    }

}
