package com.mrezanasirloo.slickmusic.presentation.ui

import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserServiceCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS
import android.support.v4.media.session.MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.media.session.PlaybackStateCompat.*
import com.mrezanasirloo.domain.implementation.model.Song
import com.mrezanasirloo.slickmusic.R
import java.util.*


class MusicService : MediaBrowserServiceCompat() {


    private val MY_MEDIA_ROOT_ID = "media_root_id"
    private val MY_EMPTY_MEDIA_ROOT_ID = "empty_root_id"

    private val mMediaSession: MediaSessionCompat? = null
    private val stateBuilder: PlaybackStateCompat.Builder = PlaybackStateCompat.Builder()
    private val metaDataBuilder: MediaMetadataCompat.Builder = MediaMetadataCompat.Builder()

    private var mediaPlayer: MediaPlayer? = null
    private lateinit var mediaSession: MediaSessionCompat

    override fun onCreate() {
        super.onCreate()
        mediaSession = MediaSessionCompat(this, "MediaService")
        mediaSession.setFlags(FLAG_HANDLES_MEDIA_BUTTONS or FLAG_HANDLES_TRANSPORT_CONTROLS)
        val mediaSessionCallback = MediaSessionCallback()
        mediaSession.setCallback(mediaSessionCallback)
        sessionToken = mediaSession.sessionToken

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player
        stateBuilder.setActions(ACTION_PLAY or ACTION_PLAY_PAUSE)
        mediaSession.setPlaybackState(stateBuilder.build())
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
        private val playlist = ArrayList<MediaSessionCompat.QueueItem>()
        private var queueIndex = -1
        private var mPreparedMedia: MediaMetadataCompat? = null

        override fun onAddQueueItem(description: MediaDescriptionCompat?) {
            description?.let {
                playlist.add(MediaSessionCompat.QueueItem(description, description.hashCode().toLong()))
                queueIndex = if (queueIndex == -1) 0 else queueIndex
                mediaSession.setQueue(playlist)
            }
        }

        override fun onRemoveQueueItem(description: MediaDescriptionCompat?) {
            description?.let {
                playlist.remove(MediaSessionCompat.QueueItem(description, description.hashCode().toLong()))
                queueIndex = if (playlist.isEmpty()) -1 else queueIndex
                mediaSession.setQueue(playlist)
            }
        }

        override fun onSeekTo(pos: Long) {
            super.onSeekTo(pos)
            println("MusicService.onSeekTo")
            TODO("Implement this")
        }

        override fun onSkipToQueueItem(id: Long) {
            super.onSkipToQueueItem(id)
            playlist.filter { it.queueId == id }
                    .map { queueIndex = playlist.indexOf(it) }
            onPlay()
        }

        override fun onSkipToNext() {
            super.onSkipToNext()
            println("MusicService.onSkipToNext")
            queueIndex++
            onPlay()
        }

        override fun onSkipToPrevious() {
            super.onSkipToPrevious()
            println("MusicService.onSkipToPrevious")
            queueIndex--
            onPlay()
        }

        override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
            super.onPlayFromMediaId(mediaId, extras)
            extras?.apply {
                classLoader = this@MusicService.classLoader
                val song = getParcelable<Song>("SONG")
                if (mediaPlayer == null) {
                    mediaPlayer = MediaPlayer()
                }
                if (!mediaSession.isActive) {
                    mediaSession.isActive = true
                }
                mediaPlayer?.run {
                    reset()
                    setAudioStreamType(AudioManager.STREAM_MUSIC)
                    setDataSource(song.data)
                    prepare()
                    start()
                    mediaSession.setPlaybackState(stateBuilder.setState(STATE_PLAYING, mediaPlayer?.currentPosition?.toLong()!!, 1f).build())
                }
            }
            println("onPlayFromMediaId mediaId = [${mediaId}], extras = [${extras.toString()}]")

        }

        override fun onPlay() {
            super.onPlay()
            println("MusicService.onPlay")
            if (playlist.isEmpty()) {
                // Nothing to play.
                return
            }

            playlist[queueIndex].description.extras?.apply {
                classLoader = this@MusicService.classLoader
                val song = getParcelable<Song>("SONG")
                if (mediaPlayer == null) {
                    mediaPlayer = MediaPlayer()
                }
                if (!mediaSession.isActive) {
                    mediaSession.isActive = true
                }
                mediaPlayer?.run {
                    reset()
                    setAudioStreamType(AudioManager.STREAM_MUSIC)
                    setDataSource(song.data)
                    prepare()
                    start()
                    mediaSession.setPlaybackState(stateBuilder.setState(STATE_PLAYING, mediaPlayer?.currentPosition?.toLong()!!, 1f).build())

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
            mediaSession.setPlaybackState(stateBuilder.setState(STATE_STOPPED, mediaPlayer?.currentPosition?.toLong()!!, 1f).build())

        }

        override fun onPause() {
            super.onPause()
            println("MusicService.onPause")
            mediaPlayer?.run {
                pause()
            }
            mediaSession.setPlaybackState(stateBuilder.setState(STATE_PAUSED, mediaPlayer?.currentPosition?.toLong()!!, 1f).build())
        }
    }
}
