package com.mrezanasirloo.slickmusic.presentation.ui.main

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import com.mrezanasirloo.slick.Presenter
import com.mrezanasirloo.slickmusic.R
import com.mrezanasirloo.slickmusic.presentation.App
import com.mrezanasirloo.slickmusic.presentation.openAppSettingPage
import com.mrezanasirloo.slickmusic.presentation.ui.play.FragmentPlay
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.row_error_rational.*
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
        App.componentMain().inject(this)
        PresenterMain_Slick.bind(this)
    }

    override fun showPages() {
        setContentView(R.layout.activity_main)
        val tag = FragmentPlay::class.java.simpleName
        val fm = supportFragmentManager
        if (fm.findFragmentByTag(tag) == null) {
            fm.beginTransaction()
                    .replace(R.id.container, FragmentPlay.newInstance(), tag)
                    .commit()
        }
    }

    override fun showError(error: Throwable) {
        error.message?.let {
            Snackbar.make(window.decorView, it, Snackbar.LENGTH_LONG).show()
        }
    }

    override fun showRationalSettingPage() {
        setContentView(R.layout.row_error_rational)
        val button = findViewById<Button>(R.id.button_grant)
        button.setText(R.string.message_go_to_settings)
        button_grant.setOnClickListener {
            openAppSettingPage()
        }
    }

    override fun showRational() {
        setContentView(R.layout.row_error_rational)
        val button = findViewById<Button>(R.id.button_grant)
        button.setText(R.string.message_grant_read_permission)
        button.setOnClickListener {
            requestPermission.onNext(1)
        }
    }

    override fun commandPermission(): Observable<Any> {
        return requestPermission
    }
}
