package com.example.musicplayer.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.musicplayer.R
import com.example.musicplayer.core.PermissionManager
import com.example.musicplayer.databinding.ActivityMainBinding
import com.example.musicplayer.navigation.NavItems
import com.example.musicplayer.ui.fragments.HomeFragment
import com.example.musicplayer.ui.fragments.MusicFragment
import com.example.musicplayer.ui.fragments.PlaylistFragment
import com.example.musicplayer.utils.NavigationUIController
import com.example.musicplayer.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    private lateinit var navigationController: NavigationUIController

    private lateinit var homeFragment: HomeFragment
    private lateinit var musicFragment: MusicFragment
    private lateinit var playlistFragment: PlaylistFragment

    // request permission from the user -> waits for the user answer -> executes code after the answer arrives
    private val permissionLauncher =
    // registerForActivityResult -> for permissions, picking images, opening files, camera results
    // ActivityResultContracts.RequestPermission() -> I want to request One Permission
        // android provides different contracts -> GetContent() for file pick, TakePicture() for open camera
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            viewModel.onAudioPermissionResult(granted)

            Toast.makeText(
                this,
                if (granted) "Permission Granted"
                else "Permission is required to load music",
                Toast.LENGTH_SHORT
            ).show()
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

        window.statusBarColor = ContextCompat.getColor(this, R.color.statusBar)

        if (savedInstanceState == null) {
            viewModel.selectTab(NavItems.HOME)
        }

        binding.icShuffle.setOnClickListener {
            val intent = Intent(this@MainActivity, PlayerActivity::class.java)
            intent.putExtra("index", 0)
            intent.putExtra("class", "MainActivity")
            startActivity(intent)
        }
    }

    private fun setupFragments() {
        homeFragment = HomeFragment()
        musicFragment = MusicFragment()
        playlistFragment = PlaylistFragment()

        supportFragmentManager.beginTransaction()
            .add(R.id.fl_navigation, homeFragment, NavItems.HOME.name)
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

        binding.cvHome.setOnClickListener {
            viewModel.selectTab(NavItems.HOME)
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
                NavItems.HOME -> showFragment(homeFragment)
                NavItems.MUSIC -> showFragment(musicFragment)
                NavItems.PLAYLIST -> showFragment(playlistFragment)
            }
        }
    }

    private fun requestAudioPermission() {

        val permission = PermissionManager.audioPermission()

        if (PermissionManager.hasAudioPermission(this)) {
            viewModel.onAudioPermissionAlreadyGranted()
            return
        }

        permissionLauncher.launch(permission)
    }

    override fun onBackPressed() {
        if (viewModel.selectedTab.value != NavItems.HOME) {
            viewModel.selectTab(NavItems.HOME)
        } else {
            super.onBackPressed()
        }
    }
}