package com.pgssoft.rxjavaweather.ui.add;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;

import com.jakewharton.rxrelay2.PublishRelay;
import com.pgssoft.rxjavaweather.DBManager;
import com.pgssoft.rxjavaweather.R;
import com.pgssoft.rxjavaweather.api.Api;
import com.pgssoft.rxjavaweather.model.city.City;
import com.pgssoft.rxjavaweather.model.city.CitySearchResponse;
import com.pgssoft.rxjavaweather.ui.ProgressEvent;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by dpodolak on 13.04.2017.
 */

public class AddViewModel implements SearchViewModel {

    /**
     * Delay between typing chars, trigger query to API
     */
    public static final int CLICK_DEBOUNCE_TIME = 500;

    /**
     * Cause that recycler view with result appear or disappear
     */
    private ObservableBoolean recyclerViewVisible = new ObservableBoolean(false);

    /**
     * Stream to notify View with appriopriate progress Bar event
     */
    private PublishRelay<ProgressEvent> progressRelay = PublishRelay.create();

    /**
     * Stream passing new results to show
     */
    private PublishRelay<List<CityViewModel>> citiesRelay = PublishRelay.create();

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    /**
     * Listening for changes in search
     */
    private ObservableField<String> searchObservable = new ObservableField();

    /**
     * Stream handling click item on recyclerView. Is use in viewModel
     */
    private PublishRelay<AddViewModel.CityClickEvent> cityClickRelay = PublishRelay.create();

    private SuccessEventCallback successEvent;

    /**
     * Indicate whether user add or remove some cities from repo
     */
    private boolean isDataModified;

    private DBManager dbManager;

    public AddViewModel(Api api, DBManager dbManager) {

        this.dbManager = dbManager;
        Flowable<City> localCityObserable = dbManager.getCityHelper().getCities();

        observeCityClick();

        Observable.create((ObservableOnSubscribe<String>) e ->
                //listen for every change
                searchObservable.addOnPropertyChangedCallback(new android.databinding.Observable.OnPropertyChangedCallback() {
                    @Override
                    public void onPropertyChanged(android.databinding.Observable observable, int i) {
                        e.onNext(searchObservable.get());
                    }
                }))
                .filter(query -> query.length() > 2) // query have to contains at least 2 chars
                .debounce(CLICK_DEBOUNCE_TIME, TimeUnit.MILLISECONDS) // if query not change for 500ms
                .doOnNext(string -> progressRelay.accept(new ProgressEvent(ProgressEvent.SHOW, R.string.search_cities))) //show progress bar
                .flatMapSingle(api.getSearchService()::searchCity) //get cities for this query
                .map(CitySearchResponse::getCities) // get city list from response
                .flatMapSingle(cities -> Observable.fromIterable(cities) //emity city one by one
                        .map(city -> new CityViewModel(cityClickRelay, city)) //wrap city to cityViewModel
                        .toList() //catch all cityViewModels to list
                        .flatMap(cityVMList ->

                                //if in local exists city which is on list, set checkbox
                                localCityObserable
                                        .doOnNext(city -> { //if some city from search is current in local database, check it in viewmodel
                                            for (CityViewModel cvm : cityVMList) {
                                                if (cvm.city.equals(city)) {
                                                    cvm.checkObservable.set(true);
                                                }
                                            }
                                        })
                                        .lastOrError() //wait for last element or return error
                                        .map(c -> cityVMList) // if it is last element, return cityVMList
                                        .onErrorResumeNext(e -> Single.just(cityVMList)) // handle above error NoSuchElementException, return also cityVMList
                        )
                )

                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(e -> {

                    //if exception is other than TimeoutException, show stacktrace
                    if (!(e instanceof TimeoutException)) {
                        e.printStackTrace();
                    }

                    //close progress bar and show error popup
                    progressRelay.accept(new ProgressEvent(ProgressEvent.CLOSE));
                    progressRelay.accept(new ProgressEvent(ProgressEvent.ERROR, R.string.error_occured_during_search));
                })
                .retry() //error interrupt stream. So if occur any error, subscribe observable again
                .subscribe(new Observer<List<CityViewModel>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(List<CityViewModel> s) {
                        recyclerViewVisible.set(!s.isEmpty());
                        citiesRelay.accept(s);
                        progressRelay.accept(new ProgressEvent(ProgressEvent.CLOSE));
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        //onError is handled before retry() operator
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void close() {
        compositeDisposable.clear();
    }

    @Override
    public void searchClear() {
        searchObservable.set("");
    }

    @Override
    public ObservableField<String> getSearchTextObservable() {
        return searchObservable;
    }

    public Observable<ProgressEvent> getProgressObservable() {
        return progressRelay;
    }

    public ObservableBoolean getRecyclerViewVisible() {
        return recyclerViewVisible;
    }

    public Observable<List<CityViewModel>> getCitiesObservable() {
        return citiesRelay;
    }

    public SearchViewModel getSearchViewModel() {
        return this;
    }

    /**
     * Init listening onClick in result list
     */
    public void observeCityClick() {
        compositeDisposable.add(cityClickRelay
                .observeOn(Schedulers.io())
                .flatMapCompletable(event -> {
                    isDataModified = true;
                    Timber.d("city selected");
                    if (event.isSelected) {
                        return dbManager.getCityHelper().insert(event.city);
                    } else {
                        return dbManager.getCityHelper().remove(event.city);
                    }
                })
                .subscribe());
    }

    public void setSuccessEvent(SuccessEventCallback successEvent) {
        this.successEvent = successEvent;
    }

    public void submit() {
        if (isDataModified) {
            successEvent.success();
        } else {
            successEvent.noDataModified();
        }
    }

    /**
     * Event emit by cityViewModel when user click
     */
    public static class CityClickEvent {
        private City city;

        private boolean isSelected;

        public CityClickEvent(City city, boolean isSelected) {
            this.city = city;
            this.isSelected = isSelected;
        }

        public City getCity() {
            return city;
        }

        public boolean isSelected() {
            return isSelected;
        }
    }

    /**
     * ViewModel bind data with particular item in recylcerView
     */
    public static class CityViewModel {
        PublishRelay<AddViewModel.CityClickEvent> cityRelay;

        public City city;

        public ObservableBoolean checkObservable = new ObservableBoolean(false);

        public CityViewModel(PublishRelay<AddViewModel.CityClickEvent> cityRelay, City city) {
            this.cityRelay = cityRelay;
            this.city = city;
        }

        public void click() {
            checkObservable.set(!checkObservable.get());
            CityClickEvent cce = new AddViewModel.CityClickEvent(city, checkObservable.get());
            cityRelay.accept(cce);
        }
    }

    /**
     * Using to notify view, that searching end up with success
     */
    interface SuccessEventCallback {

        /**
         * When user has modified data
         */
        void success();

        /**
         * When user has not modified data
         */
        void noDataModified();
    }
}
