package com.jasavast.api.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jasavast.api.user.dto.NasabahVM;
import com.jasavast.api.utils.ParamUtils;
import com.jasavast.core.annotation.GetExecution;
import com.jasavast.core.annotation.PostExecution;
import com.jasavast.core.error.InvalidParameterException;
import com.jasavast.core.util.DBUtils;
import com.jasavast.core.util.GenericValidator;
import com.jasavast.core.util.SqlParam;
import com.jasavast.service.ApiAbstract;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component("apiNasabah")
@Slf4j
public class ApiNasabah extends ApiAbstract {
    @Autowired
    private ParamUtils paramUtils;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GenericValidator validator;

    public ApiNasabah(DBUtils dbUtils) {
        super(dbUtils);
    }

    //
    @GetExecution
    public Mono<JSONObject> getNasabah(){
        return dbUtils.executeFluxQuery("select * from nasabah n",new ArrayList<>())
                .subscribeOn(Schedulers.boundedElastic())
                .collect(Collectors.toList())
                .map(data->{
                    JSONObject obj = new JSONObject();
                    obj.put("success",true);
                    obj.put("data",data);
                    return obj;
                });
    }
//
    @PostExecution
    public Mono<JSONObject> createNasabah(){
        log.info("start create nasabah");
        NasabahVM vm = objectMapper.convertValue(reqData,NasabahVM.class);
        BindingResult errors = new BeanPropertyBindingResult(vm,"NasabahVM");
        validator.validate(vm,errors);
        if (errors.hasErrors()){
            log.error("error validating ");
        }
        if (reqData.getString("firstName").length()<3){
            return Mono.error(new InvalidParameterException("firstName"));
        }
        log.info("validasi selesai");
        return paramUtils.getMapParam()
                .map(param->{
                    if (param==null){
                        log.info("param null");
                    }
                    log.info("param {}",param);
                    param.add(new SqlParam("firstName", Optional.ofNullable(reqData.getString("firstName")).orElse(""),String.class));
                    param.add(new SqlParam("lastName",Optional.ofNullable(reqData.has("lastName")?reqData.getString("lastName"):"").orElse(""),String.class));
                    param.add(new SqlParam("tanggalLahir",Optional.ofNullable(reqData.has("tanggalLahir")? reqData.getString("tanggalLahir").isEmpty()?null:
                            LocalDate.parse(reqData.getString("tanggalLahir")):null).orElse(null),LocalDate.class));
                    param.add(new SqlParam("nasabahStatus",Optional.ofNullable(reqData.has("status")?reqData.getString("status"):null).orElse("AKTIF")=="AKTIF"?0:1,Integer.class));
                    return param;
                })
                .flatMap(param->{
                    String sql="insert into nasabah (first_name,last_name,tanggal_lahir,nasabah_status,mod_on,mod_by,ins_on,ins_by) " +
                            "values(:firstName,:lastName,:tanggalLahir,:nasabahStatus, :modOn,:modBy,:insOn,:insBy) " +
                            "returning id,first_name ,last_name ,tanggal_lahir ,ins_on ,ins_by ,mod_on ,mod_by ,nasabah_status ";
                    return dbUtils.executeQuery(sql,param);
                })
                .doOnNext(nasabah->{
                    //insert alamat
                    List<SqlParam> p = new ArrayList<>();
                    p.add(new SqlParam("alamat",Optional.ofNullable(reqData.getString("alamat")).orElse("")));
                    p.add(new SqlParam("nasabahId",nasabah.getString("id")));
                    String s="insert into nasabah_address (address,\"defaults\",nasabah_id) values (:alamat,true,:nasabahId) returning id";
                    dbUtils.executeQuery(s,p).subscribe();
                })
                .map(user->{
                    log.info("insert success {}",user);
                    JSONObject map = new JSONObject();
                    map.put("success",true);
                    map.put("data",user);
                    return map;
                }).doOnError(err->{
                    log.error("error insert data",err);
                });
    }

    public Mono<JSONObject> test(){
        return paramUtils.getMapParam()
                .map(l->{
                    JSONObject object=new JSONObject();
                    object.put("data",object);
                    return object;
                });
    }

}
