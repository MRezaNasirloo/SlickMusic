package com.mrezanasirloo.slickmusic.presentation.ui

import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.ResultReceiver
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserServiceCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.MediaSessionCompat.*
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.media.session.PlaybackStateCompat.*
import android.util.Log
import com.mrezanasirloo.domain.implementation.COMMAND_ADD_QUEUE_ITEM
import com.mrezanasirloo.domain.implementation.COMMAND_CLEAR_QUEUE
import com.mrezanasirloo.domain.implementation.COMMAND_REMOVE_QUEUE_ITEM
import com.mrezanasirloo.domain.implementation.COMMAND_REQUEST_PLAYBACK_STATE
import com.mrezanasirloo.domain.implementation.model.Song
import com.mrezanasirloo.slickmusic.R
import java.util.*


class MusicService : MediaBrowserServiceCompat() {


    private val TAG: String = this::class.java.simpleName

    private val MY_MEDIA_ROOT_ID = "media_root_id"
    private val MY_EMPTY_MEDIA_ROOT_ID = "empty_root_id"

    private val mMediaSession: MediaSessionCompat? = null
    private val stateBuilder: PlaybackStateCompat.Builder = PlaybackStateCompat.Builder()
    private val metaDataBuilder: MediaMetadataCompat.Builder = MediaMetadataCompat.Builder()
    private var playbackState: PlaybackStateCompat? = null


    private var mediaPlayer: MediaPlayer? = null
    private lateinit var mediaSession: MediaSessionCompat

    override fun onCreate() {
        super.onCreate()
        mediaSession = MediaSessionCompat(this, "MediaService")
        mediaSession.setFlags(FLAG_HANDLES_MEDIA_BUTTONS or FLAG_HANDLES_TRANSPORT_CONTROLS or FLAG_HANDLES_QUEUE_COMMANDS)
        val mediaSessionCallback = MediaSessionCallback()
        mediaSession.setCallback(mediaSessionCallback)
        sessionToken = mediaSession.sessionToken

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player
        stateBuilder.setActions(ACTION_PLAY or ACTION_PLAY_PAUSE)
        setNewState(stateBuilder.build())
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.let {
            it.stop()
            it.release()
        }
        mediaPlayer = null
        mediaSession.release()
    }

