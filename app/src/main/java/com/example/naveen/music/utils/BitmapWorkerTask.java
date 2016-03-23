package com.example.naveen.music.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.graphics.Palette;
import android.widget.ImageView;

import com.example.naveen.music.R;

import java.io.IOException;
import java.lang.ref.WeakReference;

public class BitmapWorkerTask extends AsyncTask<Object, Void, Bitmap> {
    private final WeakReference<ImageView> imageViewWeakReference;
    private Bitmap bitmap;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private FloatingActionButton fab;
    private SwatchFromBitmap swatchFromBitmap;

    public BitmapWorkerTask(ImageView imageView) {
        imageViewWeakReference = new WeakReference<>(imageView);
    }

    @Override
    protected Bitmap doInBackground(Object... params) {
        Context context = (Context) params[0];
        int res = 120;
        try {
            if (params.length > 2)
                res = Integer.parseInt(params[2].toString());
            if (params[1] != null && !params[1].toString().equals("")) {
                bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), (Uri) params[1]);
                bitmap = Bitmap.createScaledBitmap(bitmap, res, (res*3)/4, false);
            }
            if (params.length == 5) {
                swatchFromBitmap = new SwatchFromBitmap();
                swatchFromBitmap.generateSwatch(bitmap);
                collapsingToolbarLayout = (CollapsingToolbarLayout) params[3];
                fab = (FloatingActionButton) params[4];
            }
        } catch (IOException | NullPointerException e) {
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.unknown);
            bitmap = Bitmap.createScaledBitmap(bitmap, res, (res*3)/4, false);
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (bitmap != null) {
            final ImageView imageView = imageViewWeakReference.get();
            if (imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
            if (swatchFromBitmap!=null && collapsingToolbarLayout != null && fab != null) {
                Palette.Swatch swatch = swatchFromBitmap.getBestSwatch();
                collapsingToolbarLayout.setStatusBarScrim(new ColorDrawable(swatch.getRgb()));
                collapsingToolbarLayout.setContentScrim(new ColorDrawable(swatch.getRgb()));
                collapsingToolbarLayout.setCollapsedTitleTextColor((swatch.getTitleTextColor()));
              //  collapsingToolbarLayout.setExpandedTitleColor(swatch.getBodyTextColor());
                fab.setRippleColor(swatch.getBodyTextColor());
            }
        }
    }
}
