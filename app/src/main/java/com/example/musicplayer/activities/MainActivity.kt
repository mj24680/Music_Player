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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestPermission()

        window.statusBarColor = ContextCompat.getColor(this, R.color.statusBar)

        setupNavigation()

        // Show Home Fragment by default when app starts
        if (savedInstanceState == null) {
            replaceFragment(HomeFragment())
        }
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
            replaceFragment(HomeFragment())
        }

        binding.cvMusic.setOnClickListener {
            updateNavigationState("music")
            replaceFragment(MusicFragment())
        }

        binding.cvPlaylists.setOnClickListener {
            updateNavigationState("playlists")
            replaceFragment(PlaylistFragment())
        }
    }

    private fun updateNavigationState(selected: String) {
        val selectedBg = Color.parseColor("#111017")
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

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fl_navigation, fragment)
        fragmentTransaction.commit()
    }
}
