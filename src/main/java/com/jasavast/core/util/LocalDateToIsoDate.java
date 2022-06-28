package com.jasavast.core.util;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@ReadingConverter
public class LocalDateToIsoDate implements Converter<LocalDateTime,String> {
    @Override
    public String convert(LocalDateTime source) {
        return source.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }
}
