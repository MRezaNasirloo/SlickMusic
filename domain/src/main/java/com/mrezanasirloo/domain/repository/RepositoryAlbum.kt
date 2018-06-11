package com.mrezanasirloo.domain.repository

import com.mrezanasirloo.domain.model.AlbumDomain

/**
 * @author : M.Reza.Nasirloo@gmail.com
 *         Created on: 2018-06-09
 */
interface RepositoryAlbum {
    fun allAlbums(): List<AlbumDomain>
}