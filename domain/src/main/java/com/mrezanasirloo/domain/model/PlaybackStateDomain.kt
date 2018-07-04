package com.mrezanasirloo.domain.model

/**
 * @author : M.Reza.Nasirloo@gmail.com
 *         Created on: 2018-07-04
 */
data class PlaybackStateDomain(
        val state: Int = 0,// PlaybackStateCompat.STATE_NONE
        val position: Long = 0,
        val actions: Long,
        val playbackSpeed: Float = 1f,
        val lastPositionUpdateTime: Long = 0,
        val song: SongDomain = SongDomain.EMPTY_SONG
)