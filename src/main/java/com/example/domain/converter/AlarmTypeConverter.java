package com.example.domain.converter;

import com.example.constant.AlarmType;

import javax.persistence.AttributeConverter;

public class AlarmTypeConverter implements AttributeConverter<AlarmType, String> {
    @Override
    public String convertToDatabaseColumn(AlarmType attribute) {
        return attribute.name();
    }

    @Override
    public AlarmType convertToEntityAttribute(String dbData) {
        return AlarmType.valueOf(dbData);
    }
}
