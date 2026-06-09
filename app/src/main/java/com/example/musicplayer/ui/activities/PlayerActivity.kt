package com.example.musicplayer.ui.activities


import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.R
import com.example.musicplayer.databinding.ActivityPlayerBinding
import com.example.musicplayer.models.Music
import com.example.musicplayer.models.favouriteChecker
import com.example.musicplayer.models.formatDuration
import com.example.musicplayer.models.setSongPosition
import com.example.musicplayer.services.MusicService
import com.example.musicplayer.ui.adapters.FavouriteAdapter
import com.example.musicplayer.ui.fragments.FavouriteFragment
import com.example.musicplayer.ui.fragments.MusicFragment
import com.example.musicplayer.ui.fragments.PlaylistFragment
import java.io.File

class PlayerActivity : AppCompatActivity(), ServiceConnection, MediaPlayer.OnCompletionListener {

    // access without creating an object
    // class level (static like) variables and functions
    companion object {
        lateinit var musicListPA: ArrayList<Music> // mutable
        var songPosition: Int = 0

        // var mediaPlayer: MediaPlayer? = null
        var isPlaying: Boolean = false
        var nowPlayingId: String = ""

        var musicService: MusicService? = null

        lateinit var binding: ActivityPlayerBinding

        var repeat: Boolean = false

        var isFavourite: Boolean = false
        var fIndex: Int = -1
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.statusBar)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.statusBar)

        initializeLayout()

        setupClickListeners()

        binding.seekBar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener { // this call immediately, when user click on seekBar
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
        })

        binding.ivRepeat.setOnClickListener {
            if (!repeat) {
                repeat = true
                binding.ivRepeat.setColorFilter(
                    ContextCompat.getColor(
                        this,
                        R.color.nav_selected_tint
                    )
                )
                Toast.makeText(this, "Repeat Enabled", Toast.LENGTH_SHORT).show()
            } else {
                repeat = false
                binding.ivRepeat.setColorFilter(ContextCompat.getColor(this, R.color.white))
                Toast.makeText(this, "Repeat Disabled", Toast.LENGTH_SHORT).show()
            }
        }

        binding.ivShare.setOnClickListener {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type = "audio/*"
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(musicListPA[songPosition].path))
            startActivity(Intent.createChooser(shareIntent, "Sharing Music File"))
        }

        binding.ivFvrt.setOnClickListener {
            if (isFavourite) {
                // already in favourite, so remove
                isFavourite = false
                binding.ivFvrt.setImageResource(R.drawable.white_heart)
                FavouriteFragment.favouriteSongs.removeAt(fIndex)
                FavouriteFragment.favouriteAdapter?.notifyDataSetChanged()
            } else {
                isFavourite = true
                binding.ivFvrt.setImageResource(R.drawable.red_heart)
                FavouriteFragment.favouriteSongs.add(musicListPA[songPosition])
                fIndex = FavouriteFragment.favouriteSongs.size - 1
                FavouriteFragment.favouriteAdapter?.notifyDataSetChanged()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.playerTitle.isSelected = true
    }

    private fun initializeLayout() {
        songPosition = intent.getIntExtra("index", 0)
        when (intent.getStringExtra("class")) {
            "MusicAdapter" -> {
                startService()
                musicListPA = ArrayList()
                musicListPA.addAll(MusicFragment.MusicListMF)
                setLayout()
                // createMediaPlayer()
            }

            "MainActivity" -> {
                startService()
                musicListPA = ArrayList()
                musicListPA.addAll(MusicFragment.MusicListMF)
                musicListPA.shuffle()
                setLayout()
                // createMediaPlayer()
            }

            "NowPlaying" -> {
                setLayout()
                binding.tvSeekBarStart.text =
                    formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
                binding.tvSeekBarEnd.text =
                    formatDuration(musicService!!.mediaPlayer!!.duration.toLong())
                binding.seekBar.progress = musicService!!.mediaPlayer!!.currentPosition
                binding.seekBar.max = musicService!!.mediaPlayer!!.duration
                if (isPlaying) {
                    binding.ivPlayPause.setImageResource(R.drawable.ic_pause)
                } else {
                    binding.ivPlayPause.setImageResource(R.drawable.ic_play)
                }
            }

            "FavouriteAdapter" -> {
                startService()
                musicListPA = ArrayList()
                musicListPA.addAll(FavouriteFragment.favouriteSongs)
                setLayout()
            }

            "PlaylistDetailsAdapter" -> {
                startService()
                musicListPA = ArrayList()
                musicListPA.addAll(PlaylistFragment.musicPlaylist.ref[PlaylistDetails.currentPlaylistPos].playlist)
                setLayout()
            }
        }
    }

    private fun setLayout() {
        //for favourite
        fIndex = favouriteChecker(musicListPA[songPosition].id)


        Glide.with(this)
            .load(musicListPA[songPosition].imgUri)
            .apply(RequestOptions().placeholder(R.drawable.ic_music_default)).centerCrop()
            .into(binding.playerImage)
        binding.playerTitle.text = musicListPA[songPosition].title
        binding.playerSubtitle.text = musicListPA[songPosition].artist

        if (repeat) binding.ivRepeat.setColorFilter(
            ContextCompat.getColor(
                this,
                R.color.nav_selected_tint
            )
        )

        if (isFavourite) {
            binding.ivFvrt.setImageResource(R.drawable.red_heart)
        } else {
            binding.ivFvrt.setImageResource(R.drawable.white_heart)
        }

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
            nowPlayingId = musicListPA[songPosition].id
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

    private fun startService() {
        // for starting service
        val intent = Intent(this, MusicService::class.java)
        ContextCompat.startForegroundService(this, intent)
        bindService(intent, this, BIND_AUTO_CREATE)
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
        if (repeat) {
            // Just restart the current song
            createMediaPlayer()
            setLayout()
        } else {
            // Normal behavior: Go to next song
            setSongPosition(increment = true)
            createMediaPlayer()
            setLayout()
        }
    }
}
