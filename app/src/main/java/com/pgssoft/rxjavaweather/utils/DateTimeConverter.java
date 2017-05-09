package com.pgssoft.rxjavaweather.utils;

import org.greenrobot.greendao.converter.PropertyConverter;
import org.joda.time.DateTime;

public class DateTimeConverter implements PropertyConverter<DateTime, String> {

        @Override
        public DateTime convertToEntityProperty(String databaseValue) {
            return new DateTime(databaseValue);
        }

        @Override
        public String convertToDatabaseValue(DateTime entityProperty) {
            return entityProperty.toString();
        }
    }