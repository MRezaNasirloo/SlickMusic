package com.mrezanasirloo.domain.implementation

import android.app.Activity
import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.content.ComponentName
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import com.mrezanasirloo.domain.implementation.model.Song
import com.mrezanasirloo.domain.interactor.InteractorPlayback
import com.mrezanasirloo.domain.interactor.InteractorPlaybackState
import com.mrezanasirloo.domain.model.PlaybackStateDomain
import com.mrezanasirloo.domain.model.SongDomain
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import java.lang.ref.WeakReference
import javax.inject.Inject
import javax.inject.Singleton

const val COMMAND_CLEAR_QUEUE = "COMMAND_CLEAR_QUEUE"
const val COMMAND_ADD_QUEUE_ITEM = "COMMAND_ADD_QUEUE_ITEM"
const val COMMAND_REMOVE_QUEUE_ITEM = "COMMAND_REMOVE_QUEUE_ITEM"
const val COMMAND_REQUEST_PLAYBACK_STATE = "COMMAND_REQUEST_PLAYBACK_STATE"

/**
 * @author : M.Reza.Nasirloo@gmail.com
 *         Created on: 2018-07-03
 */
@Singleton
class MediaPlaybackController @Inject constructor() : DefaultLifecycleObserver, InteractorPlaybackState, InteractorPlayback {
    private val TAG: String = MediaPlaybackController::class.java.simpleName
    private lateinit var mediaBrowser: MediaBrowserCompat
    private lateinit var mediaController: MediaControllerCompat
    private val mediaControllerCallback = MediaControllerCallback()
    private val connectionCallback = MediaBrowserConnectionCallback()
    private var owner: WeakReference<LifecycleOwner>? = null

    private val playbackState: PublishSubject<PlaybackStateDomain> = io.reactivex.subjects.PublishSubject.create()


    override fun onCreate(owner: LifecycleOwner) {
        this.owner = WeakReference(owner)
        val activity = owner as Activity
        mediaBrowser = MediaBrowserCompat(
                activity.applicationContext,
                ComponentName(activity.applicationContext, "com.mrezanasirloo.slickmusic.presentation.ui.MusicService"),
                connectionCallback,
                null)

    }

    override fun onStart(owner: LifecycleOwner) {
        mediaBrowser.connect()
    }

    override fun onStop(owner: LifecycleOwner) {
        if (mediaBrowser.isConnected) {
            mediaBrowser.disconnect()
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        mediaController.unregisterCallback(mediaControllerCallback)
        this.owner?.clear()
        this.owner = null
    }

    inner class MediaBrowserConnectionCallback : MediaBrowserCompat.ConnectionCallback() {
        override fun onConnected() {
            println("$TAG.onConnected")
            if (!::mediaBrowser.isInitialized) {
                Log.e(TAG, "MediaBrowser has not initialized")
                return
            }
            val activity = owner?.get() as Activity
            mediaController = MediaControllerCompat(activity.applicationContext, mediaBrowser.sessionToken)
            MediaControllerCompat.setMediaController(activity, mediaController)
            mediaController.registerCallback(mediaControllerCallback)
            requestPlaybackState().subscribe()
        }

        override fun onConnectionSuspended() {
            super.onConnectionSuspended()
            Log.d(TAG, "onConnectionSuspended() called")
        }

        override fun onConnectionFailed() {
            super.onConnectionFailed()
            Log.d(TAG, "onConnectionFailed() called")
        }
    }

    inner class MediaControllerCallback : MediaControllerCompat.Callback() {

        override fun onQueueChanged(queue: MutableList<MediaSessionCompat.QueueItem>?) {
            super.onQueueChanged(queue)
            Log.d(TAG, "onQueueChanged() called with: queue = [$queue]")
        }

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            super.onPlaybackStateChanged(state)
            state?.let {
                playbackState.onNext(mapToDomain(state))
            }
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            super.onMetadataChanged(metadata)
            Log.d(TAG, "onMetadataChanged() called with: metadata = [$metadata]")
        }

        override fun onSessionDestroyed() {
            super.onSessionDestroyed()
            Log.d(TAG, "onSessionDestroyed() called")
        }

        private fun mapToDomain(state: PlaybackStateCompat): PlaybackStateDomain {
            return PlaybackStateDomain(state.state,
                    state.position,
                    state.actions,
                    state.playbackSpeed,
                    state.lastPositionUpdateTime,
                    state.extras?.getParcelable<Song>("SONG")?.toSongDomain()!!)
        }
    }

    override fun requestPlaybackState(): Completable {
        return Completable.create {
            mediaController.transportControls.sendCustomAction(COMMAND_REQUEST_PLAYBACK_STATE, Bundle.EMPTY)
            it.onComplete()
        }
    }

    override fun playSong(song: SongDomain): Completable {
        return playSongs(listOf(song))
    }

    override fun playSongs(songs: Iterable<SongDomain>): Completable {
        return Completable.create {
            mediaController.transportControls.sendCustomAction(COMMAND_CLEAR_QUEUE, Bundle.EMPTY)
            songs.forEach {
                addQueueSong(it)
            }
            mediaController.transportControls.prepare()
            mediaController.transportControls.play()
            it.onComplete()
        }
    }

    override fun addQueueSong(songs: Iterable<SongDomain>): Completable {
        Log.d(TAG, "addQueueSong() called with: songs = [$songs]")
        return Completable.create {
            songs.forEach {
                addQueueSong(it)
            }
            it.onComplete()
            Log.d(TAG, "addQueueSong")
        }
    }

    private fun addQueueSong(song: SongDomain) {
        Log.d(TAG, "addQueueSong() called with: song = [$song]")
        // TODO("Fix This when this issue is resolved") https://issuetracker.google.com/issues/111146580
        val builder = MediaDescriptionCompat.Builder()
//                .setExtras(bundle)
                .setMediaId(song.id.toString())
                .setTitle(song.title)
                .setSubtitle(song.artistName)
        mediaController.addQueueItem(builder.build())
        mediaController.transportControls.sendCustomAction(COMMAND_ADD_QUEUE_ITEM, Bundle().apply { putParcelable("SONG", Song(song)) })
        Log.d(TAG, "addQueueSong() finished")
    }

    override fun skipToQueueSong(id: Long): Completable {
        return Completable.create {
            mediaController.transportControls.skipToQueueItem(id)
            it.onComplete()
        }
    }

    override fun play(): Completable {
        return Completable.create {
            mediaController.transportControls.play()
            it.onComplete()
        }
    }

    override fun pause(): Completable {
        return Completable.create {
            mediaController.transportControls.pause()
            it.onComplete()
        }
    }

    override fun next(): Completable {
        return Completable.create {
            mediaController.transportControls.skipToNext()
            it.onComplete()
        }
    }

    override fun previous(): Completable {
        return Completable.create {
            mediaController.transportControls.skipToPrevious()
            it.onComplete()
        }
    }

    override fun playbackStateUpdate(): Observable<PlaybackStateDomain> {
        return playbackState
    }

}