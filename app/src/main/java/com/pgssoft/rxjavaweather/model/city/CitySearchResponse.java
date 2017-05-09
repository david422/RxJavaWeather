package com.pgssoft.rxjavaweather.model.city;

import java.util.List;

/**
 * Created by dpodolak on 13.04.2017.
 */

public class CitySearchResponse {

    private List<City> RESULTS;

    public List<City> getCities(){
        return RESULTS;
    }
}
