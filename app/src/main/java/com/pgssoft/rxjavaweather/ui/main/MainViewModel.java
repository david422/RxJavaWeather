package com.pgssoft.rxjavaweather.ui.main;

import android.databinding.ObservableBoolean;

import com.jakewharton.rxrelay2.PublishRelay;
import com.pgssoft.rxjavaweather.DBManager;
import com.pgssoft.rxjavaweather.api.Api;
import com.pgssoft.rxjavaweather.api.WeatherService;
import com.pgssoft.rxjavaweather.model.city.City;
import com.pgssoft.rxjavaweather.model.condition.Condition;
import com.pgssoft.rxjavaweather.model.condition.ConditionResponse;
import com.pgssoft.rxjavaweather.ui.OpenActivityEvent;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.SingleSource;
import io.reactivex.SingleTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by dpodolak on 13.04.2017.
 */

public class MainViewModel {

    public ObservableBoolean placeholderVisible = new ObservableBoolean(true);

    private PublishRelay<List<City>> citiesPublish = PublishRelay.create();

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private PublishRelay<OpenActivityEvent> openActivityRelay = PublishRelay.create();

    private WeatherService weatherService;

    private DBManager dbManager;

    private class NetworkResponse<T> {

    }

    SingleTransformer<ConditionResponse,Condition> networkTransformer = new SingleTransformer<ConditionResponse,Condition>() {
        @Override
        public SingleSource<Condition> apply(@NonNull Single<ConditionResponse> upstream) {
            return upstream.map(ConditionResponse::getCondition);
        }
    };


    public MainViewModel(Api api, DBManager dbManager) {

        this.dbManager = dbManager;
        weatherService = api.getWeatherService();
        updateAndShowWeather();


    }

    public void updateAndShowWeather() {


        dbManager.getCityHelper().getCities()
                .doOnNext(cl -> Timber.d("Thread: " + Thread.currentThread().getName()))
                .flatMapSingle(city ->
                        //get conditions for specific city, put it into the db and update city object
                        weatherService.getWeather(city.getFullPath())
                                .compose(networkTransformer)
                                .map(condition -> {
//                                    Condition condition = conditionResponse.getCondition();
                                    condition.setCityId(city.getId());
                                    return condition;
                                })
                                .flatMap(condition -> dbManager.getConditionHelper().insertOrUpdateCondition(condition))
                                .doOnSuccess(condition -> {
                                    if (city.getConditionId() == null) {
                                        city.setCondition(condition);
                                        city.update();
                                    } else {
                                        city.refresh();
                                    }
                                }).map(condition -> city)
                                .onErrorResumeNext(e -> Single.just(city)))
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<City>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(@NonNull List<City> cities) {

                        placeholderVisible.set(cities.isEmpty());
                        citiesPublish.accept(cities);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();
                    }
                });
    }

    public void addActivity() {
        openActivityRelay.accept(new OpenActivityEvent(OpenActivityEvent.AddActivity));
    }

    public void close() {
        compositeDisposable.clear();
    }

    public Observable<OpenActivityEvent> getOpenActivityObservable() {
        return openActivityRelay;
    }

    public Observable<List<City>> citiesObservable() {
        return citiesPublish;
    }
}
