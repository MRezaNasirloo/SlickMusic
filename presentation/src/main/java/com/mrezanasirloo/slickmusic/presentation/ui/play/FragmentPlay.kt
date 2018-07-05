package com.mrezanasirloo.slickmusic.presentation.ui.play


import android.animation.ObjectAnimator
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.media.session.PlaybackStateCompat.*
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.Toast
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxSeekBar
import com.jakewharton.rxbinding2.widget.SeekBarStartChangeEvent
import com.jakewharton.rxbinding2.widget.SeekBarStopChangeEvent
import com.mrezanasirloo.domain.implementation.model.Song
import com.mrezanasirloo.domain.model.PlaybackStateDomain
import com.mrezanasirloo.slick.Presenter
import com.mrezanasirloo.slickmusic.R
import com.mrezanasirloo.slickmusic.presentation.App
import com.mrezanasirloo.slickmusic.presentation.ui.main.BackStackFragment
import com.mrezanasirloo.slickmusic.presentation.ui.song.model.Album
import io.reactivex.Observable
import kotlinx.android.synthetic.main.layout_play.*
import java.text.DecimalFormat
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

    private val formatter = DecimalFormat("#00.###")
    private val interpolator = LinearInterpolator()
    private var playPauseSignal: Observable<Any>? = null
    private var obProgressbar: ObjectAnimator? = null
    private var obSeekBar: ObjectAnimator? = null
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

    private val seekBarEvent by lazy {
        RxSeekBar.changeEvents(seekBar).share()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        playPauseSignal = RxView.clicks(button_play_pause)
                .mergeWith(RxView.clicks(button_play_pause_bottom))
                .throttleFirst(1, TimeUnit.SECONDS).share()

        seekBarEvent.ofType(SeekBarStartChangeEvent::class.java).subscribe {
            obSeekBar?.pause()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        playPauseSignal = null
    }

    override fun onStop() {
        super.onStop()
        releaseObjectAnimator(obProgressbar)
        releaseObjectAnimator(obSeekBar)
    }

    override fun play(): Observable<Any> {
        return playPauseSignal?.filter { button_play_pause.getTag(R.id.button_play_pause) == STATE_PAUSED }!!
    }

    override fun pause(): Observable<Any> {
        return playPauseSignal?.filter { button_play_pause.getTag(R.id.button_play_pause) == STATE_PLAYING }!!
    }

    override fun seekTo(): Observable<Int> {
        return seekBarEvent.ofType(SeekBarStopChangeEvent::class.java).map { it.view().progress }
    }

    override fun showError(error: Throwable) {
        Toast.makeText(context, error.message, Toast.LENGTH_LONG).show()
    }


    override fun updateState(playbackState: PlaybackStateDomain) {
        Log.d(TAG, "updateState() called with: playbackState = [$playbackState]")
        val duration = playbackState.song.duration
        when (playbackState.state) {
            STATE_NONE -> {
                button_play_pause.setImageDrawable(null)
                updateProgress(0)
            }
            STATE_PLAYING -> {
                releaseObjectAnimator(obProgressbar)
                releaseObjectAnimator(obSeekBar)

                val progress = (playbackState.position * 1000 / duration).toInt()
                obProgressbar = ObjectAnimator.ofInt(progressBar, "progress", progress, 1000)
                obSeekBar = ObjectAnimator.ofInt(seekBar, "progress", progress, 1000)

                startOb(duration, playbackState.position, obProgressbar)
                startOb(duration, playbackState.position, obSeekBar)

                updateProgress(progress)
                updateDurationText(playbackState.position, duration)

                textView_tittle.text = playbackState.song.title

                button_play_pause.run {
                    setImageResource(R.drawable.ic_pause_black_24dp)
                    button_play_pause_bottom.setImageResource(R.drawable.ic_pause_black_24dp)
                    setTag(R.id.button_play_pause, STATE_PLAYING)
                }

            }
            STATE_PAUSED, STATE_STOPPED -> button_play_pause.run {
                setImageResource(R.drawable.ic_play_arrow_black_24dp)
                setTag(R.id.button_play_pause, STATE_PAUSED)
                button_play_pause_bottom.setImageResource(R.drawable.ic_play_arrow_black_24dp)

                val progress = (playbackState.position * 1000 / duration).toInt()

                updateDurationText(playbackState.position, duration)
                updateProgress(progress)

                textView_tittle.text = playbackState.song.title

                releaseObjectAnimator(obSeekBar)
                releaseObjectAnimator(obProgressbar)
            }
        }
    }

    private fun updateProgress(progress: Int) {
        progressBar.progress = progress
        seekBar.progress = progress
    }

    private fun startOb(duration: Long, position: Long, ob: ObjectAnimator?): ObjectAnimator? {
        return ob?.also {
            it.duration = duration - position
            it.interpolator = interpolator
            it.start()
            it.addUpdateListener {
                updateDurationText(it.currentPlayTime + position, duration)
            }
        }
    }

    private fun releaseObjectAnimator(ob: ObjectAnimator?) {
        ob?.run {
            removeAllUpdateListeners()
            removeAllListeners()
            cancel()
        }
    }

    private fun updateDurationText(position: Long = 0, duration: Long) {
        // todo don't calculate every time
        val seconds = TimeUnit.SECONDS.convert(duration, TimeUnit.MILLISECONDS)
        val minutes = TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS)
        val secondsPassed = TimeUnit.SECONDS.convert(position, TimeUnit.MILLISECONDS)
        val minutesPassed = TimeUnit.MINUTES.convert(position, TimeUnit.MILLISECONDS)
        textView_remaining?.text = "${formatter.format(minutesPassed % 60)}:${formatter.format(secondsPassed % 60)} / ${formatter.format(minutes % 60)}:${formatter.format(seconds % 60)}"
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

private fun <T> Observable<T>.logIt(tag: String): Observable<T> {
    return doOnEach { notif ->
        Log.d(tag, "logIt() called with: notif = [$notif]")
    }
}
