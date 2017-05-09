package com.pgssoft.rxjavaweather.ui;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.StringRes;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by dawidpodolak on 27.04.2017.
 */

public class ProgressEvent {

    public static final int SHOW = 0;
    public static final int CLOSE = 1;
    public static final int ERROR = 2;

    @IntDef({SHOW, CLOSE, ERROR})
    @Retention(RetentionPolicy.SOURCE)
    @interface ProgressState{}

    private @ProgressEvent.ProgressState int state;

    @StringRes
    private int stringId;

    public ProgressEvent(@ProgressState int state) {
        this.state = state;
    }

    public ProgressEvent(int state, int stringId) {
        this.state = state;
        this.stringId = stringId;
    }

    public @ProgressState int getState() {
        return state;
    }

    public String getMessage(Context context) {
        if (stringId == 0){
            return null;
        }

        return context.getString(stringId);
    }


}
