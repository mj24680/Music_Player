package com.example.musicplayer.activities

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.example.musicplayer.R
import com.example.musicplayer.adapters.PlaylistAdapter
import com.example.musicplayer.adapters.TrendingAdapter
import com.example.musicplayer.databinding.ActivityMainBinding
import com.example.musicplayer.models.TrendingModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var playlistAdapter: PlaylistAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestPermission()

        window.statusBarColor = ContextCompat.getColor(this, R.color.statusBar)

        setupNavigation()
        setupPlaylist()
        setupTrending()
    }

    private fun requestPermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), 10)
        }
    }

    // handle response
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 10) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                // You can call a function here to load your music
            } else {
                val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    Manifest.permission.READ_MEDIA_AUDIO
                } else {
                    Manifest.permission.READ_EXTERNAL_STORAGE
                }
                ActivityCompat.requestPermissions(this, arrayOf(permission), 10)
            }
        }
    }

    private fun setupNavigation() {

        // Set Home as selected by default
        updateNavigationState("home")

        binding.cvHome.setOnClickListener {
            updateNavigationState("home")
        }

        binding.cvMusic.setOnClickListener {
            updateNavigationState("music")
        }

        binding.cvPlaylists.setOnClickListener {
            updateNavigationState("playlists")
        }
    }

    private fun updateNavigationState(selected: String) {

        val selectedBg = Color.parseColor("#111017")
        val defaultBg = Color.parseColor("#2A1A3A")
        val selectedTint = Color.parseColor("#FF00FF")
        val defaultTint = Color.WHITE

        // Reset all buttons to default state
        binding.cvHome.setCardBackgroundColor(defaultBg)
        binding.ivHome.setColorFilter(defaultTint)

        binding.cvMusic.setCardBackgroundColor(defaultBg)
        binding.ivMusicBtn.setColorFilter(defaultTint)
        binding.tvMusicBtn.setTextColor(defaultTint)

        binding.cvPlaylists.setCardBackgroundColor(defaultBg)
        binding.ivPlaylistsBtn.setColorFilter(defaultTint)
        binding.tvPlaylistsBtn.setTextColor(defaultTint)

        // Apply selected state
        when (selected) {

            "home" -> {
                binding.cvHome.setCardBackgroundColor(selectedBg)
                binding.ivHome.setColorFilter(selectedTint)
            }

            "music" -> {
                binding.cvMusic.setCardBackgroundColor(selectedBg)
                binding.ivMusicBtn.setColorFilter(selectedTint)
                binding.tvMusicBtn.setTextColor(selectedTint)
            }

            "playlists" -> {
                binding.cvPlaylists.setCardBackgroundColor(selectedBg)
                binding.ivPlaylistsBtn.setColorFilter(selectedTint)
                binding.tvPlaylistsBtn.setTextColor(selectedTint)
            }
        }
    }

    private fun setupPlaylist() {

        val images = listOf(
            R.drawable.thumbnail1,
            R.drawable.thumbnail2,
            R.drawable.thumbnail3,
            R.drawable.thumbnail4,
            R.drawable.thumbnail5
        )

        playlistAdapter = PlaylistAdapter(images)

        binding.rvPlaylist.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        binding.rvPlaylist.adapter = playlistAdapter

        PagerSnapHelper().attachToRecyclerView(binding.rvPlaylist)
    }

    private fun setupTrending() {

        val trendingItemList = listOf(
            TrendingModel("Ghost", "Justin Bieber", R.drawable.ic_music1),
            TrendingModel("Blinding Lights", "The Weeknd", R.drawable.ic_music2),
            TrendingModel("Shape of You", "Ed Sheeran", R.drawable.ic_music3),
            TrendingModel("Levitating", "Dua Lipa", R.drawable.ic_music4),
            TrendingModel("Stay", "The Kid LAROI", R.drawable.ic_music5),
            TrendingModel("Someone You Loved", "Lewis Capaldi", R.drawable.ic_music6),
            TrendingModel("Believer", "Imagine Dragons", R.drawable.ic_music7),
            TrendingModel("Perfect", "Ed Sheeran", R.drawable.ic_music8)
        )

        val gridLayoutManager =
            GridLayoutManager(this, 2, GridLayoutManager.HORIZONTAL, false)

        binding.rvTrending.layoutManager = gridLayoutManager

        binding.rvTrending.adapter =
            TrendingAdapter(trendingItemList) { clickedItem ->

                // Update playbar UI with selected song
                binding.ivPlaybarThumb.setImageResource(clickedItem.imageRes)
                binding.tvPlaybarTitle.text = clickedItem.title
                binding.tvPlaybarSubtitle.text = clickedItem.subtitle
            }
    }

}
