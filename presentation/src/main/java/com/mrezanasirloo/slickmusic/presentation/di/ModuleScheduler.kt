package com.mrezanasirloo.slickmusic.presentation.di

import dagger.Module
import dagger.Provides
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Named

/**
 * @author : M.Reza.Nasirloo@gmail.com
 *         Created on: 2018-06-10
 */
@Module(subcomponents = [(ComponentMain::class)])
class ModuleScheduler {
    @Provides
    @Named("main")
    fun provideMainThreadScheduler(): Scheduler {
        return AndroidSchedulers.mainThread()
    }

    @Provides
    @Named("io")
    fun provideBackgroundThreadScheduler(): Scheduler {
        return Schedulers.io()
    }
}