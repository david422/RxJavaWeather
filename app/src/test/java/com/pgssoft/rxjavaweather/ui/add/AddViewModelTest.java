package com.pgssoft.rxjavaweather.ui.add;

import android.os.Build;
import android.support.annotation.RequiresApi;

import com.pgssoft.rxjavaweather.ApiDataProvider;
import com.pgssoft.rxjavaweather.DBManager;
import com.pgssoft.rxjavaweather.RXApp;
import com.pgssoft.rxjavaweather.RxJUnitTest;
import com.pgssoft.rxjavaweather.api.Api;
import com.pgssoft.rxjavaweather.api.SearchServcie;
import com.pgssoft.rxjavaweather.model.city.City;
import com.pgssoft.rxjavaweather.model.city.CityHelper;
import com.pgssoft.rxjavaweather.model.city.CitySearchResponse;
import com.pgssoft.rxjavaweather.ui.ProgressEvent;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.TestObserver;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by dpodolak on 05.05.2017.
 */
public class AddViewModelTest extends RxJUnitTest{

    private AddViewModel addViewModel;

    @Mock
    Api api;

    @Mock
    DBManager dbManager;

    @Mock
    SearchServcie searchService;

    @Mock
    CitySearchResponse citySearchResponse;

    @Mock
    CityHelper cityHelper;

    private ApiDataProvider apiDataProvidera = new ApiDataProvider();

    private List<City> allCities = apiDataProvidera.allCities;

    @Before
    public void setup() {
        super.setUp();


        when(dbManager.getCityHelper()).thenReturn(cityHelper);


        when(cityHelper.getCities()).thenReturn(Flowable.fromIterable(allCities));
        when(api.getSearchService()).thenReturn(searchService);

        addViewModel = new AddViewModel(api, dbManager);

    }

    @Test
    public void getRecyclerviewVisible() {

        assertEquals(false, addViewModel.getRecyclerviewVisible().get());
        setQueryOnSearchText("war");
        testScheduler.advanceTimeBy(AddViewModel.CLICK_DEBOUNCE_TIME, TimeUnit.MILLISECONDS);
        assertEquals(true, addViewModel.getRecyclerviewVisible().get());

        setQueryOnSearchText("tes");
        testScheduler.advanceTimeBy(AddViewModel.CLICK_DEBOUNCE_TIME, TimeUnit.MILLISECONDS);
        assertEquals(false, addViewModel.getRecyclerviewVisible().get());

    }

    @Test
    public void getCitiesObservable() {
        TestObserver<List<AddViewModel.CityViewModel>> testObserver = new TestObserver<>();
        addViewModel.getCitiesObservable().subscribe(testObserver);

        String query = "war";
        setQueryOnSearchText(query);
        testScheduler.advanceTimeBy(AddViewModel.CLICK_DEBOUNCE_TIME, TimeUnit.MILLISECONDS);

        Predicate<List<AddViewModel.CityViewModel>> predicate = cityViewModels -> {

            for (AddViewModel.CityViewModel cvm : cityViewModels) {
                if (!cvm.city.getName().toLowerCase().startsWith(query.toLowerCase())) {
                    return false;
                }
            }
            return true;
        };

        testObserver.assertValue(predicate);

        String queryRze = "rze";
        setQueryOnSearchText(queryRze);
        testScheduler.advanceTimeBy(AddViewModel.CLICK_DEBOUNCE_TIME, TimeUnit.MILLISECONDS);

        Predicate<List<AddViewModel.CityViewModel>> predicateRze = cityViewModels -> {

            for (AddViewModel.CityViewModel cvm : cityViewModels) {
                if (!cvm.city.getName().toLowerCase().startsWith(queryRze.toLowerCase())) {
                    return false;
                }
            }
            return true;
        };

        //TestObserver keeps all emitted objects. The previous object has a result with war query
        testObserver.assertValueAt(1, predicateRze);

    }

    @Test
    public void getProgressObservable() {

        //prepare
        TestObserver<ProgressEvent> progressEventTestObserver = addViewModel.getProgressObservable().test();

        //when search is success
        String query = "war";
        when(citySearchResponse.getCities()).thenReturn(getCitiesStartWith(query));
        when(searchService.searchCity(query)).thenReturn(Single.just(citySearchResponse));

        //test
        addViewModel.getSearchTextObservable().set(query);
        testScheduler.advanceTimeBy(AddViewModel.CLICK_DEBOUNCE_TIME, TimeUnit.MILLISECONDS);

        //check result
        progressEventTestObserver.assertValueAt(0, progressEvent -> progressEvent.getState() == ProgressEvent.SHOW);
        progressEventTestObserver.assertValueAt(1, progressEvent -> progressEvent.getState() == ProgressEvent.CLOSE);


        //when search is an error e.g. TimeoutException
        query = "rze";
        when(citySearchResponse.getCities()).thenReturn(getCitiesStartWith(query));
        when(searchService.searchCity(query)).thenReturn(Single.create(e -> {
            // TODO: 08.05.2017 mask stacktrace
            e.onError(new TimeoutException());
        }));

        //test
        addViewModel.getSearchTextObservable().set(query);
        testScheduler.advanceTimeBy(AddViewModel.CLICK_DEBOUNCE_TIME, TimeUnit.MILLISECONDS);

        //check
        progressEventTestObserver.assertValueAt(2, progressEvent -> progressEvent.getState() == ProgressEvent.SHOW);
        progressEventTestObserver.assertValueAt(3, progressEvent -> progressEvent.getState() == ProgressEvent.CLOSE);
        progressEventTestObserver.assertValueAt(4, progressEvent -> progressEvent.getState() == ProgressEvent.ERROR);

    }

