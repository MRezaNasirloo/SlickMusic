package com.mrezanasirloo.slickmusic.presentation.ui.tageditor

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.mrezanasirloo.domain.implementation.model.Song
import com.mrezanasirloo.domain.model.SongTagDomain
import com.mrezanasirloo.slick.Presenter
import com.mrezanasirloo.slickmusic.R
import com.mrezanasirloo.slickmusic.presentation.App
import kotlinx.android.synthetic.main.activity_tag_editor.*
import javax.inject.Inject
import javax.inject.Provider

const val ARG_SONG = "ARG_SONG"

class ActivityTagEditor : AppCompatActivity(), ViewTagEditor {

    companion object {
        fun start(context: Context, song: Song): Unit {
            val intent = Intent(context, ActivityTagEditor::class.java).also {
                it.putExtra(ARG_SONG, song)
            }
            context.startActivity(intent)
        }
    }

    override fun showSong(songTag: SongTagDomain) {
        val song = songTag.songDomain
        editText_song.setText(song.title)
        editText_artist.setText(song.artistName)
        editText_album.setText(song.albumName)
        editText_genre.setText(songTag.genre)
        editText_track.setText(song.trackNumber.toString())
        editText_year.setText(song.year.toString())
        songTag.byte?.let {
            imageView.setImageBitmap(BitmapFactory.decodeByteArray(it, 0, it.size))
        }

    }

    override fun song(): Song {
        return song
    }

    @Inject
    lateinit var provider: Provider<PresenterTagEditor>
    @Presenter
    lateinit var presenterTagEditor: PresenterTagEditor
    private lateinit var song: Song


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tag_editor)
        App.componentMain().inject(this)
        PresenterTagEditor_Slick.bind(this)
        song = intent.extras.getParcelable(ARG_SONG) ?: throw IllegalStateException("Song cannot be null")
    }
}
