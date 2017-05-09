package com.pgssoft.rxjavaweather.ui.main;

import com.pgssoft.rxjavaweather.ApiDataProvider;
import com.pgssoft.rxjavaweather.DBManager;
import com.pgssoft.rxjavaweather.RxJUnitTest;
import com.pgssoft.rxjavaweather.api.Api;
import com.pgssoft.rxjavaweather.api.WeatherService;
import com.pgssoft.rxjavaweather.model.city.City;
import com.pgssoft.rxjavaweather.model.condition.Condition;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Created by dpodolak on 08.05.2017.
 */
public class MainViewModelTest extends RxJUnitTest {

    ApiDataProvider dataProvider = new ApiDataProvider();

    List<City> localDBCities = dataProvider.allCities;

    Map<String, Condition> apiCondition = dataProvider.citiesCondition;

    @Mock
    DBManager dbManager;

    @Mock
    Api api;

    @Mock
    WeatherService weatherService;

    @Before
    public void setUp()  {
        super.setUp();

        when(api.getWeatherService()).thenReturn(weatherService);
        when(weatherService.getWeather(anyString())).thenAnswer(new Answer<Condition>() {
            @Override
            public Condition answer(InvocationOnMock invocationOnMock) throws Throwable {

                String path = invocationOnMock.getArgument(0).toString();

                return apiCondition.get(path);
            }
        });
    }


    @Test
    public void updateAndShowWeather() throws Exception {

    }

    @Test
    public void getOpenActivityObservable() throws Exception {

    }

    @Test
    public void citiesObservable() throws Exception {

    }

}