    private void setQueryOnSearchText(String query) {
        when(citySearchResponse.getCities()).thenReturn(getCitiesStartWith(query));
        when(searchService.searchCity(query)).thenReturn(Single.just(citySearchResponse));
        addViewModel.getSearchTextObservable().set(query);
    }

    public List<City> getCitiesStartWith(String prefix) {
        List<City> citiesStartWith = new ArrayList<>();
        for (City c : allCities) {
            if (c.getName().toLowerCase().startsWith(prefix.toLowerCase())) {
                citiesStartWith.add(c);
            }
        }
        return citiesStartWith;
    }

    @Test
    public void searchClear() {
        //prepare
        addViewModel.getSearchTextObservable().set("war");

        //check
        assertEquals("war", addViewModel.getSearchTextObservable().get());

        //test
        addViewModel.searchClear();

        //check
        assertEquals("", addViewModel.getSearchTextObservable().get());
    }

    @Test
    public void submit() {

        //prepare
        AddViewModel.SuccessEventCallback successEventCallback = mock(AddViewModel.SuccessEventCallback.class);
        when(cityHelper.insert(any(City.class))).thenReturn(Completable.complete());
        when(cityHelper.remove(any(City.class))).thenReturn(Completable.complete());

        addViewModel.setSuccessEvent(successEventCallback);

        //when
        addViewModel.submit();

        //check
        verify(successEventCallback, atLeastOnce()).noDataModified();

        //prepare to handling click cityViewModel
        TestObserver<List<AddViewModel.CityViewModel>> cvObserver = addViewModel.getCitiesObservable().test();
        setQueryOnSearchText("war");
        testScheduler.advanceTimeBy(AddViewModel.CLICK_DEBOUNCE_TIME, TimeUnit.MILLISECONDS);
        cvObserver.assertValueCount(1);

        List<Object> listCVM = cvObserver.getEvents().get(0);
        List<AddViewModel.CityViewModel> firstCityVm = (List<AddViewModel.CityViewModel>) listCVM.get(0);

        //when
        firstCityVm.get(0).click();
        addViewModel.submit();

        //check
        verify(successEventCallback, atLeastOnce()).success();

    }

    /**
     * Assume that in local db are data and search result also contains data which are same.
     * In that case CityViewModel should has checkObservable set on true
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Test
    public void checkIfViewModelChecked() {

        //prepare
        TestObserver<List<AddViewModel.CityViewModel>> cvmList = addViewModel.getCitiesObservable().test();
        List<City> localDBCities =  allCities.stream()
                .filter(city -> city.getName().equals("Warsaw, Poland") || city.getName().equals("Rzeszów, Polska"))
                .collect(Collectors.toList());
        when(cityHelper.getCities()).thenReturn(Flowable.fromIterable(localDBCities));

        //when
        String query = "rze";

        //test
        setQueryOnSearchText(query);
        testScheduler.advanceTimeBy(AddViewModel.CLICK_DEBOUNCE_TIME, TimeUnit.MILLISECONDS);

        //check
        cvmList.assertValueAt(0, list -> {
            boolean isProperVMSelected = false;
            for (AddViewModel.CityViewModel cityVm: list){
                if (cityVm.city.getName().equals("Rzeszów, Polska") && cityVm.checkObservable.get()){
                    isProperVMSelected = true;
                    break;
                }
            }
            return isProperVMSelected;
        });

        //when
        query = "war";

        //test
        setQueryOnSearchText(query);
        testScheduler.advanceTimeBy(AddViewModel.CLICK_DEBOUNCE_TIME, TimeUnit.MILLISECONDS);

        //check
        cvmList.assertValueAt(1, list -> {
            boolean isProperVMSelected = false;
            for (AddViewModel.CityViewModel cityVm: list){
                if (cityVm.city.getName().equals("Warsaw, Poland") && cityVm.checkObservable.get()){
                    isProperVMSelected = true;
                    break;
                }
            }
            return isProperVMSelected;
        });


    }

}
