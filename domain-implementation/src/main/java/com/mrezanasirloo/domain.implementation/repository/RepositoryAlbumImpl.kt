package com.mrezanasirloo.domain.implementation.repository

import android.content.Context
import com.mrezanasirloo.data.loader.AlbumLoader
import com.mrezanasirloo.data.loader.SongLoader
import com.mrezanasirloo.domain.model.AlbumDomain
import com.mrezanasirloo.domain.model.SongDomain
import com.mrezanasirloo.domain.repository.RepositoryAlbum
import com.mrezanasirloo.domain.repository.RepositorySong
import javax.inject.Inject

/**
 * @author : M.Reza.Nasirloo@gmail.com
 *         Created on: 2018-06-10
 */
class RepositoryAlbumImpl @Inject constructor(private val context: Context) : RepositoryAlbum {
    override fun allAlbums(): List<AlbumDomain> {
        return AlbumLoader.getAllAlbums(context)
    }
}