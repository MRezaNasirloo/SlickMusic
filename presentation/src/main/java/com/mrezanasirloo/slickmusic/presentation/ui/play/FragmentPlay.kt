package com.mrezanasirloo.slickmusic.presentation.ui.play


import android.content.ComponentName
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mrezanasirloo.domain.implementation.usecase.UseCaseGetAllSongImpl
import com.mrezanasirloo.domain.model.SongDomain
import com.mrezanasirloo.slickmusic.R
import com.mrezanasirloo.slickmusic.presentation.App
import com.mrezanasirloo.slickmusic.presentation.ui.MusicService
import com.mrezanasirloo.slickmusic.presentation.ui.main.BackStackFragment
import com.mrezanasirloo.slickmusic.presentation.ui.song.model.Album
import com.mrezanasirloo.slickmusic.presentation.ui.song.model.Song
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.layout_play.*
import kotlinx.android.synthetic.main.row_album_big.*
import javax.inject.Inject


/**
 * A simple [Fragment] subclass.
 * Use the [FragmentPlay.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentPlay : BackStackFragment(), ViewPlay {

    @Inject
    lateinit var useCaseGetAllSongImpl: UseCaseGetAllSongImpl

    override fun playSong(vararg song: Song) {
        println("song = [${song[0]}]")
        val bundle = Bundle()
        bundle.putParcelable("SONG", song[0])
        mediaController.transportControls.playFromMediaId(song[0].id.toString(), bundle)
    }

    // TODO: Rename and change types of parameters
    private var songs: Array<Song> = emptyArray()

    private lateinit var mediaBrowser: MediaBrowserCompat
    private lateinit var mediaController: MediaControllerCompat
    private val TAG: String = FragmentPlay::class.java.simpleName
    private var isPlaying: Boolean = false
    var song: List<SongDomain>? = null


    private val mediaControllerCallback = MediaControllerCallback()
    private val connectionCallback = MediaBrowserConnectionCallback()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.componentMain().inject(this)
        arguments?.let {
            songs = it.getParcelableArray(ARG_SONGS) as Array<Song>
        }

        useCaseGetAllSongImpl.execute(Unit)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    song = it
                }

        mediaBrowser = MediaBrowserCompat(
                context,
                ComponentName(context, MusicService::class.java),
                connectionCallback,
                null)
        // todo Sync existing MediaSession state to the UI.
    }

    override fun onStart() {
        super.onStart()
        mediaBrowser.connect()
    }

    override fun onStop() {
        super.onStop()
        if (mediaBrowser.isConnected) {
            mediaBrowser.disconnect()
        }
    }

    inner class MediaBrowserConnectionCallback : MediaBrowserCompat.ConnectionCallback() {
        override fun onConnected() {
            println("FragmentPlay.onConnected")
            if (!::mediaBrowser.isInitialized) {
                Log.e(TAG, "MediaBrowser has not initialized")
                return
            }
            mediaController = MediaControllerCompat(context?.applicationContext, mediaBrowser.sessionToken)
            activity?.let {
                MediaControllerCompat.setMediaController(it, mediaController)
            }

            mediaBrowser.subscribe(mediaBrowser.root, object : MediaBrowserCompat.SubscriptionCallback() {
                override fun onChildrenLoaded(parentId: String, children: MutableList<MediaBrowserCompat.MediaItem>) {
                    super.onChildrenLoaded(parentId, children)
                    println("parentId = [${parentId}], children = [${children}]")
//                                mediaController.transportControls.prepare()
                }
            })
            mediaController.registerCallback(mediaControllerCallback)

        }

        override fun onConnectionSuspended() {
            super.onConnectionSuspended()
            println("FragmentPlay.onConnectionSuspended")
        }

        override fun onConnectionFailed() {
            super.onConnectionFailed()
            println("FragmentPlay.onConnectionFailed")
        }
    }

    inner class MediaControllerCallback : MediaControllerCompat.Callback() {

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            super.onPlaybackStateChanged(state)
            state?.let {
                if (state.state == PlaybackStateCompat.STATE_PLAYING) {
                    isPlaying = true
                    button_play_pause.setImageResource(R.drawable.ic_pause_black_24dp)
                } else {
                    isPlaying = false
                    button_play_pause.setImageResource(R.drawable.ic_play_arrow_black_24dp)
                }
            }
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            super.onMetadataChanged(metadata)
        }

        override fun onSessionDestroyed() {
            super.onSessionDestroyed()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_play, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        button_play_pause.setOnClickListener {
            val controls = mediaController.transportControls
            if (isPlaying) controls.pause() else controls.play()
        }

        imageView_album_play.setOnClickListener {
            println("FragmentPlay.onClicked")
            playSong(Song(song!![0]))
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        mediaBrowser.disconnect()
        mediaController.unregisterCallback(mediaControllerCallback)
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
            args.putParcelableArray(ARG_SONGS, album.songs.toTypedArray())
            fragment.arguments = args
            return fragment
        }
    }

}
