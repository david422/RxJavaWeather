package com.pgssoft.rxjavaweather.api;

import com.pgssoft.rxjavaweather.model.condition.ConditionResponse;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by dpodolak on 13.04.2017.
 */

public interface WeatherService {

    ///q/zmw:94102.1.99999
    @GET("conditions/q/zmw:{fpath}.json")
    public Single<ConditionResponse> getWeather(@Path("fpath") String fullPath);

}
