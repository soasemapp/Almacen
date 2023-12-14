package com.almacen.alamacen202.includes;

import android.graphics.drawable.Drawable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.almacen.alamacen202.R;


public class MyToolbar {

    public static void show(AppCompatActivity activity, String title, boolean upButton) {

        Toolbar toolbar = activity.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setTitle(title);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(upButton);
        Drawable nav = toolbar.getNavigationIcon();
        if(nav != null) {
            nav.setTint(activity.getResources().getColor(R.color.ColorWhite));
        }
    }

}
