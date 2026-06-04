package com.example.musicplayer.ui.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.R
import com.example.musicplayer.databinding.ActivityMainBinding
import com.example.musicplayer.models.setSongPosition
import com.example.musicplayer.navigation.NavItems
import com.example.musicplayer.ui.activities.PlayerActivity.Companion.musicListPA
import com.example.musicplayer.ui.activities.PlayerActivity.Companion.songPosition
import com.example.musicplayer.ui.fragments.HomeFragment
import com.example.musicplayer.ui.fragments.MusicFragment
import com.example.musicplayer.ui.fragments.PlaylistFragment
import com.example.musicplayer.utils.NavigationUIController
import com.example.musicplayer.viewmodel.MainViewModel
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    companion object {
        lateinit var binding: ActivityMainBinding
    }

    private val viewModel: MainViewModel by viewModels()

    private lateinit var navigationController: NavigationUIController

    private lateinit var homeFragment: HomeFragment
    lateinit var musicFragment: MusicFragment
    private lateinit var playlistFragment: PlaylistFragment

    // request permission from the user -> waits for the user answer -> executes code after the answer arrives
    private val audioPermissionLauncher =
    // registerForActivityResult -> for permissions, picking images, opening files, camera results
    // ActivityResultContracts.RequestPermission() -> I want to request One Permission
        // android provides different contracts -> GetContent() for file pick, TakePicture() for open camera
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            viewModel.updateAudioPermissionStatus(granted)
        }

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            viewModel.updateNotificationPermissionStatus(granted)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navigationController = NavigationUIController(binding, this)

        setupFragments()
        setupNavigation()
        observeViewModel()
        requestAudioPermission()
        requestNotificationPermission()

        window.statusBarColor = ContextCompat.getColor(this, R.color.statusBar)

        if (savedInstanceState == null) {
            viewModel.selectTab(NavItems.MUSIC)
        }


        binding.icSearch.setOnClickListener {
            binding.searchView.requestFocus()
        }

        binding.searchView.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {

                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    musicFragment.filterSongs(newText ?: "")
                    return true
                }
            }
        )

        binding.icShuffle.setOnClickListener {
            val intent = Intent(this@MainActivity, PlayerActivity::class.java)
            intent.putExtra("index", 0)
            intent.putExtra("class", "MainActivity")
            startActivity(intent)
        }

        binding.playbar.visibility = View.INVISIBLE
        binding.ivPlaypause.setOnClickListener {
            if(PlayerActivity.isPlaying) pauseMusic() else playMusic()
        }
        binding.ivPlaybarNext.setOnClickListener {
            setSongPosition(increment = true)
            PlayerActivity.musicService!!.createMediaPlayer()

            // if play next song from notification, change layout of play bar using this
            Glide.with(this)
                .load(musicListPA[songPosition].imgUri)
                .apply(RequestOptions().placeholder(R.drawable.ic_music_default)).centerCrop()
                .into(binding.ivPlaybarThumb)
            binding.tvPlaybarTitle.text = PlayerActivity.musicListPA[PlayerActivity.songPosition].title
            PlayerActivity.musicService!!.showNotification("Pause")
            playMusic()
        }
        binding.playbar.setOnClickListener {
            val intent = Intent(this, PlayerActivity::class.java)
            intent.putExtra("index", PlayerActivity.songPosition)
            intent.putExtra("class", "NowPlaying")
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()

        binding.tvPlaybarTitle.isSelected = true

        val audioGranted = isAudioPermissionGranted()
        val notificationGranted = isNotificationPermissionGranted()

        viewModel.updateAudioPermissionStatus(audioGranted)
        viewModel.updateNotificationPermissionStatus(notificationGranted)

        if(PlayerActivity.musicService != null){
            binding.playbar.visibility = View.VISIBLE
            Glide.with(this)
                .load(musicListPA[songPosition].imgUri)
                .apply(RequestOptions().placeholder(R.drawable.ic_music_default)).centerCrop()
                .into(binding.ivPlaybarThumb)
            binding.tvPlaybarTitle.text = PlayerActivity.musicListPA[PlayerActivity.songPosition].title
            if(PlayerActivity.isPlaying){
                binding.ivPlaypause.setImageResource(R.drawable.ic_pause)
            }else{
                binding.ivPlaypause.setImageResource(R.drawable.ic_play)
            }
        }

    }

    private fun setupFragments() {
        homeFragment = HomeFragment()
        musicFragment = MusicFragment()
        playlistFragment = PlaylistFragment()

        supportFragmentManager.beginTransaction()
            .add(R.id.fl_navigation, homeFragment, NavItems.FAVOURITE.name)
            .add(R.id.fl_navigation, musicFragment, NavItems.MUSIC.name).hide(musicFragment)
            .add(R.id.fl_navigation, playlistFragment, NavItems.PLAYLIST.name)
            .hide(playlistFragment)
            .commit()
    }

    private fun showFragment(fragment: Fragment) {

        supportFragmentManager.beginTransaction().apply {

            supportFragmentManager.fragments.forEach {
                hide(it)
            }
            show(fragment)
            commit()
        }
    }


    private fun setupNavigation() {

        binding.cvFvrt.setOnClickListener {
            viewModel.selectTab(NavItems.FAVOURITE)
        }

        binding.cvMusic.setOnClickListener {
            viewModel.selectTab(NavItems.MUSIC)
        }

        binding.cvPlaylists.setOnClickListener {
            viewModel.selectTab(NavItems.PLAYLIST)
        }
    }

    private fun observeViewModel() {

        viewModel.selectedTab.observe(this) { tab ->

            navigationController.update(tab)

            when (tab) {
                NavItems.FAVOURITE -> showFragment(homeFragment)
                NavItems.MUSIC -> showFragment(musicFragment)
                NavItems.PLAYLIST -> showFragment(playlistFragment)
            }
        }
    }

    private fun isAudioPermissionGranted(): Boolean {
        val permission =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Manifest.permission.READ_MEDIA_AUDIO
            } else {
                Manifest.permission.READ_EXTERNAL_STORAGE
            }

        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun isNotificationPermissionGranted(): Boolean {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return true
        }
        val permission = Manifest.permission.POST_NOTIFICATIONS
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestAudioPermission() {

        if (isAudioPermissionGranted()) {
            viewModel.updateAudioPermissionStatus(true)
            return
        }

        val permission =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Manifest.permission.READ_MEDIA_AUDIO
            } else {
                Manifest.permission.READ_EXTERNAL_STORAGE
            }

        audioPermissionLauncher.launch(permission)
    }

    private fun requestNotificationPermission() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

        if (isNotificationPermissionGranted()) {
            viewModel.updateNotificationPermissionStatus(true)
            return
        }

        notificationPermissionLauncher.launch(
            Manifest.permission.POST_NOTIFICATIONS
        )
    }

    private fun playMusic(){
        PlayerActivity.musicService!!.mediaPlayer!!.start()
        binding.ivPlaypause.setImageResource(R.drawable.ic_pause)
        PlayerActivity.musicService!!.showNotification("Pause")
        PlayerActivity.binding.ivNext.setImageResource(R.drawable.ic_pause)
        PlayerActivity.isPlaying = true
    }

    private fun pauseMusic(){
        PlayerActivity.musicService!!.mediaPlayer!!.pause()
        binding.ivPlaypause.setImageResource(R.drawable.ic_play)
        PlayerActivity.musicService!!.showNotification("Play")
        PlayerActivity.binding.ivNext.setImageResource(R.drawable.ic_play)
        PlayerActivity.isPlaying = false
    }

    override fun onBackPressed() {
        if (viewModel.selectedTab.value != NavItems.FAVOURITE) {
            viewModel.selectTab(NavItems.FAVOURITE)
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(!PlayerActivity.isPlaying && PlayerActivity.musicService != null){
            PlayerActivity.musicService!!.stopForeground(true)
            PlayerActivity.musicService!!.mediaPlayer!!.release()
            PlayerActivity.musicService = null
            exitProcess(1)
        }
    }
}