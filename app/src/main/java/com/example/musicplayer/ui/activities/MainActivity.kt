package com.example.musicplayer.ui.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.R
import com.example.musicplayer.databinding.ActivityMainBinding
import com.example.musicplayer.models.Music
import com.example.musicplayer.models.MusicPlaylist
import com.example.musicplayer.models.setSongPosition
import com.example.musicplayer.navigation.NavItems
import com.example.musicplayer.ui.activities.PlayerActivity.Companion.musicListPA
import com.example.musicplayer.ui.activities.PlayerActivity.Companion.songPosition
import com.example.musicplayer.ui.fragments.FavouriteFragment
import com.example.musicplayer.ui.fragments.MusicFragment
import com.example.musicplayer.ui.fragments.PlaylistFragment
import com.example.musicplayer.utils.NavigationUIController
import com.example.musicplayer.viewmodel.MainViewModel
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    companion object {
        lateinit var binding: ActivityMainBinding
    }

    private val viewModel: MainViewModel by viewModels()

    private lateinit var navigationController: NavigationUIController

    private lateinit var favouriteFragment: FavouriteFragment
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

        window.statusBarColor = ContextCompat.getColor(this, R.color.statusBar)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.statusBar)

        navigationController = NavigationUIController(binding, this)

        setupFragments()
        setupNavigation()
        observeViewModel()
        requestAudioPermission()
        requestNotificationPermission()

        // retrieve favourites data using shared preferences
        FavouriteFragment.favouriteSongs = ArrayList()
        val editor = getSharedPreferences("FAVOURITES", MODE_PRIVATE)
        val jsonString = editor.getString("FavouriteSongs", null)
        val typeToken = object : TypeToken<ArrayList<Music>>(){}.type
        if(jsonString != null){
            val data : ArrayList<Music> = GsonBuilder().create().fromJson(jsonString, typeToken)
            FavouriteFragment.favouriteSongs.addAll(data)
        }

        // retrieve playlists data
        PlaylistFragment.musicPlaylist = MusicPlaylist()
        val jsonStringPlaylist = editor.getString("MusicPlaylist", null)
        if(jsonStringPlaylist != null){
            val dataPlaylist : MusicPlaylist = GsonBuilder().create().fromJson(jsonStringPlaylist, MusicPlaylist::class.java)
            PlaylistFragment.musicPlaylist = dataPlaylist
        }

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

        val searchText = binding.searchView.findViewById<android.widget.AutoCompleteTextView>(
            androidx.appcompat.R.id.search_src_text
        )

        searchText.setTextColor(
            ContextCompat.getColor(this, R.color.grey)
        )

        searchText.setHintTextColor(
            ContextCompat.getColor(this, R.color.grey)
        )

        binding.icShuffle.setOnClickListener {
            val intent = Intent(this@MainActivity, PlayerActivity::class.java)
            intent.putExtra("index", 0)
            intent.putExtra("class", "MainActivity")
            startActivity(intent)
        }

        binding.playbar.visibility = View.INVISIBLE
        binding.tvCurrentSongMsg.visibility = View.VISIBLE
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

    fun setBlackNavBarWithWhiteIcons() {
        // 1. Set nav bar color
        window.navigationBarColor = Color.BLACK

        // 2. Make system icons light (white)
        WindowCompat.getInsetsController(window, window.decorView)
            ?.isAppearanceLightNavigationBars = false

        // 3. Fallback for older APIs
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility =
            window.decorView.systemUiVisibility and
                    View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
    }

    override fun onResume() {
        super.onResume()

        // store favourites data using shared preferences
        val editor = getSharedPreferences("FAVOURITES", MODE_PRIVATE).edit()
        // for favourites
        val jsonString = GsonBuilder().create().toJson(FavouriteFragment.favouriteSongs)
        editor.putString("FavouriteSongs", jsonString)

        // for playlists
        val jsonStringPlalist = GsonBuilder().create().toJson(PlaylistFragment.musicPlaylist)
        editor.putString("MusicPlaylist", jsonStringPlalist)

        editor.apply()

        binding.tvPlaybarTitle.isSelected = true

        val audioGranted = isAudioPermissionGranted()
        val notificationGranted = isNotificationPermissionGranted()

        viewModel.updateAudioPermissionStatus(audioGranted)
        viewModel.updateNotificationPermissionStatus(notificationGranted)

        if(PlayerActivity.musicService != null){
            binding.playbar.visibility = View.VISIBLE
            binding.tvCurrentSongMsg.visibility = View.INVISIBLE
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
        favouriteFragment = FavouriteFragment()
        musicFragment = MusicFragment()
        playlistFragment = PlaylistFragment()

        supportFragmentManager.beginTransaction()
            .add(R.id.fl_navigation, favouriteFragment, NavItems.FAVOURITE.name)
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
                NavItems.FAVOURITE -> showFragment(favouriteFragment)
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