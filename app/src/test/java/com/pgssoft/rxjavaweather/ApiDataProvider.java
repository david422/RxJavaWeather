package com.pgssoft.rxjavaweather;

import com.pgssoft.rxjavaweather.model.city.City;
import com.pgssoft.rxjavaweather.model.condition.Condition;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dpodolak on 08.05.2017.
 */

public class ApiDataProvider {

    public List<City> allCities = new ArrayList<>();
    public Map<String, Condition> citiesCondition = new HashMap<>();

    public ApiDataProvider(){
        City city1 = new City(null, "Warsaw, Poland", "Europe/Warsaw", "00000.123.12372", "52.259998 21.020000", null);
        City city2 = new City(null, "Warangal, India", "Asia/Kolkata", "00000.1.43087", "18.299999 79.580002", null);
        City city3 = new City(null, "Warri, Nigeria", "Asia/Kolkata", "00000.226.65250", "4.300000 6.250000", null);
        City city4 = new City(null, "Rzeszów, Polska", "Europe/Warsaw", "00000.1.12580", "50.380001 22.670000", null);
        City city5 = new City(null, "Rzesin, Poland", "Europe/Warsaw", "00000.198.12210", "53.889999 15.120000", null);
        Collections.addAll(allCities, city1, city2, city3, city4, city5);

        Condition warsawCondition = new Condition(null, null, "Partly Cloudy", "24.0", "From the NW at 2.2 MPH Gusting to 3.8 MPH", "NW", "3.5", "1018", "10.0", "http://icons.wxug.com/i/c/k/partlycloudy.gif", 0);
        Condition warangalCondition = new Condition(null, null, "Overcast", "10.5", "From the SSW at 6.2 MPH Gusting to 9.3 MPH", "SSW", "10.5", "1020", "10.0", "http://icons.wxug.com/i/c/k/cloudy.gif", 0);
        Condition warriCondition = new Condition(null, null, "Partly Cloudy", "30", "From the South at 7 MPH", "South", "11", "1012", "10.0", "http://icons.wxug.com/i/c/k/partlycloudy.gif", 0);
        Condition rzeszowCondition = new Condition(null, null, "Rain", "12.2", "Calm", "SSW", "1.4", "1007", "10.0", "http://icons.wxug.com/i/c/k/rain.gif", 0);
        Condition rzesinCondition = new Condition(null, null, "Mostly Cloudy", "8.2", "From the West at 3.1 MPH Gusting to 6.8 MPH", "West", "5.0", "1017", "10.0", "http://icons.wxug.com/i/c/k/mostlycloudy.gif", 0);

        citiesCondition.put("00000.123.12372", warsawCondition);
        citiesCondition.put("00000.1.43087", warangalCondition);
        citiesCondition.put("00000.226.65250", warriCondition);
        citiesCondition.put("00000.1.12580", rzeszowCondition);
        citiesCondition.put("00000.198.12210", rzesinCondition);
    }


}
