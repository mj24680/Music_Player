package com.example.musicplayer.ui.activities


import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.os.Bundle
import android.os.IBinder
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.R
import com.example.musicplayer.databinding.ActivityPlayerBinding
import com.example.musicplayer.models.Music
import com.example.musicplayer.models.formatDuration
import com.example.musicplayer.models.setSongPosition
import com.example.musicplayer.services.MusicService
import com.example.musicplayer.ui.fragments.MusicFragment
import com.example.musicplayer.viewmodel.MusicViewModel

class PlayerActivity : AppCompatActivity(), ServiceConnection, MediaPlayer.OnCompletionListener {

    // access without creating an object
    // class level (static like) variables and functions
    companion object {
        lateinit var musicListPA: ArrayList<Music> // mutable
        var songPosition: Int = 0

        // var mediaPlayer: MediaPlayer? = null
        var isPlaying: Boolean = false

        var musicService: MusicService? = null

        lateinit var binding: ActivityPlayerBinding
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // for starting service
        val intent = Intent(this, MusicService::class.java)
        ContextCompat.startForegroundService(this, intent)
        bindService(intent, this, BIND_AUTO_CREATE)

        window.statusBarColor = ContextCompat.getColor(this, R.color.statusBar)

        initializeLayout()

        setupClickListeners()

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean // yh function btata h k user ny kuch changes kiye hain, ya bus click he kiya
            ) {
                if (fromUser) musicService!!.mediaPlayer!!.seekTo(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) =
                Unit // this calls, when user click on seekbar

            override fun onStopTrackingTouch(seekBar: SeekBar?) =
                Unit  // and this calls, when user leave click
        }) // this call immediately, when user click on seekBar
    }

    private fun initializeLayout() {
        songPosition = intent.getIntExtra("index", 0)
        when (intent.getStringExtra("class")) {
            "MusicAdapter" -> {
                musicListPA = ArrayList()
                musicListPA.addAll(MusicFragment.MusicListMF)
                setLayout()
                // createMediaPlayer()
            }

            "MainActivity" -> {
                musicListPA = ArrayList()
                musicListPA.addAll(MusicFragment.MusicListMF)
                musicListPA.shuffle()
                setLayout()
                // createMediaPlayer()
            }
        }
    }

    private fun setLayout() {
        Glide.with(this)
            .load(musicListPA[songPosition].imgUri)
            .apply(RequestOptions().placeholder(R.drawable.ic_music_default)).centerCrop()
            .into(binding.playerImage)
        binding.playerTitle.text = musicListPA[songPosition].title
        binding.playerSubtitle.text = musicListPA[songPosition].artist
    }

    private fun createMediaPlayer() {
        try {
            // MediaPlayer Class in android -> to play music
            if (musicService!!.mediaPlayer == null) {
                musicService!!.mediaPlayer = MediaPlayer()
            }

            musicService!!.mediaPlayer!!.apply {
                reset() // currently song is playing, need to reset first
                setDataSource(musicListPA[songPosition].path)
                prepare()
                start()
            }
            isPlaying = true
            binding.ivPlayPause.setImageResource(R.drawable.ic_pause)
            musicService!!.showNotification("Pause")
            // music current and end time
            binding.tvSeekBarStart.text =
                formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
            binding.tvSeekBarEnd.text =
                formatDuration(musicService!!.mediaPlayer!!.duration.toLong())
            binding.seekBar.progress = 0 // initial 0
            binding.seekBar.max = musicService!!.mediaPlayer!!.duration
            musicService!!.mediaPlayer!!.setOnCompletionListener(this)
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
        musicService!!.showNotification("Pause")
        isPlaying = true
        musicService!!.mediaPlayer!!.start()
    }

    private fun pauseMusic() {
        binding.ivPlayPause.setImageResource(R.drawable.ic_play)
        musicService!!.showNotification("Play")
        isPlaying = false
        musicService!!.mediaPlayer!!.pause()
    }

    private fun preNextSong(increment: Boolean) { // increment -> if true
        // then play next if false play previous
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

    override fun onServiceConnected(
        name: ComponentName?,
        service: IBinder?
    ) {
        val binder = service as MusicService.MyBinder
        musicService = binder.currentService()
        createMediaPlayer()
        musicService!!.seekBarSetup()
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        musicService = null
    }

    override fun onCompletion(mp: MediaPlayer?) {
        setSongPosition(increment = true)
        createMediaPlayer()
        try {
            setLayout()
        } catch (e: Exception) {
            return
        }
    }
}