    override fun onLoadChildren(parentId: String, result: Result<MutableList<MediaBrowserCompat.MediaItem>>) {
        result.sendResult(null)
    }

    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?): BrowserRoot? {
        return BrowserRoot(getString(R.string.app_name), null)
    }

    inner class MediaSessionCallback : MediaSessionCompat.Callback() {
        private val playlistQueueItem = ArrayList<MediaSessionCompat.QueueItem>()
        private val playlistSong = ArrayList<Song>()
        private var queueIndex = -1
        private var mPreparedMedia: MediaMetadataCompat? = null

        override fun onAddQueueItem(description: MediaDescriptionCompat?) {
            Log.d(TAG, "onAddQueueItem() called with: description = [$description]")
            description?.let {
                playlistQueueItem.add(MediaSessionCompat.QueueItem(description, description.hashCode().toLong()))
                queueIndex = if (queueIndex == -1) 0 else queueIndex
                mediaSession.setQueue(playlistQueueItem)
            }
        }

        override fun onRemoveQueueItem(description: MediaDescriptionCompat?) {
            Log.d(TAG, "onRemoveQueueItem() called with: description = [$description]")
            description?.let {
                playlistQueueItem.remove(MediaSessionCompat.QueueItem(description, description.hashCode().toLong()))
                queueIndex = if (playlistQueueItem.isEmpty()) -1 else queueIndex
                mediaSession.setQueue(playlistQueueItem)
            }
        }

        override fun onCustomAction(action: String?, extras: Bundle?) {
            Log.d(TAG, "onCustomAction() called with: action = [$action], extras = [$extras]")
            when (action) {
                COMMAND_CLEAR_QUEUE -> {
                    playlistQueueItem.clear()
                    playlistSong.clear()
                    queueIndex = -1
                    mediaSession.setQueue(playlistQueueItem)
                }
                COMMAND_ADD_QUEUE_ITEM -> {
                    val song = extras?.run {
                        classLoader = this@MusicService.classLoader
                        getParcelable<Song>("SONG")
                    }
                    playlistSong.add(song!!)

                }
                COMMAND_REMOVE_QUEUE_ITEM -> {
                    val song = extras?.run {
                        classLoader = this@MusicService.classLoader
                        getParcelable<Song>("SONG")
                    }
                    playlistSong.remove(song!!)
                }
                COMMAND_REQUEST_PLAYBACK_STATE -> mediaPlayer?.run {
                    setNewState(stateBuilder.setState(playbackState?.state!!, mediaPlayer?.currentPosition?.toLong()!!, 1f).build())
                }
            }
        }

        override fun onCommand(command: String?, extras: Bundle?, cb: ResultReceiver?) {
            Log.d(TAG, "onCommand() called with: command = [$command], extras = [$extras], cb = [$cb]")
        }

        override fun onSeekTo(pos: Long) {
            super.onSeekTo(pos)
            println("MusicService.onSeekTo")
            if (queueIndex == -1) return
            val newPos = playlistSong[queueIndex].duration * pos / 1000
            mediaPlayer?.seekTo(newPos.toInt())
        }

        override fun onSkipToQueueItem(id: Long) {
            super.onSkipToQueueItem(id)
            playlistQueueItem.filter { it.queueId == id }
                    .map { queueIndex = playlistQueueItem.indexOf(it) }
            onPrepare()
            onPlay()
        }

        override fun onSkipToNext() {
            super.onSkipToNext()
            println("MusicService.onSkipToNext")
            if (playlistQueueItem.isEmpty() || playlistQueueItem.size == 1) return
            queueIndex++
            onPrepare()
            onPlay()
        }

        override fun onSkipToPrevious() {
            super.onSkipToPrevious()
            println("MusicService.onSkipToPrevious")
            if (playlistQueueItem.isEmpty() || playlistQueueItem.size == 1) return
            queueIndex--
            onPrepare()
            onPlay()
        }

        override fun onPrepare() {
            super.onPrepare()
            playlistSong[queueIndex].also { song ->
                if (mediaPlayer == null) {
                    mediaPlayer = MediaPlayer()
                    mediaPlayer?.setOnCompletionListener {
                        setNewState(stateBuilder.setState(STATE_STOPPED, mediaPlayer?.currentPosition?.toLong()!!, 1f).build())
                    }
                    mediaPlayer?.setOnSeekCompleteListener {
                        setNewState(stateBuilder.setState(playbackState?.state!!, mediaPlayer?.currentPosition?.toLong()!!, 1f).build())
                    }
                }
                if (!mediaSession.isActive) {
                    mediaSession.isActive = true
                }
                mediaPlayer?.run {
                    startService(Intent(this@MusicService, MusicService::class.java))
                    reset()
                    setAudioStreamType(AudioManager.STREAM_MUSIC)
                    setDataSource(song.data)
                    prepare()
                }
            }

        }

        override fun onPlay() {
            super.onPlay()
            println("MusicService.onPlay")
            if (playlistQueueItem.isEmpty()) {
                // Nothing to play.
                return
            }

            playlistSong[queueIndex].also { song ->
                if (mediaPlayer == null) {
                    onPrepare()
                }
                mediaPlayer?.run {
                    start()
                    val playbackState = stateBuilder
                            .setState(STATE_PLAYING, mediaPlayer?.currentPosition?.toLong()!!, 1f)
                            .setExtras(Bundle().apply {
                                putParcelable("SONG", song)
                            }).build()
                    setNewState(playbackState)

                    metaDataBuilder.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, song.artistName)
                            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST, song.artistName)
                            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, song.albumName)
                            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, song.title)
                            .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, song.duration)
                            .putLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER, (queueIndex + 1).toLong())
                            .putLong(MediaMetadataCompat.METADATA_KEY_YEAR, song.year.toLong())
                            .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, null)

                    mediaSession.setMetadata(metaDataBuilder.build())
                }
            }
        }

        override fun onStop() {
            super.onStop()
            println("MusicService.onStop")
            mediaPlayer?.run {
                stop()
                reset()
            }
            mediaSession.isActive = false
            setNewState(stateBuilder.setState(STATE_STOPPED, mediaPlayer?.currentPosition?.toLong()!!, 1f).build())
        }

        override fun onPause() {
            super.onPause()
            println("MusicService.onPause")
            mediaPlayer?.run {
                pause()
            }
            setNewState(stateBuilder.setState(STATE_PAUSED, mediaPlayer?.currentPosition?.toLong()!!, 1f).build())
        }
    }

    private fun setNewState(newState: PlaybackStateCompat?) {
        playbackState = newState
        mediaSession.setPlaybackState(playbackState)
    }
}
