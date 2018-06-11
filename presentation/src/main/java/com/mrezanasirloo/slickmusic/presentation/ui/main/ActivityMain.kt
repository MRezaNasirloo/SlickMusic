package com.mrezanasirloo.slickmusic.presentation.ui.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import com.mrezanasirloo.slick.Presenter
import com.mrezanasirloo.slickmusic.R
import com.mrezanasirloo.slickmusic.presentation.App
import com.mrezanasirloo.slickmusic.presentation.openAppSettingPage
import com.mrezanasirloo.slickmusic.presentation.ui.album.FragmentAlbum
import com.mrezanasirloo.slickmusic.presentation.ui.play.FragmentPlay
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

    private val requestPermission: PublishSubject<Any> = PublishSubject.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        App.componentMain().inject(this)
        PresenterMain_Slick.bind(this)
    }

    override fun onResume() {
        super.onResume()
        // TODO: 2018-06-11 Remove logic from view
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED)
            requestPermission.onNext(1)
    }

    override fun showPages() {
        println("ActivityMain.showPages")
        permission_view.visibility = INVISIBLE
        navigation.visibility = VISIBLE
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
        permission_view.visibility = VISIBLE
        button.setText(R.string.message_grant_read_permission)
        button.setOnClickListener {
            requestPermission.onNext(1)
        }
    }

    override fun commandPermission(): Observable<Any> {
        return requestPermission
    }

    override fun onBackPressed() {
        if (BackStackFragment.handleBackPressed(supportFragmentManager)) return
        super.onBackPressed()

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
            1 -> FragmentPlay.newInstance()
            2 -> TODO("FragmentFavorite Not Implemented")
            else -> throw IllegalStateException()
        }
    }

    override fun getCount(): Int {
        return 2
    }
}

