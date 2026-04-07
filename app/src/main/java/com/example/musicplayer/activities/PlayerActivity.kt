package com.example.musicplayer.activities

import android.media.MediaPlayer
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.musicplayer.R
import com.example.musicplayer.databinding.ActivityPlayerBinding
import com.example.musicplayer.fragments.MusicFragment
import com.example.musicplayer.models.Music

class PlayerActivity : AppCompatActivity() {

    companion object{
        lateinit var musicListPA : ArrayList<Music>
        var musicPosition : Int = 0
        var mediaPlayer : MediaPlayer ?= null
    }

    private lateinit var binding: ActivityPlayerBinding
    private var isPlaying = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.statusBar)

        musicPosition = intent.getIntExtra("index", 0)

        when(intent.getStringExtra("class")){
            "MusicAdapter" -> {
                musicListPA = ArrayList()
                musicListPA.addAll(MusicFragment.MusicListMF)

                // to play an audio file, there is a class call media player in android
                if(mediaPlayer == null){
                    mediaPlayer = MediaPlayer()
                }
                mediaPlayer!!.reset() // if media player playing a song, it is important to reset it
                mediaPlayer!!.setDataSource(musicListPA[musicPosition].path)
                mediaPlayer!!.prepare()
                mediaPlayer!!.start()
            }
        }












        // Receive data passed from the previous screen
        val title = intent.getStringExtra("itemTitle")
        val subtitle = intent.getStringExtra("itemSubtitle")
        val imageRes = intent.getIntExtra("itemImage", 0)

        // Set the received data to the views
        binding.playerTitle.text = title
        binding.playerSubtitle.text = subtitle
        if (imageRes == 0) {
            binding.playerImage.setImageResource(R.drawable.ic_music_default)
        } else {
            binding.playerImage.setImageResource(imageRes)
        }


        binding.btnClose.setOnClickListener {
            finish()
        }

        // Toggle play/pause state
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