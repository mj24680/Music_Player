package com.example.musicplayer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager

class HeadphoneReceiver(private val onHeadphoneDisconnected: () -> Unit) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if(intent?.action == AudioManager.ACTION_AUDIO_BECOMING_NOISY){
            onHeadphoneDisconnected()
        }
    }
}