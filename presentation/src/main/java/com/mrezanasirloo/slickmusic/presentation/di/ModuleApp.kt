package com.mrezanasirloo.slickmusic.presentation.di

import android.arch.persistence.room.Room
import android.content.Context
import com.mrezanasirloo.favorite.AppDatabase
import com.mrezanasirloo.favorite.DaoFavorite
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


/**
 * @author : M.Reza.Nasirloo@gmail.com
 *         Created on: 2018-06-10
 */
@Module(subcomponents = [(ComponentMain::class)])
class ModuleApp(context: Context) {
    private val context: Context = context.applicationContext

    @Provides
    @Singleton
    fun providesContext(): Context {
        return context
    }

    @Provides
    @Singleton
    fun providesDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "database.db").build()
    }

    @Provides
    @Singleton
    fun providesDao(appDatabase: AppDatabase): DaoFavorite {
        return appDatabase.daoFavorite()
    }


}