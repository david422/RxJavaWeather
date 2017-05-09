package com.pgssoft.rxjavaweather.ui;

import android.databinding.BindingAdapter;
import android.databinding.BindingConversion;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Locale;

/**
 * Created by dawidpodolak on 27.04.2017.
 */

public class Bindings {

    @BindingAdapter("bind:check")
    public static void checkboxBinding(CheckBox checkBox, boolean check) {
        checkBox.setChecked(check);
    }

    @BindingAdapter("bind:imageUrl")
    public static void loadImageUrl(ImageView iv, String url) {
        Picasso.with(iv.getContext()).load(url).into(iv);
    }

    @BindingConversion
    public static int convertVisibility(boolean visible) {
        if (visible) {
            return View.VISIBLE;
        } else {
            return View.GONE;
        }
    }

    @BindingConversion
    public static String convertDateTime(DateTime time) {
        if (time == null) {
            return "";
        }
        DateTimeFormatter dtf = DateTimeFormat.forPattern("YYYY-MM-dd HH:mm:ss");
        dtf.withLocale(Locale.getDefault());
        return time.toString(dtf);
    }
}
