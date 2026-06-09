package com.example.musicplayer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.models.favouriteChecker
import com.example.musicplayer.models.setSongPosition
import com.example.musicplayer.ui.activities.MainActivity
import com.example.musicplayer.ui.activities.PlayerActivity
import com.example.musicplayer.ui.activities.PlayerActivity.Companion.binding
import com.example.musicplayer.ui.activities.PlayerActivity.Companion.musicListPA
import com.example.musicplayer.ui.activities.PlayerActivity.Companion.musicService
import com.example.musicplayer.ui.activities.PlayerActivity.Companion.songPosition
import kotlin.system.exitProcess

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            ApplicationClass.PREVIOUS -> preNextSong(false, context!!)
            ApplicationClass.PLAY -> if (PlayerActivity.isPlaying) pauseMusic() else playMusic()
            ApplicationClass.NEXT -> preNextSong(true, context!!)
            ApplicationClass.EXIT -> {
                musicService!!.mediaPlayer!!.pause()
                PlayerActivity.isPlaying = false
                // update UI if Player Activity Visible
                PlayerActivity.binding.ivPlayPause.setImageResource(R.drawable.ic_play)
                MainActivity.binding.ivPlaypause.setImageResource(R.drawable.ic_play)
                musicService?.stopForeground(true)
                // musicService?.mediaPlayer?.release()
                // exitProcess(1)
            }
        }
    }

    private fun playMusic() {
        PlayerActivity.isPlaying = true
        PlayerActivity.musicService!!.mediaPlayer!!.start()
        PlayerActivity.musicService!!.showNotification("Pause")
        PlayerActivity.binding.ivPlayPause.setImageResource(R.drawable.ic_pause)
        MainActivity.binding.ivPlaypause.setImageResource(R.drawable.ic_pause)
    }

    private fun pauseMusic() {
        PlayerActivity.isPlaying = false
        PlayerActivity.musicService!!.mediaPlayer!!.pause()
        PlayerActivity.musicService!!.showNotification("Play")
        PlayerActivity.binding.ivPlayPause.setImageResource(R.drawable.ic_play)
        MainActivity.binding.ivPlaypause.setImageResource(R.drawable.ic_play)
    }

    private fun preNextSong(increment: Boolean, context: Context) {
        setSongPosition(increment = increment)
        PlayerActivity.musicService!!.createMediaPlayer()
        Glide.with(context)
            .load(musicListPA[songPosition].imgUri)
            .apply(RequestOptions().placeholder(R.drawable.ic_music_default)).centerCrop()
            .into(binding.playerImage)
        PlayerActivity.binding.playerTitle.text = musicListPA[songPosition].title
        PlayerActivity.binding.playerSubtitle.text = musicListPA[songPosition].artist

        // if play next song from notification, change layout of play bar using this
        Glide.with(context)
            .load(musicListPA[songPosition].imgUri)
            .apply(RequestOptions().placeholder(R.drawable.ic_music_default)).centerCrop()
            .into(MainActivity.Companion.binding.ivPlaybarThumb)
        MainActivity.Companion.binding.tvPlaybarTitle.text = PlayerActivity.musicListPA[PlayerActivity.songPosition].title

        playMusic()

        // if click next in notification layout change of player activity and favourite icon not updated, this code for this issue
        PlayerActivity.fIndex = favouriteChecker(PlayerActivity.musicListPA[PlayerActivity.songPosition].id)
        if(PlayerActivity.isFavourite){
            PlayerActivity.binding.ivFvrt.setImageResource(R.drawable.red_heart)
        }else{
            PlayerActivity.binding.ivFvrt.setImageResource(R.drawable.white_heart)
        }
    }
}