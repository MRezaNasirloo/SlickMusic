package com.mrezanasirloo.favorite

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase


/**
 * @author : M.Reza.Nasirloo@gmail.com
 *         Created on: 2018-07-08
 */
@Database(entities = [ModelFavorite::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun daoFavorite(): DaoFavorite
}