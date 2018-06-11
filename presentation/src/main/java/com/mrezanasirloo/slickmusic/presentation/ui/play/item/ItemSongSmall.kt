package com.mrezanasirloo.slickmusic.presentation.ui.play.item

import com.mrezanasirloo.slickmusic.R
import com.mrezanasirloo.slickmusic.presentation.ui.play.model.Song
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.OnItemLongClickListener
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.row_song_small.*

/**
 * @author : M.Reza.Nasirloo@gmail.com
 *         Created on: 2018-06-10
 */
class ItemSongSmall(private val song: Song) : Item() {
    override fun getLayout() = R.layout.row_song_small

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.textView_song_title.text = song.title
        viewHolder.textView_song_artist.text = song.artistName
        viewHolder.textView_no.text = song.trackNumber.toString()
    }

}