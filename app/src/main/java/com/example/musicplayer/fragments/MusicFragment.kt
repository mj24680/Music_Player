package com.example.musicplayer.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicplayer.adapters.MusicAdapter
import com.example.musicplayer.databinding.FragmentMusicBinding
import com.example.musicplayer.models.Music
import java.io.File

class MusicFragment : Fragment() {

    private lateinit var binding: FragmentMusicBinding
    private lateinit var musicAdapter: MusicAdapter

    companion object {
        // This list stores all the songs we find. 
        // We use 'companion object' so the list stays saved even if we switch screens.
        var MusicListMF: ArrayList<Music> = ArrayList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMusicBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        if (MusicListMF.isEmpty()) {
            refreshMusicList()
        }
    }

    override fun onResume() {
        super.onResume()
        if (MusicListMF.isEmpty()) {
            refreshMusicList()
        }
    }

    private fun setupRecyclerView() {
        binding.rvMusics.setHasFixedSize(true)
        binding.rvMusics.setItemViewCacheSize(10)
        binding.rvMusics.layoutManager = LinearLayoutManager(requireContext())

        // Pass the context and our music list to the adapter
        musicAdapter = MusicAdapter(requireContext(), MusicListMF)
        binding.rvMusics.adapter = musicAdapter
    }

    fun refreshMusicList() {
        val context = context ?: return

        // Decide which permission to check based on Android version
        val storagePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        // permission granted -> fetch the songs
        if (ContextCompat.checkSelfPermission(context, storagePermission) == PackageManager.PERMISSION_GRANTED) {
            val fetchedSongs = getAllAudio()

            // Clear the old list and add the fresh songs we found
            MusicListMF.clear()
            MusicListMF.addAll(fetchedSongs)

            // Tell the adapter that the list has changed so it can update the screen
            if (::musicAdapter.isInitialized) {
                musicAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun getAllAudio(): ArrayList<Music> {
        val tempList = ArrayList<Music>()
        val context = context ?: return tempList

        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0 " +
                "AND ${MediaStore.Audio.Media.RELATIVE_PATH} NOT LIKE '%WhatsApp%'"

        // The specific details we want to get for each song
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID
        )

        try {
            // Use 'cursor' to read through the storage database like a table
            val cursor = context.contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                MediaStore.Audio.Media.DATE_ADDED + " DESC" // Show newest songs first
            )

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        // Get each detail from the current row
                        val title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                        val id = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
                        val album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                        val artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                        val path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                        val duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                        val albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)).toString()

                        val artUri = Uri.parse("content://media/external/audio/albumart")
                        val finalImgUri = Uri.withAppendedPath(artUri, albumId).toString()

                        val music = Music(
                            id = id,
                            title = title,
                            album = album,
                            artist = artist,
                            path = path,
                            duration = duration,
                            imgUri = finalImgUri
                        )

                        // Only add the song if the file actually exists in phone
                        val file = File(music.path)
                        if (file.exists()) {
                            tempList.add(music)
                        }

                    } while (cursor.moveToNext())
                    cursor.close()
                }
            }
        } catch (e: SecurityException) {
            // This happens if permissions are missing or revoked
        }
        return tempList
    }
}
