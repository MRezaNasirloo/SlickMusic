package com.mrezanasirloo.slickmusic.presentation.ui.album


import android.content.res.Configuration
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.mrezanasirloo.slick.Presenter
import com.mrezanasirloo.slickmusic.R
import com.mrezanasirloo.slickmusic.presentation.App
import com.mrezanasirloo.slickmusic.presentation.ui.main.BackStackFragment
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.fragment_song.*
import javax.inject.Inject
import javax.inject.Provider


/**
 * A simple [Fragment] subclass.
 * Use the [FragmentAlbum.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentAlbum : BackStackFragment(), ViewAlbum {
    @Inject
    lateinit var provider: Provider<PresenterAlbum>

    @Presenter
    lateinit var presenterPlay: PresenterAlbum

    companion object {

        fun newInstance(): FragmentAlbum = FragmentAlbum()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.componentMain().inject(this)
        PresenterAlbum_Slick.bind(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_album, container, false)
    }

    private val adapter: GroupAdapter<ViewHolder> = GroupAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        list.adapter = adapter
        list.layoutManager = GridLayoutManager(context, getSpanCount(), LinearLayoutManager.VERTICAL, false)
    }

    private fun getSpanCount() = if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) 2 else 4

    override fun update(list: List<Item>) {
        adapter.update(list)
    }

    override fun showError(error: Throwable) {
        error.printStackTrace()
        Toast.makeText(context, error.message, Toast.LENGTH_LONG).show()
    }

}
