package com.mrezanasirloo.domain.model

/**
 * @author : M.Reza.Nasirloo@gmail.com
 * Created on: 2018-07-07
 */
data class SongTagDomain(
        val songDomain: SongDomain,
        val genre: String,
        val year: String = "",
        val track: String = "",
        val byte: ByteArray?
)
