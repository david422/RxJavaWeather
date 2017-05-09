package com.pgssoft.rxjavaweather.model.condition;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dpodolak on 28.04.2017.
 */

public class ConditionResponse {

    @SerializedName("current_observation")
    private Condition condition;

    public Condition getCondition() {
        return condition;
    }
}
