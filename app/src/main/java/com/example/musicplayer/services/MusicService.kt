package com.example.musicplayer.services

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.support.v4.media.session.MediaSessionCompat
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.musicplayer.ApplicationClass
import com.example.musicplayer.HeadphoneReceiver
import com.example.musicplayer.NotificationReceiver
import com.example.musicplayer.R
import com.example.musicplayer.models.formatDuration
import com.example.musicplayer.ui.activities.MainActivity
import com.example.musicplayer.ui.activities.PlayerActivity
import com.example.musicplayer.ui.activities.PlayerActivity.Companion.binding
import com.example.musicplayer.ui.activities.PlayerActivity.Companion.musicListPA
import com.example.musicplayer.ui.activities.PlayerActivity.Companion.musicService
import com.example.musicplayer.ui.activities.PlayerActivity.Companion.nowPlayingId
import com.example.musicplayer.ui.activities.PlayerActivity.Companion.songPosition

class MusicService : Service() {

    private var myBinder = MyBinder()
    var mediaPlayer: MediaPlayer? = null
    private lateinit var headphoneReceiver: HeadphoneReceiver
    private lateinit var mediaSession: MediaSessionCompat

    // helps use to run same code repeatedly
    private lateinit var runnable: Runnable

    // this method call, when we bind service with activity
    override fun onBind(intent: Intent?): IBinder? {
        return myBinder
    }

    override fun onCreate() {
        super.onCreate()
        mediaSession = MediaSessionCompat(baseContext, "MyMusicSession")
        mediaSession.isActive = true

        // pause music, when unplug headphones
        headphoneReceiver = HeadphoneReceiver {
            // Only pause if music is actually playing
            if (PlayerActivity.isPlaying) {
                mediaPlayer?.pause()
                PlayerActivity.isPlaying = false // 1. Update the global state

                showNotification("Play") // 2. Update notification text

                // 3. Update PlayerActivity UI (using try-catch to avoid crash if Activity is closed)
                try {
                    binding.ivPlayPause.setImageResource(R.drawable.ic_play)
                } catch (e: Exception) {
                    // Activity is not active, ignore
                }

                // 4. Update MainActivity UI Playbar
                try {
                    MainActivity.binding.ivPlaypause.setImageResource(R.drawable.ic_play)
                } catch (e: Exception) {
                    // Activity is not active, ignore
                }
            }
        }
        // Registering the receiver to listen for headphone disconnection
        registerReceiver(headphoneReceiver, IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY))
    }

    override fun onDestroy() {
        super.onDestroy()

        try {
            unregisterReceiver(headphoneReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        mediaPlayer?.release()
        mediaPlayer = null
    }

    // this class helps us to return our main class object
    inner class MyBinder : Binder() {
        fun currentService(): MusicService {
            return this@MusicService
        }
    }

    fun showNotification(playPauseString: String) {
        Log.d("MusicService", "showNotification called")

        val intent = Intent(baseContext, MainActivity::class.java)
        val contentIntent = PendingIntent.getActivity(baseContext, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val previousIntent = Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.PREVIOUS)
        val previousPendingIntent = PendingIntent.getBroadcast(baseContext, 0, previousIntent, PendingIntent.FLAG_IMMUTABLE)

        val playIntent = Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.PLAY)
        val playPendingIntent = PendingIntent.getBroadcast(baseContext, 1, playIntent, PendingIntent.FLAG_IMMUTABLE)

        val nextIntent = Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.NEXT)
        val nextPendingIntent = PendingIntent.getBroadcast(baseContext, 2, nextIntent, PendingIntent.FLAG_IMMUTABLE)

         val exitIntent = Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.EXIT)
         val exitPendingIntent = PendingIntent.getBroadcast(baseContext, 3, exitIntent, PendingIntent.FLAG_IMMUTABLE)


        val notification = NotificationCompat.Builder(baseContext, ApplicationClass.CHANNEL_ID)
            .setContentTitle(PlayerActivity.musicListPA[PlayerActivity.songPosition].title)
            .setContentText(PlayerActivity.musicListPA[PlayerActivity.songPosition].artist.replace("<unknown>", "unknown"))
            .setSmallIcon(R.drawable.ic_playlist)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_music_default))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .setContentIntent(contentIntent)
            .addAction(R.drawable.ic_previous, "Previous", previousPendingIntent)
            .addAction(R.drawable.ic_pause, playPauseString, playPendingIntent)
            .addAction(R.drawable.ic_next, "Next", nextPendingIntent)
            .setDeleteIntent(exitPendingIntent) // triggers on swipe
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // Android 14 (API LEVEL 34)
            startForeground(10, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)
        } else {
            startForeground(10, notification)
        }
    }

    fun createMediaPlayer() {
        try {
            // MediaPlayer Class in android -> to play music
            if (musicService!!.mediaPlayer == null) {
                musicService!!.mediaPlayer = MediaPlayer()
            }

            musicService!!.mediaPlayer!!.apply {
                reset() // currently song is playing, need to reset first
                setDataSource(musicListPA[songPosition].path)
                prepare()
            }
            binding.ivPlayPause.setImageResource(R.drawable.ic_pause)
            musicService!!.showNotification("Pause")

            // because when click on next button in notification new song plays and seekbar still on previous song duration position
            binding.tvSeekBarStart.text = formatDuration(mediaPlayer!!.currentPosition.toLong())
            binding.tvSeekBarEnd.text = formatDuration(mediaPlayer!!.duration.toLong())

            binding.seekBar.max = musicService!!.mediaPlayer!!.duration
            nowPlayingId = musicListPA[songPosition].id

        } catch (e: Exception) {
            return
        }
    }

    fun seekBarSetup(){
        runnable = Runnable{
            binding.tvSeekBarStart.text = formatDuration(mediaPlayer!!.currentPosition.toLong())
            binding.seekBar.progress = mediaPlayer!!.currentPosition
            Handler(Looper.getMainLooper()).postDelayed(runnable, 200) // yh code kitny time bad dubara re-run hona chahiye
        }
        Handler(Looper.getMainLooper()).postDelayed(runnable, 0)
    }
}
