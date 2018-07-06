package com.mrezanasirloo.slickmusic.presentation.ui.main

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Rect
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetBehavior.STATE_COLLAPSED
import android.support.design.widget.BottomSheetBehavior.STATE_EXPANDED
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.FrameLayout
import com.mrezanasirloo.domain.implementation.MediaPlaybackController
import com.mrezanasirloo.slick.Presenter
import com.mrezanasirloo.slickmusic.R
import com.mrezanasirloo.slickmusic.presentation.App
import com.mrezanasirloo.slickmusic.presentation.openAppSettingPage
import com.mrezanasirloo.slickmusic.presentation.ui.album.FragmentAlbum
import com.mrezanasirloo.slickmusic.presentation.ui.play.FragmentPlay
import com.mrezanasirloo.slickmusic.presentation.ui.song.FragmentSong
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.row_error_rational.*
import kotlinx.android.synthetic.main.row_error_rational.view.*
import javax.inject.Inject
import javax.inject.Provider


class ActivityMain : AppCompatActivity(), ViewMain {
    @Inject
    lateinit var provider: Provider<PresenterMain>

    @Presenter
    lateinit var presenter: PresenterMain

    @Inject
    lateinit var mediaPlaybackController: MediaPlaybackController

    private val requestPermission: PublishSubject<Any> = PublishSubject.create()
    private var bottomSheetBehavior: BottomSheetBehavior<FrameLayout>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        App.componentMain().inject(this)
        PresenterMain_Slick.bind(this)
        val fm = supportFragmentManager
        val tag = FragmentPlay::class.java.simpleName
        if (fm.findFragmentByTag(tag) == null) {
            fm.beginTransaction()
                    .add(R.id.container_play, FragmentPlay.newInstance(), tag)
                    .commit()
        }
        lifecycle.addObserver(mediaPlaybackController)
        bottomSheetBehavior = BottomSheetBehavior.from(container_play)
    }

    override fun onResume() {
        super.onResume()
        // TODO: 2018-06-11 Remove logic from view
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED)
            requestPermission.onNext(1)
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            if (bottomSheetBehavior?.state == STATE_EXPANDED) {
                val outRect = Rect()
                container_play.getGlobalVisibleRect(outRect)

                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt()))
                    bottomSheetBehavior?.state = STATE_COLLAPSED
            }
        }

        return super.dispatchTouchEvent(event)
    }

    override fun showPages() {
        println("ActivityMain.showPages")
        permission_view.visibility = INVISIBLE
        navigation.visibility = VISIBLE
        container_play.visibility = VISIBLE
        view_pager.visibility = VISIBLE
        view_pager.offscreenPageLimit = 2
        view_pager.adapter = Pager(supportFragmentManager)
        navigation.setOnNavigationItemSelectedListener(BottomNavListener())
    }

    override fun showError(error: Throwable) {
        error.printStackTrace()
        println("ActivityMain.showError")
        error.message?.let {
            Snackbar.make(window.decorView, it, Snackbar.LENGTH_LONG).show()
        }
    }

    override fun showRationalSettingPage() {
        println("ActivityMain.showRationalSettingPage")
        val button = permission_view.button_grant
        view_pager.visibility = INVISIBLE
        navigation.visibility = INVISIBLE
        container_play.visibility = INVISIBLE
        permission_view.visibility = VISIBLE
        button.setText(R.string.message_go_to_settings)
        button_grant.setOnClickListener {
            openAppSettingPage()
        }
    }

    override fun showRational() {
        println("ActivityMain.showRational")
        val button = permission_view.button_grant
        view_pager.visibility = INVISIBLE
        navigation.visibility = INVISIBLE
        container_play.visibility = INVISIBLE
        permission_view.visibility = VISIBLE
        button.setText(R.string.message_grant_read_permission)
        button.setOnClickListener {
            requestPermission.onNext(1)
        }
    }

    fun toggleBottomSheet() {
        if (bottomSheetBehavior?.state == STATE_EXPANDED) bottomSheetBehavior?.state = STATE_COLLAPSED else bottomSheetBehavior?.state = STATE_EXPANDED
    }

    override fun commandPermission(): Observable<Any> {
        return requestPermission
    }

    override fun onBackPressed() {
        if (BackStackFragment.handleBackPressed(supportFragmentManager)) return
        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(mediaPlaybackController)
    }

    inner class BottomNavListener : BottomNavigationView.OnNavigationItemSelectedListener {
        override fun onNavigationItemSelected(item: MenuItem): Boolean {
            return when (item.itemId) {
                R.id.navigation_album -> {
                    view_pager.currentItem = 0
                    true
                }
                R.id.navigation_songs -> {
                    view_pager.currentItem = 1
                    true
                }
                R.id.navigation_favorite -> {
                    view_pager.currentItem = 3
                    true
                }
                else -> false
            }
        }
    }
}

class Pager(fm: FragmentManager?) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> FragmentAlbum.newInstance()
            1 -> FragmentSong.newInstance()
            2 -> TODO("FragmentFavorite Not Implemented")
            else -> throw IllegalStateException()
        }
    }

    override fun getCount(): Int {
        return 2
    }
}

