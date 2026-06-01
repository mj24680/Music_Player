package com.example.musicplayer.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.musicplayer.repositories.MusicRepository
import com.example.musicplayer.models.Music

class MusicViewModel : ViewModel() {

    private val repository = MusicRepository()

    private val _musicList = MutableLiveData<List<Music>>()
    val musicList: LiveData<List<Music>> = _musicList

    fun loadMusic(context: Context) {
        val songs = repository.getAllAudio(context)
        _musicList.value = songs
    }
}