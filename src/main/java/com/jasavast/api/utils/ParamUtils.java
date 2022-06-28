package com.jasavast.api.utils;

import com.jasavast.core.util.SqlParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Component
@Slf4j
public class ParamUtils {
    public Mono<List<SqlParam>> getMapParam(){
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> {
                    List<SqlParam> sqlParams = new ArrayList<>();
                    sqlParams.add(new SqlParam("insOn",LocalDateTime.now(), LocalDateTime.class));
                    sqlParams.add(new SqlParam("insBy", Optional.ofNullable(securityContext.getAuthentication().getCredentials().toString()).orElse(null),String.class));
                    sqlParams.add(new SqlParam("modOn",LocalDateTime.now(),LocalDateTime.class));
                    sqlParams.add(new SqlParam("modBy",Optional.ofNullable(securityContext.getAuthentication().getCredentials().toString()).orElse(null),String.class));
                    return sqlParams;
                }).doOnError(err->{
                    log.error("error create param {}",err);
                });
    }
}
