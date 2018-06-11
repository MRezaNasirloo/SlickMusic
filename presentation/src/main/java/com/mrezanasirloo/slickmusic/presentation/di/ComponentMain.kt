package com.mrezanasirloo.slickmusic.presentation.di

import com.mrezanasirloo.slickmusic.presentation.ui.main.ActivityMain
import com.mrezanasirloo.slickmusic.presentation.ui.play.FragmentPlay
import dagger.Subcomponent

@Subcomponent(modules = [ModuleMain::class])
interface ComponentMain {
    fun inject(fragmentPlay: FragmentPlay)
    fun inject(fragmentPlay: ActivityMain)

    @Subcomponent.Builder
    interface Builder {
        fun mainModule(moduleMain: ModuleMain): Builder

        fun build(): ComponentMain
    }

}
