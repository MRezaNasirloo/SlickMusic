package com.mrezanasirloo.slickmusic.presentation.glide;

import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.widget.ImageView;

import com.bumptech.glide.request.animation.GlideAnimation;
import com.mrezanasirloo.slickmusic.R;
import com.mrezanasirloo.slickmusic.presentation.glide.palette.BitmapPaletteTarget;
import com.mrezanasirloo.slickmusic.presentation.glide.palette.BitmapPaletteWrapper;
import com.mrezanasirloo.slickmusic.presentation.util.PhonographColorUtil;


public abstract class PhonographColoredTarget extends BitmapPaletteTarget {
    public PhonographColoredTarget(ImageView view) {
        super(view);
    }

    @Override
    public void onLoadFailed(Exception e, Drawable errorDrawable) {
        super.onLoadFailed(e, errorDrawable);
        onColorReady(getDefaultFooterColor());
    }

    @Override
    public void onResourceReady(BitmapPaletteWrapper resource, GlideAnimation<? super BitmapPaletteWrapper> glideAnimation) {
        super.onResourceReady(resource, glideAnimation);
        onColorReady(PhonographColorUtil.getColor(resource.getPalette(), getDefaultFooterColor()));
    }

    protected int getDefaultFooterColor() {
        return ResourcesCompat.getColor(getView().getResources(), R.color.colorPrimary, null);
    }

    protected int getAlbumArtistFooterColor() {
        return ResourcesCompat.getColor(getView().getResources(), R.color.colorPrimaryDark, null);
    }

    public abstract void onColorReady(int color);
}
