package com.example.nitu.popularmovies.animation;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;

public class ShowAnimation extends Animation {
    float targetWeight;
    View view;


    public ShowAnimation(View view, float targetWeight, long durationMillis) {
        this.view = view;
        this.targetWeight = targetWeight;
        this.setDuration(durationMillis);
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        ((LinearLayout.LayoutParams) view.getLayoutParams()).weight = targetWeight * interpolatedTime;
        view.requestLayout();

    }

    @Override
    public void initialize(int width, int height, int parentWidth,
                           int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
}