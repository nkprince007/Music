package com.example.naveen.music.extras.recycler_view_extras;

import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;

public abstract class CustomAnimationDrawable extends AnimationDrawable {
    private Handler mAnimationHandler;

    public CustomAnimationDrawable(AnimationDrawable animationDrawable) {
        for (int i = 0; i < animationDrawable.getNumberOfFrames(); i++) {
            this.addFrame(animationDrawable.getFrame(i), animationDrawable.getDuration(i));
        }
    }

    @Override
    public void start() {
        super.start();
        mAnimationHandler = new Handler();
        mAnimationHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                onAnimationFinish();
            }
        }, getTotalDuration());
    }

    private int getTotalDuration() {
        int duration = 0;
        for (int i = 0; i < this.getNumberOfFrames(); i++) {
            duration += this.getDuration(i);
        }
        return duration;
    }

    public abstract void onAnimationFinish();
}
