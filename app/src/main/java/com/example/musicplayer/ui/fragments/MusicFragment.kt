package com.example.musicplayer.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicplayer.databinding.FragmentMusicBinding
import com.example.musicplayer.models.Music
import com.example.musicplayer.ui.adapters.MusicAdapter
import com.example.musicplayer.viewmodel.MainViewModel
import com.example.musicplayer.viewmodel.MusicViewModel

class MusicFragment : Fragment() {

    companion object{
        lateinit var MusicListMF : ArrayList<Music>
    }

    private lateinit var binding: FragmentMusicBinding

    private lateinit var mainViewModel: MainViewModel
    private lateinit var musicViewModel: MusicViewModel

    private lateinit var musicAdapter: MusicAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMusicBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        musicViewModel = ViewModelProvider(requireActivity())[MusicViewModel::class.java]

        setupRecyclerView()
        observeViewModels()
    }

    private fun setupRecyclerView() {

        binding?.rvMusics?.apply {
            layoutManager = LinearLayoutManager(requireContext())

            musicAdapter = MusicAdapter(requireContext(), emptyList())
            adapter = musicAdapter
        }
    }

    private fun observeViewModels() {

        // 1. Permission observer (MainViewModel)
        mainViewModel.audioPermissionGranted.observe(viewLifecycleOwner) { granted ->

            if (granted == true) {
                musicViewModel.loadMusic(requireContext())
            }
        }

        // 2. Music list observer (MusicViewModel)
        musicViewModel.musicList.observe(viewLifecycleOwner) { songs ->

            MusicListMF = ArrayList()
            MusicListMF.clear()
            MusicListMF.addAll(songs)

            musicAdapter.updateList(songs)
        }
    }
}