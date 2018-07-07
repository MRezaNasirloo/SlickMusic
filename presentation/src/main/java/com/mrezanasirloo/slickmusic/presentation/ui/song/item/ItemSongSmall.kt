package com.mrezanasirloo.slickmusic.presentation.ui.song.item

import android.annotation.SuppressLint
import com.mrezanasirloo.domain.implementation.model.Song
import com.mrezanasirloo.slickmusic.R
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.OnItemLongClickListener
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.row_song_small.*
import kotlin.math.abs

/**
 * @author : M.Reza.Nasirloo@gmail.com
 *         Created on: 2018-06-10
 */
class ItemSongSmall(val song: Song) : Item() {
    override fun getLayout() = R.layout.row_song_small

    @SuppressLint("SetTextI18n")
    override fun bind(viewHolder: ViewHolder, position: Int) {
        val context = viewHolder.containerView.context
        viewHolder.textView_song_title.text = song.title
        viewHolder.textView_song_artist.text = song.artistName
        viewHolder.textView_no.text = (abs(song.trackNumber) % 1000).toString()
    }

    override fun bind(holder: ViewHolder, position: Int, payloads: MutableList<Any>, onItemClickListener: OnItemClickListener?, onItemLongClickListener: OnItemLongClickListener?) {
        super.bind(holder, position, payloads, onItemClickListener, onItemLongClickListener)
        holder.button_option.setOnClickListener {
            onItemClickListener?.onItemClick(this, holder.button_option)
        }
    }

}