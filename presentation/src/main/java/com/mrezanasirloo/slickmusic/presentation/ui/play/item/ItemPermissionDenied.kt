package com.mrezanasirloo.slickmusic.presentation.ui.play.item

import android.view.View
import com.mrezanasirloo.slickmusic.R
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.OnItemLongClickListener
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.row_error_rational.*

/**
 * @author : M.Reza.Nasirloo@gmail.com
 *         Created on: 2018-06-10
 */
class ItemPermissionDenied(
        private val text: String,
        private val onClickListener: View.OnClickListener
) : Item() {
    override fun getLayout() = R.layout.row_error_rational
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.button_grant.setOnClickListener(onClickListener)
        viewHolder.button_grant.text = text
    }

    override fun unbind(holder: ViewHolder) {
        holder.button_grant.setOnClickListener(null)
        super.unbind(holder)

    }

}