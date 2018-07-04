package com.mrezanasirloo.slickmusic.presentation.ui.play


import android.animation.ObjectAnimator
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.media.session.PlaybackStateCompat.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.Toast
import com.jakewharton.rxbinding2.view.RxView
import com.mrezanasirloo.domain.implementation.model.Song
import com.mrezanasirloo.domain.model.PlaybackStateDomain
import com.mrezanasirloo.slick.Presenter
import com.mrezanasirloo.slickmusic.R
import com.mrezanasirloo.slickmusic.presentation.App
import com.mrezanasirloo.slickmusic.presentation.ui.main.BackStackFragment
import com.mrezanasirloo.slickmusic.presentation.ui.song.model.Album
import io.reactivex.Observable
import kotlinx.android.synthetic.main.layout_play.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Provider


/**
 * A simple [Fragment] subclass.
 * Use the [FragmentPlay.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentPlay : BackStackFragment(), ViewPlay {
    private val TAG: String = FragmentPlay::class.java.simpleName

    @Inject
    lateinit var provider: Provider<PresenterPlay>
    @Inject
    @Presenter
    lateinit var presenter: PresenterPlay

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.componentMain().inject(this)
        PresenterPlay_Slick.bind(this)
        arguments?.let {
            //            songs = it.getParcelableArray(ARG_SONGS) as Array<Song>
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_play, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }

    override fun play(): Observable<Any> {
        return RxView.clicks(button_play_pause).throttleFirst(1, TimeUnit.SECONDS)
                .filter { button_play_pause.getTag(0) == STATE_PAUSED }
    }

    override fun pause(): Observable<Any> {
        return RxView.clicks(button_play_pause).throttleFirst(1, TimeUnit.SECONDS)
                .filter { button_play_pause.getTag(0) == STATE_PLAYING }
    }

    override fun showError(error: Throwable) {
        Toast.makeText(context, error.message, Toast.LENGTH_LONG).show()
    }

    private val interpolator = LinearInterpolator()

    override fun updateState(playbackState: PlaybackStateDomain) {
        val objectAnimator = ObjectAnimator.ofFloat(progressBar, "progress", 100f)
        val progress = (playbackState.position * 100 / playbackState.song.duration).toInt()
        when (playbackState.state) {
            STATE_NONE -> {
                button_play_pause.setImageDrawable(null)
                progressBar.progress = 0
            }
            STATE_PLAYING -> {
                button_play_pause.run {
                    setImageResource(R.drawable.ic_pause_black_24dp)
                    setTag(0, STATE_PAUSED)
                    progressBar.progress = progress
                    objectAnimator.duration = playbackState.song.duration - playbackState.position
                    objectAnimator.interpolator = interpolator
                    objectAnimator.start()
                    objectAnimator.cancel()
                }
            }
            STATE_PAUSED, STATE_STOPPED -> button_play_pause.run {
                setImageResource(R.drawable.ic_play_arrow_black_24dp)
                setTag(0, STATE_PLAYING)
                progressBar.progress = progress
            }
        }
    }

    companion object {
        private val ARG_SONGS = "arg_songs"
        fun newInstance(): FragmentPlay = FragmentPlay()

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
            args.putParcelableArray(ARG_SONGS, album.songs.toTypedArray())
            fragment.arguments = args
            return fragment
        }
    }
}
