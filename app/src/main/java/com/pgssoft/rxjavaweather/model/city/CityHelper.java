package com.pgssoft.rxjavaweather.model.city;


import android.database.sqlite.SQLiteException;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.Flowable;
/**
 * Created by dpodolak on 13.04.2017.
 */

public class CityHelper {

    private CityDao cityDao;


    public CityHelper(CityDao dao) {
        cityDao = dao;
    }

    public Completable insert(City city) {
        return Completable.create(e -> {
            cityDao.insert(city);
            e.onComplete();
        });
    }

    public Completable remove(City city) {
        return Completable.create(e -> {
            try {
                //In this place city could not have cityDao context (no id, e.g. from searching), so we have to do a query
                //prepare query to get proper city to remove

                WhereCondition nameCondition = CityDao.Properties.Name.eq(city.getName());
                WhereCondition countryCondition = CityDao.Properties.Country.eq(city.getCountry());
                List<City> cityToDelete = cityDao.queryBuilder().where(nameCondition, countryCondition).build().list();
                for (City c : cityToDelete) {
                    c.delete();
                }

            } catch (SQLiteException ex) {
                e.onError(ex);
            }
            e.onComplete();
        });
    }

    public Flowable<City> getCities() {
        return Flowable.create(e -> {
            List<City> allCities = cityDao.loadAll();
            for (City city : allCities) {
                e.onNext(city);
            }
            e.onComplete();
        }, BackpressureStrategy.BUFFER);
    }

}
