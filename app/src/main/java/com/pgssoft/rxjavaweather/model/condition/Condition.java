package com.pgssoft.rxjavaweather.model.condition;

import com.google.gson.annotations.SerializedName;
import com.pgssoft.rxjavaweather.utils.DateTimeConverter;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.joda.time.DateTime;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by dpodolak on 28.04.2017.
 */

@Entity()
public class Condition {

    @Id(autoincrement = true)
    private Long id;

    @SerializedName("observation_time_rfc822")
    @Convert(converter = DateTimeConverter.class, columnType = String.class)
    private DateTime observationTime;

    private String weather;

    @SerializedName("temp_c")
    private String tempC;

    @SerializedName("wind_string")
    private String windString;

    @SerializedName("wind_dir")
    private String windDirection;

    @SerializedName("wind_kph")
    private String windSpeed;

    @SerializedName("pressure_mb")
    private String pressure;

    @SerializedName("visibility_km")
    private String visibilityDistance;

    @SerializedName("icon_url")
    private String icon;

    private long cityId;

    @Generated(hash = 1429830174)
    public Condition(Long id, DateTime observationTime, String weather,
            String tempC, String windString, String windDirection, String windSpeed,
            String pressure, String visibilityDistance, String icon, long cityId) {
        this.id = id;
        this.observationTime = observationTime;
        this.weather = weather;
        this.tempC = tempC;
        this.windString = windString;
        this.windDirection = windDirection;
        this.windSpeed = windSpeed;
        this.pressure = pressure;
        this.visibilityDistance = visibilityDistance;
        this.icon = icon;
        this.cityId = cityId;
    }

    @Generated(hash = 1179462728)
    public Condition() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DateTime getObservationTime() {
        return this.observationTime;
    }

    public void setObservationTime(DateTime observationTime) {
        this.observationTime = observationTime;
    }

    public String getWeather() {
        return this.weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getTempC() {
        return this.tempC;
    }

    public void setTempC(String tempC) {
        this.tempC = tempC;
    }

    public String getWindString() {
        return this.windString;
    }

    public void setWindString(String windString) {
        this.windString = windString;
    }

    public String getWindDirection() {
        return this.windDirection;
    }

    public void setWindDirection(String windDirection) {
        this.windDirection = windDirection;
    }

    public String getWindSpeed() {
        return this.windSpeed;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }

    public String getPressure() {
        return this.pressure;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    public String getVisibilityDistance() {
        return this.visibilityDistance;
    }

    public void setVisibilityDistance(String visibilityDistance) {
        this.visibilityDistance = visibilityDistance;
    }

    public String getIcon() {
        return this.icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public long getCityId() {
        return this.cityId;
    }

    public void setCityId(long cityId) {
        this.cityId = cityId;
    }

}
