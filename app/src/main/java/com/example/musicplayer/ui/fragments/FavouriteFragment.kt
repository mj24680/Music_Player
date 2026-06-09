package com.example.musicplayer.ui.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.musicplayer.databinding.FragmentFavouriteBinding
import com.example.musicplayer.models.Music
import com.example.musicplayer.models.checkPlaylist
import com.example.musicplayer.ui.adapters.FavouriteAdapter

class FavouriteFragment : Fragment() {

    companion object{
        var favouriteSongs: ArrayList<Music> = ArrayList()
        var favouriteAdapter : FavouriteAdapter? = null
    }

    private lateinit var binding: FragmentFavouriteBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavouriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        favouriteSongs = checkPlaylist(favouriteSongs)

        binding.rvFavourite.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            setHasFixedSize(true)
            setItemViewCacheSize(12)
            favouriteAdapter = FavouriteAdapter(requireContext(), favouriteSongs)
            adapter = favouriteAdapter
        }

    }
}
