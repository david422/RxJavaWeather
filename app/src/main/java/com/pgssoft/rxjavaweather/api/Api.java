package com.pgssoft.rxjavaweather.api;

import android.content.Context;

import com.facebook.stetho.common.StringUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.pgssoft.rxjavaweather.R;
import com.pgssoft.rxjavaweather.model.city.City;

import org.joda.time.DateTime;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by dawidpodolak on 27.04.2017.
 */

public class Api {

    private Context context;

    private Retrofit.Builder rBuilder;
    public Api(Context context){
        this.context = context;

        OkHttpClient.Builder client = new OkHttpClient.Builder();

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
        client.addNetworkInterceptor(interceptor);

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(DateTime.class, (JsonDeserializer<DateTime>) (json, typeOfT, context1) -> {
                    //example Thu, 04 May 2017 01:30:56 -0700
                    String dateToParse = json.getAsString();
                    DateTime dateTime =  new DateTime();
                    try {
                        Date formattedDate = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US).parse(dateToParse);
                        dateTime = new DateTime(formattedDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    return dateTime;
                })
                .create();

        rBuilder = new Retrofit.Builder()
                .client(client.build())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(context.getString(R.string.base_url, context.getString(R.string.wheater_underground)));
    }

    public SearchServcie getSearchService (){
        return rBuilder.baseUrl(context.getString(R.string.autocomplete_endpoint)).build().create(SearchServcie.class);
    }

    public WeatherService getWeatherService (){
        return rBuilder.build().create(WeatherService.class);
    }
}
