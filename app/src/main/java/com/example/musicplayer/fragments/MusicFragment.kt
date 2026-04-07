package com.example.musicplayer.fragments

import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicplayer.R
import com.example.musicplayer.adapters.MusicAdapter
import com.example.musicplayer.databinding.FragmentMusicBinding
import com.example.musicplayer.models.Music
import java.io.File

class MusicFragment : Fragment() {

    private lateinit var binding: FragmentMusicBinding
    private lateinit var musicAdapter : MusicAdapter

    companion object{
        lateinit var MusicListMF : ArrayList<Music>
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


        MusicListMF = getAllAudio()

        binding.rvMusics.setHasFixedSize(true) // no extra objects, efficiently save memory
        binding.rvMusics.setItemViewCacheSize(10)
        binding.rvMusics.layoutManager = LinearLayoutManager(requireContext())
        musicAdapter = MusicAdapter(requireContext(), MusicListMF)
        binding.rvMusics.adapter = musicAdapter
    }

    private fun getAllAudio() : ArrayList<Music>{
        val tempList = ArrayList<Music>()

        // cursor helps us to fetch music from storage, we need to tell cursor which type and in which order we want data

        val selection = MediaStore.Audio.Media.IS_MUSIC + "!=0" // it tells cursor, which type of file we want
        val projection = arrayOf(MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.DATE_ADDED, MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID) // what data i want from this file

        val cursor = requireContext().contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection, null,
            MediaStore.Audio.Media.DATE_ADDED + " DESC")

        if(cursor != null){
            if(cursor.moveToFirst()){
                do{

                    var titleC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                    var idC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
                    var albumC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                    var artistC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    var pathC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    var durationC = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                    var albumIdC = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)).toString()
                    var uri = Uri.parse("content://media/external/audio/albumart")
                    var musicUri = Uri.withAppendedPath(uri, albumIdC).toString()


                    val music = Music(id = idC, title = titleC, album = albumC, artist = artistC, path = pathC, duration = durationC, imgUri = musicUri)

                    val file = File(music.path)
                    if(file.exists()){
                        tempList.add(music)
                    }

                }while (cursor.moveToNext())
                cursor.close()
            }
        }


        return tempList
    }
}
