package com.example.musicplayer.activities

import android.media.MediaPlayer
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
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
    private var isPlaying = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.statusBar)

        initializeLayout()

        binding.ivPlayPause.setOnClickListener {
            if(isPlaying){
                pauseMusic()
            }else{
                playMusic()
            }
        }

        binding.btnClose.setOnClickListener {
            finish()
        }
    }

    private fun initializeLayout() {
        musicPosition = intent.getIntExtra("index", 0)

        when(intent.getStringExtra("class")){
            "MusicAdapter" -> {
                musicListPA = ArrayList()
                musicListPA.addAll(MusicFragment.MusicListMF)
                setLayout()
                createMediaPlayer()
            }
        }
    }

    private fun setLayout(){
        Glide.with(this)
            .load(musicListPA[musicPosition].imgUri)
            .apply(RequestOptions().placeholder(R.drawable.ic_music_default))
            .into(binding.PAMusicImage)

        binding.PAMusicName.text = musicListPA[musicPosition].title
    }

    private fun createMediaPlayer(){
        // to play an audio file, there is a class call media player in android
        try {
            if(mediaPlayer == null){
                mediaPlayer = MediaPlayer()
            }
            mediaPlayer!!.reset() // if media player playing a song, it is important to reset it
            mediaPlayer!!.setDataSource(musicListPA[musicPosition].path)
            mediaPlayer!!.prepare()
            mediaPlayer!!.start()
            isPlaying = true
            binding.ivPlayPause.setImageResource(R.drawable.ic_pause)
        }catch (e: Exception){
            return
        }
    }

    private fun playMusic(){
        binding.ivPlayPause.setImageResource(R.drawable.ic_pause)
        isPlaying = true
        mediaPlayer!!.start()
    }

    private fun pauseMusic(){
        binding.ivPlayPause.setImageResource(R.drawable.ic_play_capital)
        isPlaying = false
        mediaPlayer!!.pause()
    }

}