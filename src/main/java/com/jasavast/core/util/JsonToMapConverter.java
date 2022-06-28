package com.jasavast.core.util;

import org.json.JSONObject;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

import java.util.Map;

@WritingConverter
public class JsonToMapConverter implements Converter<JSONObject, Map<String,Object>> {
    @Override
    public Map<String, Object> convert(JSONObject source) {
        return source.toMap();
    }
}
