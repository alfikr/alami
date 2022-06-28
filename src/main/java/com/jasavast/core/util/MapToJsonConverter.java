package com.jasavast.core.util;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.util.Map;

@ReadingConverter
@Slf4j
public class MapToJsonConverter implements Converter<Map<String,Object>, JSONObject> {
    @Override
    public JSONObject convert(Map<String,Object> source) {
        return new JSONObject(source);
    }
}
