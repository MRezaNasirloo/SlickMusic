package com.mrezanasirloo.slickmusic.presentation.di

import com.mrezanasirloo.slickmusic.presentation.ui.play.FragmentPlay
import dagger.Subcomponent
import javax.inject.Singleton

@Subcomponent(modules = [ModuleMain::class])
interface ComponentMain {
    fun inject(fragmentPlay: FragmentPlay)

    @Subcomponent.Builder
    interface Builder {
        fun mainModule(moduleMain: ModuleMain): Builder

        fun build(): ComponentMain
    }
}
