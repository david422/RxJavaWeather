package com.pgssoft.rxjavaweather.model.city;

import com.google.gson.annotations.SerializedName;
import com.pgssoft.rxjavaweather.model.condition.Condition;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.DaoException;
import com.pgssoft.rxjavaweather.model.condition.ConditionDao;
import org.greenrobot.greendao.annotation.NotNull;

/**
 * Created by dpodolak on 13.04.2017.
 */
@Entity
public class City {

    @Id(autoincrement = true)
    private Long id;

    private String name;

    @SerializedName("tz")
    private String country;

    @SerializedName("zmw")
    private String fullPath;

    @SerializedName("ll")
    private String latlong;

    private Long conditionId;
    
    @ToOne(joinProperty = "conditionId")
    private Condition condition;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 448079911)
    private transient CityDao myDao;


    @Generated(hash = 311203842)
    public City(Long id, String name, String country, String fullPath, String latlong,
            Long conditionId) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.fullPath = fullPath;
        this.latlong = latlong;
        this.conditionId = conditionId;
    }

    @Generated(hash = 750791287)
    public City() {
    }

    @Generated(hash = 1311814051)
    private transient Long condition__resolvedKey;


    @Keep
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        City city = (City) o;

        if (name != null ? !name.equals(city.name) : city.name != null) return false;
        if (country != null ? !country.equals(city.country) : city.country != null) return false;
        return latlong != null ? latlong.equals(city.latlong) : city.latlong == null;

    }

    @Keep
    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (country != null ? country.hashCode() : 0);
        result = 31 * result + (latlong != null ? latlong.hashCode() : 0);
        return result;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return this.country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getFullPath() {
        return this.fullPath;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

    public String getLatlong() {
        return this.latlong;
    }

    public void setLatlong(String latlong) {
        this.latlong = latlong;
    }

    public Long getConditionId() {
        return this.conditionId;
    }

    public void setConditionId(long conditionId) {
        this.conditionId = conditionId;
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1656147593)
    public Condition getCondition() {
        Long __key = this.conditionId;
        if (condition__resolvedKey == null || !condition__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ConditionDao targetDao = daoSession.getConditionDao();
            Condition conditionNew = targetDao.load(__key);
            synchronized (this) {
                condition = conditionNew;
                condition__resolvedKey = __key;
            }
        }
        return condition;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1444414285)
    public void setCondition(Condition condition) {
        synchronized (this) {
            this.condition = condition;
            conditionId = condition == null ? null : condition.getId();
            condition__resolvedKey = conditionId;
        }
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    public void setConditionId(Long conditionId) {
        this.conditionId = conditionId;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 293508440)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getCityDao() : null;
    }

}
