package com.example.musicplayer.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.musicplayer.navigation.NavItems

class MainViewModel : ViewModel() {

    private val _selectedTab = MutableLiveData<NavItems>()
    val selectedTab: LiveData<NavItems> = _selectedTab

    private val _isAudioPermissionGranted = MutableLiveData<Boolean>()
    val audioPermissionGranted: LiveData<Boolean> = _isAudioPermissionGranted

    private val _isNotificationPermissionGranted = MutableLiveData<Boolean>()
    val isNotificationPermissionGranted: LiveData<Boolean> = _isNotificationPermissionGranted

    init {
        _selectedTab.value = NavItems.MUSIC
    }

    fun selectTab(tab: NavItems) {
        _selectedTab.value = tab
    }

    fun updateAudioPermissionStatus(isGranted: Boolean) {
        _isAudioPermissionGranted.value = isGranted
    }

    fun updateNotificationPermissionStatus(isGranted: Boolean) {
        _isNotificationPermissionGranted.value = isGranted
    }
}