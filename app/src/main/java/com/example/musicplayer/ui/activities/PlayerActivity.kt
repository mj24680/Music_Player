package com.example.musicplayer.ui.activities


import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.musicplayer.R
import com.example.musicplayer.databinding.ActivityPlayerBinding
import com.example.musicplayer.models.Music
import com.example.musicplayer.ui.fragments.MusicFragment
import com.example.musicplayer.viewmodel.MusicViewModel

class PlayerActivity : AppCompatActivity() {

    // access without creating an object
    // class level (static like) variables and functions
    companion object {
        lateinit var musicListPA: ArrayList<Music> // mutable
        var songPosition: Int = 0
        var mediaPlayer: MediaPlayer? = null
    }

    private lateinit var binding: ActivityPlayerBinding
    private lateinit var musicViewModel: MusicViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.statusBar)

        musicViewModel = ViewModelProvider(this)[MusicViewModel::class.java]

        songPosition = intent.getIntExtra("index", 0)
        when (intent.getStringExtra("class")) {
            "MusicAdapter" -> {
                musicListPA = ArrayList()
                musicListPA.addAll(MusicFragment.MusicListMF)
                // MediaPlayer Class in android -> to play music
                if (mediaPlayer == null) {
                    mediaPlayer = MediaPlayer()
                }

                mediaPlayer!!.apply {
                    reset() // currently song is playing, need to reset first
                    setDataSource(musicListPA[songPosition].path)
                    prepare()
                    start()
                }
            }
        }

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.ivPlayPause.setOnClickListener {

        }

        binding.ivPrevious.setOnClickListener {

        }

        binding.ivNext.setOnClickListener {

        }

        binding.btnClose.setOnClickListener {
            finish()
        }
    }
}
