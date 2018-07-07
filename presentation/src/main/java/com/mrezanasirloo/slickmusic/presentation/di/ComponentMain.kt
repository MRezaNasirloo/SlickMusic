package com.mrezanasirloo.slickmusic.presentation.di

import com.mrezanasirloo.slickmusic.presentation.ui.album.FragmentAlbum
import com.mrezanasirloo.slickmusic.presentation.ui.main.ActivityMain
import com.mrezanasirloo.slickmusic.presentation.ui.play.FragmentPlay
import com.mrezanasirloo.slickmusic.presentation.ui.song.FragmentSong
import com.mrezanasirloo.slickmusic.presentation.ui.tageditor.ActivityTagEditor
import dagger.Subcomponent

@Subcomponent(modules = [ModuleMain::class])
interface ComponentMain {
    fun inject(fragmentPlay: FragmentSong)
    fun inject(fragmentAlbum: FragmentAlbum)
    fun inject(activityMain: ActivityMain)
    fun inject(fragmentPlay: FragmentPlay)
    fun inject(activityTagEditor: ActivityTagEditor)

    @Subcomponent.Builder
    interface Builder {
        fun mainModule(moduleMain: ModuleMain): Builder

        fun build(): ComponentMain
    }


}
