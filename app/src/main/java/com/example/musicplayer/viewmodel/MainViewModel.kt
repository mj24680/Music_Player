package com.example.musicplayer.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.musicplayer.navigation.NavItems

class MainViewModel : ViewModel() {

    private val _selectedTab = MutableLiveData<NavItems>()
    val selectedTab: LiveData<NavItems> = _selectedTab

    private val _audioPermissionGranted = MutableLiveData<Boolean>()
    val audioPermissionGranted: LiveData<Boolean> = _audioPermissionGranted

    init {
        _selectedTab.value = NavItems.HOME
    }

    fun selectTab(tab: NavItems) {
        _selectedTab.value = tab
    }

    fun onAudioPermissionAlreadyGranted() {
        _audioPermissionGranted.value = true
    }

    fun onAudioPermissionResult(granted: Boolean) {
        _audioPermissionGranted.value = granted
    }
}