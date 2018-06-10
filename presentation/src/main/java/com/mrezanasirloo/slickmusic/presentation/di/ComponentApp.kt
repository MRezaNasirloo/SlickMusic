package com.mrezanasirloo.slickmusic.presentation.di

import dagger.Component
import javax.inject.Singleton

/**
 * @author : M.Reza.Nasirloo@gmail.com
 *         Created on: 2018-06-10
 */
@Singleton
@Component(modules = [ModuleApp::class, ModuleScheduler::class])
interface ComponentApp {
    fun plus(): ComponentMain.Builder
}