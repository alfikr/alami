package com.jasavast.core.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SqlParam {
    private String key;
    private Object value;
    private Class aClass;
    public SqlParam(@NotNull String key,String val){
        this(key,val,String.class);
    }
    public SqlParam(@NotNull String key, @NotNull Object val){
        this(key,val,val.getClass());
    }
}
