package com.example.cookingquest.usuario;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.MenuItem;

import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.request.target.Target;

public class MenuTarget implements Target<Drawable> {
    private MenuItem menuItem;

    public MenuTarget(MenuItem menuItem) {
        this.menuItem = menuItem;
    }

    @Override
    public void onLoadStarted(Drawable placeholder) {}

    @Override
    public void onLoadFailed(Drawable errorDrawable) {}

    @Override
    public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
        menuItem.setIcon(resource);
    }

    @Override
    public void onLoadCleared(Drawable placeholder) {}

    @Override
    public void getSize(SizeReadyCallback cb) {
        cb.onSizeReady(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
    }

    @Override
    public void removeCallback(SizeReadyCallback cb) {}

    @Override
    public void setRequest(Request request) {}

    @Override
    public Request getRequest() {
        return null;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestroy() {

    }
}