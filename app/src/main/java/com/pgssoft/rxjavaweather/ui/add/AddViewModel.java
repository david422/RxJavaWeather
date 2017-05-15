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

import io.reactivex.CompletableObserver;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by dpodolak on 13.04.2017.
 */

public class AddViewModel implements SearchViewModel {

    public static final int CLICK_DEBOUNCE_TIME = 500;

    private ObservableBoolean recyclerviewVisible = new ObservableBoolean(false);

    private PublishRelay<ProgressEvent> progressRelay = PublishRelay.create();

    private PublishRelay<List<CityViewModel>> citiesRelay = PublishRelay.create();

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private ObservableField<String> searchObservable = new ObservableField<>();

    private PublishRelay<AddViewModel.CityClickEvent> cityClickRelay = PublishRelay.create();

    private SuccessEventCallback successEvent;

    private boolean isDataModified;

    private DBManager dbManager;

    public AddViewModel(Api api, DBManager dbManager) {

        this.dbManager = dbManager;
        Flowable<City> localCityObserable = dbManager.getCityHelper().getCities();

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
                    if (!(e instanceof TimeoutException)) {
                        e.printStackTrace();
                    }
                    progressRelay.accept(new ProgressEvent(ProgressEvent.CLOSE));
                    progressRelay.accept(new ProgressEvent(ProgressEvent.ERROR, R.string.error_occured_during_search));
                })
                .retry() //if occur any error, subscribe observable again
                .subscribe(new Observer<List<CityViewModel>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(List<CityViewModel> s) {
                        recyclerviewVisible.set(!s.isEmpty());
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

        observeCityClick();
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

    public ObservableBoolean getRecyclerviewVisible() {
        return recyclerviewVisible;
    }

    public Observable<List<CityViewModel>> getCitiesObservable() {
        return citiesRelay;
    }

    public SearchViewModel getSearchViewModel() {
        return this;
    }

    public void observeCityClick() {
        cityClickRelay
                .observeOn(Schedulers.io())
                .flatMapCompletable(event -> {
                    isDataModified = true;
                    if (event.isSelected) {
                        return dbManager.getCityHelper().insert(event.city);
                    } else {
                        return dbManager.getCityHelper().remove(event.city);
                    }
                })
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onComplete() {
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();
                    }
                });
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

    interface SuccessEventCallback {
        void success();

        void noDataModified();
    }
}
