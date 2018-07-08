package com.mrezanasirloo.favorite

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

/**
 * @author : M.Reza.Nasirloo@gmail.com
 *         Created on: 2018-07-08
 */
@Entity(tableName = "favorite", indices = [(Index(value = ["songId"], unique = true))])
data class ModelFavorite(
        @PrimaryKey(autoGenerate = true) var id: Int? = null,
        @ColumnInfo(name = "songId") var songId: Int = -1
)