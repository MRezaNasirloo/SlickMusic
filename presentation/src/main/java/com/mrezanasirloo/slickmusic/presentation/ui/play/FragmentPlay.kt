package com.mrezanasirloo.slickmusic.presentation.ui.play


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
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
import com.mrezanasirloo.slickmusic.presentation.ui.play.item.ItemPermissionDenied
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_play.*
import javax.inject.Inject
import javax.inject.Provider


/**
 * A simple [Fragment] subclass.
 * Use the [FragmentPlay.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentPlay : Fragment(), ViewPlay {
    @Inject
    lateinit var provider: Provider<PresenterPlay>

    @Presenter
    lateinit var presenterPlay: PresenterPlay

    private val requestPermission: PublishSubject<Any> = PublishSubject.create()

    companion object {

        fun newInstance(): FragmentPlay = FragmentPlay()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.componentMain().inject(this)
        PresenterPlay_Slick.bind(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_play, container, false)
    }

    private val adapter: GroupAdapter<ViewHolder> = GroupAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        list.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL))
        list.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        list.adapter = adapter
    }

    override fun update(list: List<Item>) {
        adapter.update(list)
    }

    override fun showError(error: Throwable) {
        Toast.makeText(context, error.message, Toast.LENGTH_LONG).show()
    }

    override fun showRationalSettingPage() {
        adapter.clear()
        adapter.add(ItemPermissionDenied("Go To Setting", View.OnClickListener {
            startInstalledAppDetailsActivity(context)
        }))
    }

    override fun showRational() {
        adapter.clear()
        adapter.add(ItemPermissionDenied("Grant Read Permission", View.OnClickListener {
            requestPermission.onNext(1)
        }))
    }

    override fun commandPermission(): Observable<Any> {
        return requestPermission
    }

    private fun startInstalledAppDetailsActivity(context: Context?) {
        if (context == null) return
        val intent = Intent()
        intent.apply {
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            addCategory(Intent.CATEGORY_DEFAULT)
            data = Uri.parse("package:" + context.packageName)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
        }
        context.startActivity(intent)
    }

}
