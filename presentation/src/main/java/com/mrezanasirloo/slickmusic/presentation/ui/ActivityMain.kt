package com.mrezanasirloo.slickmusic.presentation.ui

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import com.mrezanasirloo.slickmusic.R
import com.mrezanasirloo.slickmusic.presentation.ui.play.FragmentPlay
import kotlinx.android.synthetic.main.activity_main.*

class ActivityMain : AppCompatActivity() {

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val tag = FragmentPlay::class.java.simpleName
        val fm = supportFragmentManager
        if (fm.findFragmentByTag(tag) == null) {
            fm.beginTransaction()
                    .replace(R.id.container, FragmentPlay.newInstance(), tag)
                    .commit()
        }
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }
}
