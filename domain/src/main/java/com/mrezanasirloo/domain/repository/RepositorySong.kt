package com.mrezanasirloo.domain.repository

import com.mrezanasirloo.domain.model.SongDomain

/**
 * @author : M.Reza.Nasirloo@gmail.com
 *         Created on: 2018-06-09
 */
interface RepositorySong {
    fun allSongs(): List<SongDomain>
}