package com.example.naveen.music.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.naveen.music.R;

import java.io.IOException;

public class SongActivity extends AppCompatActivity {

    private final Palette.Swatch[] colors = new Palette.Swatch[6];
    private final String[] names = new String[6];
    private int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);
        final View view = findViewById(R.id.rel_layout);
//        pauseDrawable = getResources().getDrawable(R.drawable.pause);
//        playDrawable = getResources().getDrawable(R.drawable.play);
        Uri uri = Uri.parse(getIntent().getStringExtra("uri"));
        final ImageView imageView = (ImageView) findViewById(R.id.imageView2);
        final TextView textView = (TextView) findViewById(R.id.palette_text);
        textView.setTextSize(30.0f);
        initToolBar();
        Bitmap bitmap;
        try {
            int res = getResources().getDisplayMetrics().widthPixels;
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            bitmap = Bitmap.createScaledBitmap(bitmap, res, res, false);
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.unknown);
        }
        imageView.setImageBitmap(bitmap);
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                colors[0] = palette.getDarkVibrantSwatch();
                colors[1] = palette.getDarkMutedSwatch();
                colors[2] = palette.getLightMutedSwatch();
                colors[3] = palette.getLightVibrantSwatch();
                colors[4] = palette.getVibrantSwatch();
                colors[5] = palette.getMutedSwatch();
                names[0] = "DarkVibrantColor";
                names[1] = "DarkMutedColor";
                names[2] = "LightMutedColor";
                names[3] = "LightVibrantColor";
                names[4] = "VibrantColor";
                names[5] = "MutedColor";
                int i = 0;
                for (int j = 1; j < 6; j++) {
                    if (colors[i] != null && colors[j] != null)
                        if (colors[i].getPopulation() < colors[j].getPopulation())
                            i = j;
                    if (colors[i] == null && colors[j] != null)
                        i = j;
                }
                view.setBackgroundColor(colors[i].getRgb());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getWindow().setNavigationBarColor(colors[i].getRgb());
                }
                textView.setTextColor(colors[i].getTitleTextColor());
                textView.setText(names[i]);
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (colors[i] == null) {
                    view.setBackgroundColor(0);
                    String s = names[i] + "Swatch is null";
                    textView.setText(s);
                    i = (i + 1) % 6;
                    return;
                }
                view.setBackgroundColor(colors[i].getRgb());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getWindow().setNavigationBarColor(colors[i].getRgb());
                }
                textView.setTextColor(Color.rgb(Color.red(colors[i].getTitleTextColor()),
                        Color.green(colors[i].getTitleTextColor()),
                        Color.blue(colors[i].getTitleTextColor())));
                textView.setText(names[i]);
                i = (i + 1) % 6;
            }
        });
    }

//    public void playClicked(View view) {
//        final ImageView imageView = ((ImageView) view);
//        if (isPlay)
//            animateToPause(imageView);
//        else animateToPlay(imageView);
//        isPlay = !isPlay;
//    }
//
//    private void animateToPause(ImageView view) {
//        if (playDrawable instanceof Animatable && Build.VERSION.SDK_INT >= 21) {
//            Animatable animatable = (Animatable) playDrawable;
//            animatable.start();
//        }
//        view.setImageDrawable(playDrawable);
//    }
//
//    private void animateToPlay(ImageView view) {
//        if (pauseDrawable instanceof Animatable && Build.VERSION.SDK_INT >= 21) {
//            Animatable animatable = (Animatable) pauseDrawable;
//            animatable.start();
//        }
//        view.setImageDrawable(pauseDrawable);
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.song_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search)
            return true;
        if (id == android.R.id.home)
            NavUtils.navigateUpFromSameTask(this);
        return super.onOptionsItemSelected(item);
    }

    private void initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("");
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
}




