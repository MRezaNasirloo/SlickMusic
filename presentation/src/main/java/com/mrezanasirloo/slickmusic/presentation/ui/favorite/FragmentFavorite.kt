package com.mrezanasirloo.slickmusic.presentation.ui.song


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import com.mrezanasirloo.domain.implementation.model.Song
import com.mrezanasirloo.slick.Presenter
import com.mrezanasirloo.slickmusic.R
import com.mrezanasirloo.slickmusic.presentation.App
import com.mrezanasirloo.slickmusic.presentation.ui.favorite.ViewFavorite
import com.mrezanasirloo.slickmusic.presentation.ui.main.BackStackFragment
import com.mrezanasirloo.slickmusic.presentation.ui.song.item.ItemSongSmall
import com.mrezanasirloo.slickmusic.presentation.ui.tageditor.ActivityTagEditor
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_song.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Provider


/**
 * A simple [Fragment] subclass.
 * Use the [FragmentSong.newInstance] factory method to
 * playSongCallback an instance of this fragment.
 */
class FragmentFavorite : BackStackFragment(), ViewFavorite {

    override fun removeFromFavorite(): Observable<Song> {
        return remove.share()
    }

    override fun triggerLoad(): Observable<Any> {
        return remove.cast(Any::class.java).mergeWith(onAddListener).delay(300, TimeUnit.MILLISECONDS)
                .cast(Any::class.java)
    }

    override fun addSongToQueue(): Observable<Collection<Song>> {
        return addSongToQueue
    }

    override fun playSongs(): Observable<Song> {
        return playSongCallback
    }

    @Inject
    lateinit var provider: Provider<PresenterFavorite>
    @Presenter
    lateinit var presenterPlay: PresenterFavorite
    private val playSongCallback = PublishSubject.create<Song>()
    private val remove = PublishSubject.create<Song>()
    private val searchCallback = PublishSubject.create<String>()
    private val searchCloseCallback = PublishSubject.create<Any>()
    private val addSongToQueue = PublishSubject.create<Collection<Song>>()

    companion object {
        fun newInstance(): FragmentFavorite = FragmentFavorite()
        val onAddListener = PublishSubject.create<Any>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.componentMain().inject(this)
        PresenterFavorite_Slick.bind(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_favorite, container, false)
    }

    private val adapter: GroupAdapter<ViewHolder> = GroupAdapter()

    override fun onViewCreated(parent: View, savedInstanceState: Bundle?) {
        super.onViewCreated(parent, savedInstanceState)
        list.adapter = adapter
        list.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        list.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        adapter.setOnItemClickListener { item, view ->
            when (view.id) {
                R.id.button_option -> {
                    val popupMenu = PopupMenu(context, view)
                    popupMenu.inflate(R.menu.menu_option_favorite)
                    popupMenu.setOnMenuItemClickListener {
                        when (it.itemId) {
                            R.id.action_add_to_queue -> {
                                addSongToQueue.onNext(listOf((item as ItemSongSmall).song))
                                return@setOnMenuItemClickListener true
                            }
                            R.id.action_edit_tag -> {
                                ActivityTagEditor.start(context!!, (item as ItemSongSmall).song)
                                return@setOnMenuItemClickListener true
                            }
                            R.id.action_remove_favorite -> {
                                remove.onNext((item as ItemSongSmall).song)
                                return@setOnMenuItemClickListener true
                            }
                            else -> return@setOnMenuItemClickListener false
                        }
                    }
                    popupMenu.show()
                }
                else -> playSongCallback.onNext((item as ItemSongSmall).song)
            }

        }
    }

    override fun update(list: List<Item>) {
        adapter.update(list)
    }

    override fun showError(error: Throwable) {
        Toast.makeText(context, error.message, Toast.LENGTH_LONG).show()
    }

}
