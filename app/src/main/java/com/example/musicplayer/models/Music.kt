package com.example.musicplayer.models

import com.example.musicplayer.ui.activities.PlayerActivity.Companion.musicListPA
import com.example.musicplayer.ui.activities.PlayerActivity.Companion.songPosition
import java.util.concurrent.TimeUnit

data class Music(
    val id: String,
    val title: String,
    val album: String,
    val artist: String,
    val duration: Long = 0,
    val path: String,
    val imgUri: String
)

fun formatDuration(duration: Long): String {
    val minutes = TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS)
    val seconds = (TimeUnit.SECONDS.convert(duration, TimeUnit.MILLISECONDS) -
            minutes * TimeUnit.SECONDS.convert(1, TimeUnit.MINUTES))

    return String.format("%02d:%02d", minutes, seconds)
}

fun setSongPosition(increment: Boolean) { // for first and end song
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