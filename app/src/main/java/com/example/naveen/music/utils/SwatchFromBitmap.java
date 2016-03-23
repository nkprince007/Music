package com.example.naveen.music.utils;

import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.graphics.Palette;

class SwatchFromBitmap {

    private final Palette.Swatch[] swatch = new Palette.Swatch[6];

    public void generateSwatch(Bitmap bitmap) {
        Palette palette = Palette.from(bitmap).generate();
        swatch[0] = palette.getDarkVibrantSwatch();
        swatch[1] = palette.getDarkMutedSwatch();
        swatch[2] = palette.getLightMutedSwatch();
        swatch[3] = palette.getLightVibrantSwatch();
        swatch[4] = palette.getVibrantSwatch();
        swatch[5] = palette.getMutedSwatch();
    }

    public void generateSwatchAsync(Bitmap bitmap, final CollapsingToolbarLayout layout, final FloatingActionButton fab) {
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                swatch[0] = palette.getDarkVibrantSwatch();
                swatch[1] = palette.getDarkMutedSwatch();
                swatch[2] = palette.getLightMutedSwatch();
                swatch[3] = palette.getLightVibrantSwatch();
                swatch[4] = palette.getVibrantSwatch();
                swatch[5] = palette.getMutedSwatch();
                layout.setContentScrim(new ColorDrawable(getBestSwatch().getRgb()));
                fab.setRippleColor(getBestSwatch().getTitleTextColor());
            }
        });
    }

    public Palette.Swatch[] getAllSwatches() {
        return swatch;
    }

    public Palette.Swatch getBestSwatch() {
        int i = 0;
        for (int j = 1; j < 6; j++) {
            if (swatch[i] != null && swatch[j] != null)
                if (swatch[i].getPopulation() < swatch[j].getPopulation())
                    i = j;
            if (swatch[i] == null && swatch[j] != null)
                i = j;
        }
        return swatch[i];
    }
}
