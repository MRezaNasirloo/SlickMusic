package com.mrezanasirloo.slickmusic.presentation.ui.album.item

import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import com.mrezanasirloo.slickmusic.R
import com.mrezanasirloo.slickmusic.presentation.glide.PhonographColoredTarget
import com.mrezanasirloo.slickmusic.presentation.glide.SongGlideRequest
import com.mrezanasirloo.slickmusic.presentation.ui.song.model.Album
import com.mrezanasirloo.slickmusic.presentation.util.ColorUtil
import com.mrezanasirloo.slickmusic.presentation.util.MaterialValueHelper
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.row_album_big.*

/**
 * @author : M.Reza.Nasirloo@gmail.com
 *         Created on: 2018-06-11
 */
class ItemAlbumBig(private val album: Album) : Item() {

    override fun getLayout(): Int = R.layout.row_album_big

    override fun bind(viewHolder: ViewHolder, position: Int) {
        val context = viewHolder.containerView.context
        SongGlideRequest.Builder.from(Glide.with(context), album.safeGetFirstSong())
                .generatePalette(context).build()
                .into(object : PhonographColoredTarget(viewHolder.imageView_album_art) {
                    override fun onLoadCleared(placeholder: Drawable?) {
                        super.onLoadCleared(placeholder)
                        setColors(defaultFooterColor, viewHolder)
                    }

                    override fun onColorReady(color: Int) {
                        setColors(color, viewHolder)
                    }
                })
        viewHolder.textView_album_name.text = album.getTitle()
        viewHolder.textView_artist_name.text = album.getArtistName()
    }

    override fun getSpanSize(spanCount: Int, position: Int): Int {
        return 1
    }

    private fun setColors(color: Int, holder: ViewHolder?) {
        holder?.apply {
            container_palette?.setBackgroundColor(color)
            val context = containerView.context
            textView_album_name?.setTextColor(MaterialValueHelper.getPrimaryTextColor(context, ColorUtil.isColorLight(color)))
            textView_artist_name?.setTextColor(MaterialValueHelper.getSecondaryTextColor(context, ColorUtil.isColorLight(color)))
        }
    }
}