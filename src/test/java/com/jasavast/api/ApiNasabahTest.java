package com.jasavast.api;

import com.jasavast.api.user.ApiNasabah;
import com.jasavast.core.util.DBUtils;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class ApiNasabahTest {
    @Mock
    private ApiNasabah apiNasabah;

    @Mock
    private DBUtils dbUtils;
    private JSONObject getNasabah(){
        JSONObject reqData = new JSONObject();
        reqData.put("firstName","test");
        reqData.put("lastName","test last name");
        reqData.put("tanggalLahir", LocalDate.parse("1999-10-29"));
        reqData.put("alamat","alamat test");
        return reqData;
    }

//    @Test
//    public void createNasabah_success(){
//        JSONObject reqData = new JSONObject();
//        reqData.put("firstName","test");
//        reqData.put("lastName","test last name");
//        reqData.put("tanggalLahir", LocalDate.parse("1999-10-29"));
//        reqData.put("alamat","alamat test");
//        JSONObject p=new JSONObject();
//        p.put("method","apiNasabah.createNasabah");
//        p.put("data",reqData);
//        apiNasabah.init(p);
//        Mockito.when(dbUtils.executeQuery(Mockito.anyString(),Mockito.anyList())).thenReturn(Mono.just(reqData));
//        apiNasabah.createNasabah().subscribe(map -> {
//            log.info("result {}",map);
//        });
//    }
}
