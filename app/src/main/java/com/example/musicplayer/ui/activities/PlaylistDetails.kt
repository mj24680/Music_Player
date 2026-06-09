package com.example.musicplayer.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.R
import com.example.musicplayer.databinding.ActivityPlaylistDetailsBinding
import com.example.musicplayer.databinding.AddPlaylistDialogBinding
import com.example.musicplayer.models.MusicPlaylist
import com.example.musicplayer.models.checkPlaylist
import com.example.musicplayer.ui.adapters.MusicAdapter
import com.example.musicplayer.ui.fragments.FavouriteFragment
import com.example.musicplayer.ui.fragments.MusicFragment
import com.example.musicplayer.ui.fragments.PlaylistFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder
import kotlin.text.isNotEmpty

class PlaylistDetails : AppCompatActivity() {

    companion object {
        var currentPlaylistPos: Int = -1
    }

    lateinit var binding: ActivityPlaylistDetailsBinding
    lateinit var playlistAdapter: MusicAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPlaylistDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.statusBar)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.statusBar)

        // receive intent data
        currentPlaylistPos = intent.extras?.get("index") as Int

        // if song remove from device, remove it from shared preferences also
        PlaylistFragment.musicPlaylist.ref[currentPlaylistPos].playlist = checkPlaylist(PlaylistFragment.musicPlaylist.ref[currentPlaylistPos].playlist)

        binding.rvPlaylistMusic.setItemViewCacheSize(10)
        binding.rvPlaylistMusic.setHasFixedSize(true)
        binding.rvPlaylistMusic.layoutManager = LinearLayoutManager(this)
        playlistAdapter = MusicAdapter(
            this,
            PlaylistFragment.musicPlaylist.ref[currentPlaylistPos].playlist,
            playlistDetails = true
        )
        binding.rvPlaylistMusic.adapter = playlistAdapter

        binding.btnAddMusic.setOnClickListener {
            startActivity(Intent(this, SelectionActivity::class.java))
        }
        binding.btnRemoveAllMusic.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(this)
                builder.setTitle("Remove")
                    .setMessage("Do you want to remove all musics?")
                .setPositiveButton("YES, I Want") { dialog, _ ->
                    PlaylistFragment.musicPlaylist.ref[currentPlaylistPos].playlist.clear()
                    playlistAdapter.refreshPlaylist()
                    dialog.dismiss()
                }
                .show()
        }
    }

    override fun onResume() {
        super.onResume()
        binding.tvPlaylistName.text = PlaylistFragment.musicPlaylist.ref[currentPlaylistPos].name
        binding.tvCreatedBy.text = PlaylistFragment.musicPlaylist.ref[currentPlaylistPos].createdBy
        binding.tvCreatedAt.text = PlaylistFragment.musicPlaylist.ref[currentPlaylistPos].createdOn

        if (playlistAdapter.itemCount > 0) {
            Glide.with(this)
                .load(PlaylistFragment.musicPlaylist.ref[currentPlaylistPos].playlist[0].imgUri)
                .apply(RequestOptions().placeholder(R.drawable.ic_music_default).centerCrop())
                .into(binding.ivPlaylist)
        }
        playlistAdapter.notifyDataSetChanged()


        // store favourites data using shared preferences
        val editor = getSharedPreferences("FAVOURITES", MODE_PRIVATE).edit()
        // for favourites
        val jsonString = GsonBuilder().create().toJson(FavouriteFragment.favouriteSongs)
        editor.putString("FavouriteSongs", jsonString)

        // for playlists
        val jsonStringPlalist = GsonBuilder().create().toJson(PlaylistFragment.musicPlaylist)
        editor.putString("MusicPlaylist", jsonStringPlalist)

        editor.apply()

    }
}