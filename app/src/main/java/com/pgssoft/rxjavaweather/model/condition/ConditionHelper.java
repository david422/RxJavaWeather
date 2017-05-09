package com.pgssoft.rxjavaweather.model.condition;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

import io.reactivex.Single;

/**
 * Created by dpodolak on 04.05.2017.
 */

public class ConditionHelper {

    private ConditionDao conditionDao;

    public ConditionHelper(ConditionDao conditionDao) {
        this.conditionDao = conditionDao;
    }

    public Single<Condition> insertOrUpdateCondition(Condition condition) {
        return Single.create(e -> {
            WhereCondition wh = ConditionDao.Properties.CityId.eq(condition.getCityId());

            //The condition from response doesn't have a daoSession context, so we have query for condition manually
            List<Condition> localConditions = conditionDao.queryBuilder().where(wh).list();

            if (localConditions.isEmpty()) {
                long id = conditionDao.insert(condition);
                condition.setId(id);
                e.onSuccess(condition);
            } else if (localConditions.size() == 1) {
                Condition localCondition = localConditions.get(0);
                localCondition.setIcon(condition.getIcon());
                localCondition.setObservationTime(condition.getObservationTime());
                localCondition.setPressure(condition.getPressure());
                localCondition.setTempC(condition.getTempC());
                localCondition.setWeather(condition.getWeather());
                localCondition.setVisibilityDistance(condition.getVisibilityDistance());
                localCondition.setWindDirection(condition.getWindDirection());
                localCondition.setWindSpeed(condition.getWindSpeed());
                localCondition.setWindString(condition.getWindString());
                conditionDao.update(localCondition);
                e.onSuccess(localCondition);
            }
        });
    }
}
