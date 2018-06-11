package com.mrezanasirloo.slickmusic.presentation.glide.palette;

import android.content.Context;
import android.graphics.Bitmap;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.transcode.ResourceTranscoder;
import com.mrezanasirloo.slickmusic.presentation.util.PhonographColorUtil;

public class BitmapPaletteTransCoder implements ResourceTranscoder<Bitmap, BitmapPaletteWrapper> {
    private final BitmapPool bitmapPool;

    public BitmapPaletteTransCoder(Context context) {
        this(Glide.get(context).getBitmapPool());
    }

    public BitmapPaletteTransCoder(BitmapPool bitmapPool) {
        this.bitmapPool = bitmapPool;
    }

    @Override
    public Resource<BitmapPaletteWrapper> transcode(Resource<Bitmap> bitmapResource) {
        Bitmap bitmap = bitmapResource.get();
        BitmapPaletteWrapper bitmapPaletteWrapper = new BitmapPaletteWrapper(bitmap, PhonographColorUtil.generatePalette(bitmap));
        return new BitmapPaletteResource(bitmapPaletteWrapper, bitmapPool);
    }

    @Override
    public String getId() {
        return "BitmapPaletteTransCoder.com.kabouzeid.gramophone.glide.palette";
    }
}