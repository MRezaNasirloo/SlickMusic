package com.mrezanasirloo.domain.model

/**
 * @author : M.Reza.Nasirloo@gmail.com
 * Created on: 2018-07-07
 */
data class SongTagDomain(
        val songDomain: SongDomain,
        val genre: String,
        val byte: ByteArray?
)
