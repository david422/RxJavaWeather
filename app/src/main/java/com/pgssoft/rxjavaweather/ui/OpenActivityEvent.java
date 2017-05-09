package com.pgssoft.rxjavaweather.ui;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by dpodolak on 18.04.2017.
 */

public class OpenActivityEvent {

    public static final String AddActivity = "AddActivity";

    @StringDef({AddActivity})
    @Retention(RetentionPolicy.SOURCE)
    @interface OpenEvent{}

    private String whichActivity;

    public OpenActivityEvent(@OpenEvent String whichActivity) {
        this.whichActivity = whichActivity;
    }

    public @OpenEvent String getActivity() {
        return whichActivity;
    }
}
