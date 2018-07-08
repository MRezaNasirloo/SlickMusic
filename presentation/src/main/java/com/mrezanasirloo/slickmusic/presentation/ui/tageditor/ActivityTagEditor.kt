package com.mrezanasirloo.slickmusic.presentation.ui.tageditor

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.mrezanasirloo.domain.implementation.model.Song
import com.mrezanasirloo.domain.model.SongTagDomain
import com.mrezanasirloo.slick.Presenter
import com.mrezanasirloo.slickmusic.R
import com.mrezanasirloo.slickmusic.presentation.App
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_tag_editor.*
import javax.inject.Inject
import javax.inject.Provider


const val ARG_SONG = "ARG_SONG"
const val REQUEST_CODE_SELECT_IMAGE = 222

class ActivityTagEditor : AppCompatActivity(), ViewTagEditor {
    companion object {
        fun start(context: Context, song: Song): Unit {
            val intent = Intent(context, ActivityTagEditor::class.java).also {
                it.putExtra(ARG_SONG, song)
            }
            context.startActivity(intent)
        }
    }

    private val saveCallback: PublishSubject<SongTagDomain> = PublishSubject.create()

    @Inject
    lateinit var provider: Provider<PresenterTagEditor>

    @Presenter
    lateinit var presenterTagEditor: PresenterTagEditor

    private lateinit var song: Song
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tag_editor)
        title = "Tag Editor"
        App.componentMain().inject(this)
        PresenterTagEditor_Slick.bind(this)
        song = intent.extras.getParcelable(ARG_SONG) ?: throw IllegalStateException("Song cannot be null")

//        imageView.setOnClickListener {
//            val intent = Intent(Intent.ACTION_GET_CONTENT)
//            intent.type = "image/*"
//            startActivityForResult(Intent.createChooser(intent, "Pick up an image"), REQUEST_CODE_SELECT_IMAGE)
//        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_SELECT_IMAGE) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_tag_editor, menu)
        return true
    }

    override fun save(): Observable<SongTagDomain> {
        return saveCallback
    }

    override fun showSong(songTag: SongTagDomain) {
        val song = songTag.songDomain
        editText_song.setText(song.title)
        editText_artist.setText(song.artistName)
        editText_album.setText(song.albumName)
        editText_genre.setText(songTag.genre)
        editText_track.setText(songTag.track)
        editText_year.setText(songTag.year)
        songTag.byte?.let {
            imageView.setImageBitmap(BitmapFactory.decodeByteArray(it, 0, it.size))
        }

    }

    override fun song(): Song {
        return song
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_save -> {
                Log.d(TAG, "onOptionsItemSelected: ")
                val songTagDomain = SongTagDomain(
                        Song(
                                song.dbId,
                                song.id,
                                editText_song.text.toString(),
                                song.trackNumber,
                                song.year,
                                song.duration,
                                song.data,
                                song.dateModified,
                                song.albumId,
                                editText_album.text.toString(),
                                song.artistId,
                                editText_artist.text.toString()
                        ).toSongDomain(),
                        editText_genre.text.toString(),
                        editText_year.text.toString(),
                        editText_track.text.toString(),
                        null
                )
                saveCallback.onNext(songTagDomain)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private val TAG: String = this::class.java.simpleName

}
