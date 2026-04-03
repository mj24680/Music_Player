package com.example.musicplayer.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.example.musicplayer.R
import com.example.musicplayer.adapters.PlaylistAdapter
import com.example.musicplayer.adapters.TrendingAdapter
import com.example.musicplayer.databinding.FragmentHomeBinding
import com.example.musicplayer.models.TrendingModel

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupPlaylist()
        setupTrending()
    }

    private fun setupPlaylist() {
        val images = listOf(
            R.drawable.thumbnail1,
            R.drawable.thumbnail2,
            R.drawable.thumbnail3,
            R.drawable.thumbnail4,
            R.drawable.thumbnail5
        )

        val playlistAdapter = PlaylistAdapter(images)
        binding.rvPlaylist.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvPlaylist.adapter = playlistAdapter

        // Only attach SnapHelper if not already attached (to avoid crash on configuration change)
        if (binding.rvPlaylist.onFlingListener == null) {
            PagerSnapHelper().attachToRecyclerView(binding.rvPlaylist)
        }
    }

    private fun setupTrending() {
        val trendingItemList = listOf(
            TrendingModel("Ghost", "Justin Bieber", R.drawable.ic_music1),
            TrendingModel("Blinding Lights", "The Weeknd", R.drawable.ic_music2),
            TrendingModel("Shape of You", "Ed Sheeran", R.drawable.ic_music3),
            TrendingModel("Levitating", "Dua Lipa", R.drawable.ic_music4),
            TrendingModel("Stay", "The Kid LAROI", R.drawable.ic_music5),
            TrendingModel("Someone You Loved", "Lewis Capaldi", R.drawable.ic_music6),
            TrendingModel("Believer", "Imagine Dragons", R.drawable.ic_music7),
            TrendingModel("Perfect", "Ed Sheeran", R.drawable.ic_music8)
        )

        binding.rvTrending.layoutManager =
            GridLayoutManager(requireContext(), 2, GridLayoutManager.HORIZONTAL, false)

        binding.rvTrending.adapter = TrendingAdapter(trendingItemList) { clickedItem ->
            // In a real app, you'd use a ViewModel or Interface to update the MainActivity's playbar
            // For now, we'll keep it simple.
        }
    }
}
