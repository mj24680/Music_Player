package com.example.musicplayer.ui.activities


import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
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
        var isPlaying: Boolean = false
    }

    private lateinit var binding: ActivityPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.statusBar)

        initializeLayout()

        setupClickListeners()
    }

    private fun initializeLayout() {
        songPosition = intent.getIntExtra("index", 0)
        when (intent.getStringExtra("class")) {
            "MusicAdapter" -> {
                musicListPA = ArrayList()
                musicListPA.addAll(MusicFragment.MusicListMF)
                setLayout()
                createMediaPlayer()
            }

            "MainActivity" -> {
                musicListPA = ArrayList()
                musicListPA.addAll(MusicFragment.MusicListMF)
                musicListPA.shuffle()
                setLayout()
                createMediaPlayer()
            }
        }
    }

    private fun setLayout() {
        Glide.with(this)
            .load(musicListPA[songPosition].imgUri)
            .apply(RequestOptions().placeholder(R.drawable.ic_music_default))
            .into(binding.playerImage)
        binding.playerTitle.text = musicListPA[songPosition].title
        binding.playerSubtitle.text = musicListPA[songPosition].artist
    }

    private fun createMediaPlayer() {
        try {
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
            isPlaying = true
            binding.ivPlayPause.setImageResource(R.drawable.ic_pause)

        } catch (e: Exception) {
            return
        }
    }

    private fun setupClickListeners() {
        binding.ivPlayPause.setOnClickListener {
            if (isPlaying) {
                pauseMusic()
            } else {
                playMusic()
            }
        }

        binding.ivPrevious.setOnClickListener {
            preNextSong(false)
        }

        binding.ivNext.setOnClickListener {
            preNextSong(true)
        }

        binding.btnClose.setOnClickListener {
            finish()
        }
    }

    private fun playMusic() {
        binding.ivPlayPause.setImageResource(R.drawable.ic_pause)
        isPlaying = true
        mediaPlayer!!.start()
    }

    private fun pauseMusic() {
        binding.ivPlayPause.setImageResource(R.drawable.ic_play)
        isPlaying = false
        mediaPlayer!!.pause()
    }

    private fun preNextSong(increment: Boolean) { // increment -> if true then play next if false play previous
        if (increment) {
            setSongPosition(true)
            setLayout()
            createMediaPlayer()
        } else {
            setSongPosition(false)
            setLayout()
            createMediaPlayer()
        }
    }

    private fun setSongPosition(increment: Boolean) { // for first and end song
        if (increment) {
            if (musicListPA.size - 1 == songPosition) {
                songPosition = 0
            } else {
                ++songPosition
            }
        } else {
            if (0 == songPosition) {
                songPosition = musicListPA.size - 1
            } else {
                --songPosition
            }
        }
    }


}
