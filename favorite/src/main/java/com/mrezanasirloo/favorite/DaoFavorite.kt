package com.mrezanasirloo.favorite

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import io.reactivex.Single


/**
 * @author : M.Reza.Nasirloo@gmail.com
 *         Created on: 2018-07-08
 */
@Dao
interface DaoFavorite {
    @Query("SELECT * FROM favorite")
    fun all(): Single<List<ModelFavorite>>

    @Insert
    fun insertAll(vararg favorite: ModelFavorite)

    @Delete
    fun delete(favorite: ModelFavorite)
}