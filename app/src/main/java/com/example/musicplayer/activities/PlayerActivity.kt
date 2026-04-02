package com.example.musicplayer.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.musicplayer.R
import com.example.musicplayer.databinding.ActivityPlayerBinding

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private var isPlaying = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Change the status bar color to match the player screen theme
        window.statusBarColor = ContextCompat.getColor(this, R.color.statusBar)

        // Receive data passed from the previous screen
        val title = intent.getStringExtra("itemTitle")
        val subtitle = intent.getStringExtra("itemSubtitle")
        val imageRes = intent.getIntExtra("itemImage", 0)

        // Set the received data to the views
        binding.playerTitle.text = title
        binding.playerSubtitle.text = subtitle
        binding.playerImage.setImageResource(imageRes)

        // Close button simply finishes this activity and returns to previous screen
        binding.btnClose.setOnClickListener {
            finish()
        }

        // Toggle play/pause state and update UI
        binding.ivPlayPause.setOnClickListener {
            if (isPlaying) {
                binding.ivPlayPause.setImageResource(R.drawable.ic_playcapital)
                isPlaying = false
            } else {
                binding.ivPlayPause.setImageResource(R.drawable.ic_pause)
                isPlaying = true
            }
        }
    }
}