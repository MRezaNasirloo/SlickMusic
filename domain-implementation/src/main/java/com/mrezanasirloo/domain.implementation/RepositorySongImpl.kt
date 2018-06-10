package com.mrezanasirloo.domain.implementation

import android.content.Context
import com.mrezanasirloo.data.loader.SongLoader
import com.mrezanasirloo.domain.model.SongDomain
import com.mrezanasirloo.domain.repository.RepositorySong
import javax.inject.Inject

/**
 * @author : M.Reza.Nasirloo@gmail.com
 *         Created on: 2018-06-10
 */
class RepositorySongImpl @Inject constructor(private val context: Context) : RepositorySong {
    override fun allSongs(): List<SongDomain> {
        return SongLoader.getAllSongs(context)
    }
}