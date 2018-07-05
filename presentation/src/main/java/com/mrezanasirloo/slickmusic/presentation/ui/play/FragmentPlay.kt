package com.mrezanasirloo.slickmusic.presentation.ui.play


import android.animation.ObjectAnimator
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.media.session.PlaybackStateCompat.*
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
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
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
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

    @Inject
    lateinit var provider: Provider<PresenterPlay>
    @Inject
    @Presenter
    lateinit var presenter: PresenterPlay
    private val formatter = DecimalFormat("#00.###")

    private val interpolator = LinearInterpolator()

    private var obProgressbar: ObjectAnimator? = null
    private var obSeekBar: ObjectAnimator? = null
    private val adapter: GroupAdapter<ViewHolder> = GroupAdapter()
    private val playPauseSignal by lazy {
        RxView.clicks(button_play_pause)
                .mergeWith(RxView.clicks(button_play_pause_bottom))
                .throttleFirst(500, TimeUnit.MILLISECONDS).share()
    }

    private val seekBarEvent by lazy {
        RxSeekBar.changeEvents(seekBar).share()
    }

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
        seekBarEvent.ofType(SeekBarStartChangeEvent::class.java).subscribe {
            obSeekBar?.pause()
        }
        list_queue.adapter = adapter
        list_queue.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        list_queue.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
    }

    override fun onDestroyView() {
        super.onDestroyView()
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

    override fun next(): Observable<Any> {
        return RxView.clicks(button_skip_next).throttleFirst(500, TimeUnit.MILLISECONDS)
    }

    override fun previous(): Observable<Any> {
        return RxView.clicks(button_skip_previous).throttleFirst(500, TimeUnit.MILLISECONDS)
    }

    override fun seekTo(): Observable<Int> {
        return seekBarEvent.ofType(SeekBarStopChangeEvent::class.java).map { it.view().progress }
    }

    override fun updateQueue(list: List<Item>) {
        adapter.update(list)
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

                startOb(playbackState, duration, playbackState.position, obProgressbar)
                startOb(playbackState, duration, playbackState.position, obSeekBar)

                updateProgress(progress)

                textView_tittle.text = "${playbackState.song.title}\n${updateDurationText(playbackState.position, duration)}"

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
                updateProgress(progress)

                textView_tittle.text = "${playbackState.song.title}\n${updateDurationText(playbackState.position, duration)}"

                releaseObjectAnimator(obSeekBar)
                releaseObjectAnimator(obProgressbar)
            }
        }
    }

    private fun updateProgress(progress: Int) {
        progressBar.progress = progress
        seekBar.progress = progress
    }

    private fun startOb(state: PlaybackStateDomain, duration: Long, position: Long, ob: ObjectAnimator?): ObjectAnimator? {
        return ob?.also {
            it.duration = duration - position
            it.interpolator = interpolator
            it.start()
            it.addUpdateListener {
                textView_tittle.text = "${state.song.title}\n${updateDurationText(it.currentPlayTime + position, duration)}"

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

    private fun updateDurationText(position: Long = 0, duration: Long): String {
        // todo don't calculate every time
        val seconds = TimeUnit.SECONDS.convert(duration, TimeUnit.MILLISECONDS)
        val minutes = TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS)
        val secondsPassed = TimeUnit.SECONDS.convert(position, TimeUnit.MILLISECONDS)
        val minutesPassed = TimeUnit.MINUTES.convert(position, TimeUnit.MILLISECONDS)
        return "${formatter.format(minutesPassed % 60)}:${formatter.format(secondsPassed % 60)} / ${formatter.format(minutes % 60)}:${formatter.format(seconds % 60)}"
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
