package com.pgssoft.rxjavaweather.ui.main;

import com.pgssoft.rxjavaweather.ApiDataProvider;
import com.pgssoft.rxjavaweather.DBManager;
import com.pgssoft.rxjavaweather.RxJUnitTest;
import com.pgssoft.rxjavaweather.api.Api;
import com.pgssoft.rxjavaweather.api.WeatherService;
import com.pgssoft.rxjavaweather.model.city.City;
import com.pgssoft.rxjavaweather.model.city.CityHelper;
import com.pgssoft.rxjavaweather.model.condition.Condition;
import com.pgssoft.rxjavaweather.model.condition.ConditionHelper;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.List;
import java.util.Map;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.observers.TestObserver;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Created by dpodolak on 08.05.2017.
 */
public class MainViewModelTest extends RxJUnitTest {

    ApiDataProvider dataProvider;

    Map<String, Condition> apiCondition;

    @Mock
    DBManager dbManager;

    @Mock
    Api api;

    @Mock
    WeatherService weatherService;

    @Mock
    CityHelper cityHelper;

    @Mock
    ConditionHelper conditionHelper;

    MainViewModel mainViewModel;

    @Before
    public void setUp()  {
        MockitoAnnotations.initMocks(this);

        super.setUp();

        dataProvider = new ApiDataProvider();
        List<City> localDBCities = dataProvider.allCities;
        apiCondition = dataProvider.citiesCondition;

        when(api.getWeatherService()).thenReturn(weatherService);
        when(weatherService.getWeather(anyString())).thenAnswer(new Answer<Condition>() {
            @Override
            public Condition answer(InvocationOnMock invocationOnMock) throws Throwable {

                String path = invocationOnMock.getArgument(0).toString();

                return apiCondition.get(path);
            }
        });

        when(cityHelper.getCities()).thenReturn(Flowable.fromIterable(localDBCities));
        when(conditionHelper.insertOrUpdateCondition(any(Condition.class))).then(new Answer<Single<Condition>>() {
            @Override
            public Single<Condition> answer(InvocationOnMock invocationOnMock) throws Throwable {

                return Single.just((Condition) invocationOnMock.getMock());
            }
        });
        when(dbManager.getCityHelper()).thenReturn(cityHelper);
        when(dbManager.getConditionHelper()).thenReturn(conditionHelper);
    }

    @Test
    public void placeholder(){
        mainViewModel = new MainViewModel(api, dbManager);
        TestObserver to = mainViewModel.citiesObservable().test();
        mainViewModel.updateAndShowWeather();
        to.assertValue(list -> {
            System.out.println("Size: " + ((List)list).size());
            return true;
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