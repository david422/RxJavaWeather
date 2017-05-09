package com.pgssoft.rxjavaweather.api;

import com.pgssoft.rxjavaweather.model.city.CitySearchResponse;

import io.reactivex.Single;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by dpodolak on 13.04.2017.
 */

public interface SearchServcie {

    @GET("aq")
    Single<CitySearchResponse> searchCity(@Query("query") String query);

}
