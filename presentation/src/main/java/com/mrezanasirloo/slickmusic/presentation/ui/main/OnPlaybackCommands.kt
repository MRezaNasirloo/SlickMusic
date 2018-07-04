package com.mrezanasirloo.slickmusic.presentation.ui.main

import com.mrezanasirloo.domain.implementation.model.Song

interface OnPlaybackCommands {
    fun playSongs(vararg songs: Song)
//    fun playAlbum(album: Album)

}
