package com.example.musicplayer.utils

import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat
import com.example.musicplayer.R
import com.example.musicplayer.databinding.ActivityMainBinding
import com.example.musicplayer.navigation.NavItems

class NavigationUIController(
    private val binding: ActivityMainBinding,
    private val context: Context
) {

    fun update(tab: NavItems) {

        val selectedBg = ContextCompat.getColor(context, R.color.nav_selected_bg)
        val defaultBg = ContextCompat.getColor(context, R.color.nav_default_bg)

        val selectedTint = ContextCompat.getColor(context, R.color.nav_selected_tint)
        val defaultTint = Color.WHITE

        reset(defaultBg, defaultTint)

        when (tab) {

            NavItems.HOME -> {
                binding.cvHome.setCardBackgroundColor(selectedBg)
                binding.ivHome.setColorFilter(selectedTint)
            }

            NavItems.MUSIC -> {
                binding.cvMusic.setCardBackgroundColor(selectedBg)
                binding.ivMusicBtn.setColorFilter(selectedTint)
            }

            NavItems.PLAYLIST -> {
                binding.cvPlaylists.setCardBackgroundColor(selectedBg)
                binding.ivPlaylistsBtn.setColorFilter(selectedTint)
            }
        }
    }

    private fun reset(bg: Int, tint: Int) {

        binding.cvHome.setCardBackgroundColor(bg)
        binding.cvMusic.setCardBackgroundColor(bg)
        binding.cvPlaylists.setCardBackgroundColor(bg)

        binding.ivHome.setColorFilter(tint)
        binding.ivMusicBtn.setColorFilter(tint)
        binding.ivPlaylistsBtn.setColorFilter(tint)
    }
}