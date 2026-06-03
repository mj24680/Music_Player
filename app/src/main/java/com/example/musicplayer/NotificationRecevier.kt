package com.example.musicplayer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.models.setSongPosition
import com.example.musicplayer.ui.activities.PlayerActivity
import com.example.musicplayer.ui.activities.PlayerActivity.Companion.binding
import com.example.musicplayer.ui.activities.PlayerActivity.Companion.musicListPA
import com.example.musicplayer.ui.activities.PlayerActivity.Companion.musicService
import com.example.musicplayer.ui.activities.PlayerActivity.Companion.songPosition
import kotlin.system.exitProcess

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when(intent?.action){
            ApplicationClass.PREVIOUS -> preNextSong(false, context!!)
            ApplicationClass.PLAY -> if(PlayerActivity.isPlaying) pauseMusic() else playMusic()
            ApplicationClass.NEXT -> preNextSong(true, context!!)
            // ApplicationClass.EXIT -> exitProcess(1)
        }
    }

    private fun playMusic(){
        PlayerActivity.isPlaying = true
        PlayerActivity.musicService!!.mediaPlayer!!.start()
        PlayerActivity.musicService!!.showNotification("Pause")
        PlayerActivity.binding.ivPlayPause.setImageResource(R.drawable.ic_pause)
    }
    private fun pauseMusic(){
        PlayerActivity.isPlaying = false
        PlayerActivity.musicService!!.mediaPlayer!!.pause()
        PlayerActivity.musicService!!.showNotification("Play")
        PlayerActivity.binding.ivPlayPause.setImageResource(R.drawable.ic_play)
    }

    private fun preNextSong(increment: Boolean, context: Context){
        setSongPosition(increment = increment)
        musicService!!.createMediaPlayer()
        Glide.with(context)
            .load(musicListPA[songPosition].imgUri)
            .apply(RequestOptions().placeholder(R.drawable.ic_music_default)).centerCrop()
            .into(binding.playerImage)
        binding.playerTitle.text = musicListPA[songPosition].title
        binding.playerSubtitle.text = musicListPA[songPosition].artist
        playMusic()
    }
}