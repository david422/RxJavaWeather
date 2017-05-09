package com.pgssoft.rxjavaweather;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.pgssoft.rxjavaweather.api.Api;

import net.danlew.android.joda.JodaTimeAndroid;

import timber.log.Timber;

/**
 * Created by dpodolak on 13.04.2017.
 */

public class RXApp extends Application {

    private Api api;
    private static RXApp instance;

    private DBManager dbManager;
    @Override
    public void onCreate() {
        super.onCreate();

        Stetho.initializeWithDefaults(this);
        JodaTimeAndroid.init(this);
        Timber.plant(new Timber.DebugTree());
        api = new Api(this);
        dbManager = new DBManager(this);


        instance = this;
    }

    public static RXApp getInstance(){
        return  instance;
    }

    public Api getApi(){
        return api;
    }

    public DBManager getDbManager(){
        return dbManager;
    }

}
