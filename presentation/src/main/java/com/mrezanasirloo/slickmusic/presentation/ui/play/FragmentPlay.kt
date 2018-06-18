package com.mrezanasirloo.slickmusic.presentation.ui.play


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.mrezanasirloo.slickmusic.R
import com.mrezanasirloo.slickmusic.presentation.ui.main.BackStackFragment
import com.mrezanasirloo.slickmusic.presentation.ui.song.model.Song


/**
 * A simple [Fragment] subclass.
 * Use the [FragmentPlay.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentPlay : BackStackFragment(), ViewPlay {

    // TODO: Rename and change types of parameters
    private var songs: Array<Song> = emptyArray()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            songs = it.getParcelableArray(ARG_SONGS) as Array<Song>
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_play, container, false)
    }

    companion object {
        private val ARG_SONGS = "arg_songs"

        fun newInstance(vararg song: Song): FragmentPlay {
            val fragment = FragmentPlay()
            val args = Bundle()
            args.putParcelableArray(ARG_SONGS, song)
            fragment.arguments = args
            return fragment
        }
        fun newInstance(album: Album): FragmentPlay {
            val fragment = FragmentPlay()
            val args = Bundle()
            args.putParcelableArray(ARG_SONGS, album.songs.toArray())
            fragment.arguments = args
            return fragment
        }
    }

}
