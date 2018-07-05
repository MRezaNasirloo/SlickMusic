package com.mrezanasirloo.slickmusic.presentation.ui.song.item

import android.annotation.SuppressLint
import com.mrezanasirloo.domain.implementation.model.Song
import com.mrezanasirloo.slickmusic.R
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.row_song_small.*

/**
 * @author : M.Reza.Nasirloo@gmail.com
 *         Created on: 2018-06-10
 */
class ItemSongQueue(val song: Song) : Item() {
    override fun getLayout() = R.layout.row_song_small

    @SuppressLint("SetTextI18n")
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.textView_song_title.text = song.title
        viewHolder.textView_song_artist.text = song.artistName
        viewHolder.textView_no.text = (position + 1).toString()
    }

}