package com.jasavast.core.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.CaseUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.binding.Bindings;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
@Component
@Slf4j
public class DBUtils {
    @Autowired
    private DatabaseClient databaseClient;
    @Autowired
    private ObjectMapper objectMapper;

    public Mono<JSONObject> executeQuery(String query, List<SqlParam> map){
        DatabaseClient.GenericExecuteSpec spec=databaseClient.sql(query);
        for (SqlParam param:map){
            if (param.getValue()==null || param.getValue().toString().isEmpty()){
                spec=spec.bindNull(param.getKey(),param.getAClass());
            }else {
                spec=spec.bind(param.getKey(),param.getValue());
            }
        }
        return spec.fetch()
                .first()
                .map(stringObjectMap -> {
                    JSONObject res = new JSONObject();
                    for(String s:stringObjectMap.keySet()){
                        res.put(CaseUtils.toCamelCase(s,false,'_'),stringObjectMap.get(s));
                    }
                    log.info("result query {}",res);
                    return res;
                });

    }
    public Flux<JSONObject> executeFluxQuery(String query, List<SqlParam> map){
        DatabaseClient.GenericExecuteSpec spec=databaseClient.sql(query);
        for (SqlParam param:map){
            if (param.getValue()==null || param.getValue().toString().isEmpty()){
                spec=spec.bindNull(param.getKey(),param.getAClass());
            }else {
                spec=spec.bind(param.getKey(),param.getValue());
            }
        }
        return spec.fetch()
                .all()
                .publishOn(Schedulers.boundedElastic())
                .map(stringObjectMap -> {
                    JSONObject res = new JSONObject();
                    for(String s:stringObjectMap.keySet()){
                        res.put(CaseUtils.toCamelCase(s,false,'_'),stringObjectMap.get(s));
                    }
                    log.info("result query {}",res);
                    return res;
                });
    }
}
