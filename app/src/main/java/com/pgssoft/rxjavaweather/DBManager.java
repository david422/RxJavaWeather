package com.pgssoft.rxjavaweather;

import android.content.Context;

import com.pgssoft.rxjavaweather.model.city.CityHelper;
import com.pgssoft.rxjavaweather.model.city.DaoMaster;
import com.pgssoft.rxjavaweather.model.city.DaoSession;
import com.pgssoft.rxjavaweather.model.condition.ConditionHelper;

import org.greenrobot.greendao.database.Database;

/**
 * Created by dpodolak on 04.05.2017.
 */

public class DBManager {

    private static final String DB_NAME = "weather.db";

    private DaoSession daoSession;

    private CityHelper cityHelper;

    private ConditionHelper conditionHelper;

    public DBManager(Context context) {
        DaoMaster.DevOpenHelper dbHelper = new DaoMaster.DevOpenHelper(context, "weather.db");
        Database db = dbHelper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
    }

    public CityHelper getCityHelper(){
        if (cityHelper == null){
            cityHelper = new CityHelper(daoSession.getCityDao());
        }

        return cityHelper;
    }

    public ConditionHelper getConditionHelper(){
        if (conditionHelper == null){
            conditionHelper = new ConditionHelper(daoSession.getConditionDao());
        }

        return conditionHelper;
    }
}
