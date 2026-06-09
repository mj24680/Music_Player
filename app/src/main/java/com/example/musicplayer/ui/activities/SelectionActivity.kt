package com.example.musicplayer.ui.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicplayer.R
import com.example.musicplayer.databinding.ActivitySelectionBinding
import com.example.musicplayer.ui.activities.PlaylistDetails.Companion.currentPlaylistPos
import com.example.musicplayer.ui.adapters.MusicAdapter
import com.example.musicplayer.ui.fragments.MusicFragment
import com.example.musicplayer.ui.fragments.PlaylistFragment

class SelectionActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySelectionBinding
    private lateinit var selectionAdapter: MusicAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySelectionBinding.inflate(layoutInflater)

        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.statusBar)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.statusBar)

        binding.rvSelection.setItemViewCacheSize(10)
        binding.rvSelection.setHasFixedSize(true)
        binding.rvSelection.layoutManager = LinearLayoutManager(this)
        selectionAdapter = MusicAdapter(this, MusicFragment.MusicListMF, selectionActivity = true)
        binding.rvSelection.adapter = selectionAdapter

        binding.tvDone.setOnClickListener {
            finish()
        }
    }
}