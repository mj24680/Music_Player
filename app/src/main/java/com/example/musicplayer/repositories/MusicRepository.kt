package com.example.musicplayer.repositories

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.example.musicplayer.models.Music
import java.io.File

class MusicRepository {
    fun getAllAudio(context: Context): List<Music> {

        val musicList = mutableListOf<Music>() // empty list to store music

        // we want music files not WhatsApp audio files
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0 " +
                    "AND ${MediaStore.Audio.Media.RELATIVE_PATH} NOT LIKE '%WhatsApp%'"

        // These are the columns/data we want from MediaStore
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID    // used for album art
        )

        try {

            // Query MediaStore to get audio files
            val cursor = context.contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                "${MediaStore.Audio.Media.DATE_ADDED} DESC"
            )

            // cursor?.use automatically closes the cursor after use
            cursor?.use {
                // cursor as a table returned from a database query
                // like this;
                // TITLE	ALBUM_ID
                // Song A	42
                // Song B	57
                // when android gives a cursor, it doesn't automatically know which column you want to read

                val idColumn =
                    it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID) // means -> find the position of the ALBUM_ID column in the cursor

                val titleColumn =
                    it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)

                val albumColumn =
                    it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)

                val artistColumn =
                    it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)

                val durationColumn =
                    it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)

                val pathColumn =
                    it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)

                val albumIdColumn =
                    it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)

                // Loop through all songs
                while (it.moveToNext()) {

                    // Read data from current row
                    val id = it.getString(idColumn)
                    val title = it.getString(titleColumn)
                    val album = it.getString(albumColumn)
                    val artist = it.getString(artistColumn)
                    val duration = it.getLong(durationColumn)
                    val path = it.getString(pathColumn)
                    val albumId = it.getLong(albumIdColumn)

                    // build path of album cover image using album id
                    val imageUri = Uri.withAppendedPath(
                        Uri.parse("content://media/external/audio/albumart"),
                        albumId.toString()
                    ).toString()

                    // Create file object using song path
                    val file = File(path)

                    // Check if file actually exists
                    if (file.exists()) { // This avoids broken or deleted songs
                        val music = Music(
                            id = id,
                            title = title,
                            album = album,
                            artist = artist,
                            duration = duration,
                            path = path,
                            imgUri = imageUri
                        )

                        musicList.add(music) // add to list
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return musicList
    }
}