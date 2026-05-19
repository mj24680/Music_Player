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
import androidx.fragment.app.Fragment
import com.example.musicplayer.R
import com.example.musicplayer.databinding.ActivityMainBinding
import com.example.musicplayer.fragments.HomeFragment
import com.example.musicplayer.fragments.MusicFragment
import com.example.musicplayer.fragments.PlaylistFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var homeFragment: HomeFragment
    private lateinit var musicFragment: MusicFragment
    private lateinit var playlistFragment: PlaylistFragment

    private var activeFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupFragments()

        requestPermission()

        window.statusBarColor = ContextCompat.getColor(this, R.color.statusBar)

        setupNavigation()

        // Show Home Fragment by default when app starts
        if (savedInstanceState == null) {
            switchFragment(homeFragment)
        }
    }

    private fun requestPermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(
                this,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), 10)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 10) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupNavigation() {
        // Set Home as selected UI state by default
        updateNavigationState("home")

        binding.cvHome.setOnClickListener {
            updateNavigationState("home")
            switchFragment(homeFragment)
        }

        binding.cvMusic.setOnClickListener {
            updateNavigationState("music")
            switchFragment(musicFragment)
        }

        binding.cvPlaylists.setOnClickListener {
            updateNavigationState("playlists")
            switchFragment(playlistFragment)
        }
    }

    private fun updateNavigationState(selected: String) {
        val selectedBg = Color.parseColor("#191320")
        val defaultBg = Color.parseColor("#2A1A3A")
        val selectedTint = Color.parseColor("#F44BF8")
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

    private fun setupFragments() {
        homeFragment = HomeFragment()
        musicFragment = MusicFragment()
        playlistFragment = PlaylistFragment()

        activeFragment = homeFragment

        supportFragmentManager.beginTransaction().add(R.id.fl_navigation, homeFragment, "HOME")
            .add(R.id.fl_navigation, musicFragment, "MUSIC").hide(musicFragment)
            .add(R.id.fl_navigation, playlistFragment, "PLAYLIST").hide(playlistFragment).commit()
    }

    private fun switchFragment(target: Fragment) {
        if (activeFragment == target) return

        activeFragment?.let { current ->
            supportFragmentManager.beginTransaction().setCustomAnimations(
                android.R.anim.fade_in, android.R.anim.fade_out
            ).hide(current).show(target).commit()
        }
        activeFragment = target
    }

    override fun onBackPressed() {
        if (activeFragment != homeFragment) {
            activeFragment = homeFragment
        } else {
            super.onBackPressed()
        }
    }

}
