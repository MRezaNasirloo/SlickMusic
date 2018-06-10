package com.mrezanasirloo.slickmusic.presentation.di

import android.content.Context
import dagger.Module
import dagger.Provides

/**
 * @author : M.Reza.Nasirloo@gmail.com
 *         Created on: 2018-06-10
 */
@Module(subcomponents = [(ComponentMain::class)])
class ModuleApp(context: Context) {
    private val context: Context = context.applicationContext

    @Provides
    fun providesContext(): Context {
        return context
    }

}