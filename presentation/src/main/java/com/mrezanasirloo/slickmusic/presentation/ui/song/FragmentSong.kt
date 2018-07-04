package com.mrezanasirloo.slickmusic.presentation.ui.song


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.mrezanasirloo.slick.Presenter
import com.mrezanasirloo.slickmusic.R
import com.mrezanasirloo.slickmusic.presentation.App
import com.mrezanasirloo.slickmusic.presentation.ui.main.BackStackFragment
import com.mrezanasirloo.slickmusic.presentation.ui.main.OnPlaybackCommands
import com.mrezanasirloo.slickmusic.presentation.ui.song.item.ItemSongSmall
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.fragment_song.*
import javax.inject.Inject
import javax.inject.Provider


/**
 * A simple [Fragment] subclass.
 * Use the [FragmentSong.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentSong : BackStackFragment(), ViewSong {
    @Inject
    lateinit var provider: Provider<PresenterSong>
    @Presenter
    lateinit var presenterPlay: PresenterSong

    private var onPlaybackCommands: OnPlaybackCommands? = null

    companion object {
        fun newInstance(): FragmentSong = FragmentSong()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.componentMain().inject(this)
        PresenterSong_Slick.bind(this)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            onPlaybackCommands = context as OnPlaybackCommands
        } catch (e: ClassCastException) {
            throw RuntimeException("${activity?.javaClass?.simpleName} must implement ${OnPlaybackCommands::class.java.simpleName}")
        }
    }

    override fun onDetach() {
        super.onDetach()
        onPlaybackCommands = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_song, container, false)
    }

    private val adapter: GroupAdapter<ViewHolder> = GroupAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        list.adapter = adapter
        list.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        list.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        adapter.setOnItemClickListener { item, _ ->
            onPlaybackCommands?.playSongs((item as ItemSongSmall).song)
        }
    }

    override fun update(list: List<Item>) {
        adapter.update(list)
    }

    override fun showError(error: Throwable) {
        Toast.makeText(context, error.message, Toast.LENGTH_LONG).show()
    }

}
