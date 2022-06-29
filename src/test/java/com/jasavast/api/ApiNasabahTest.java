package com.jasavast.api;

import com.jasavast.api.user.ApiNasabah;
import com.jasavast.api.utils.ParamUtils;
import com.jasavast.core.util.DBUtils;
import com.jasavast.core.util.SqlParam;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Before;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class ApiNasabahTest {
    @Mock
    private ApiNasabah apiNasabah;

    @Mock
    private DBUtils dbUtils;

    @Mock
    ParamUtils paramUtils;

    @Mock
    private ReactiveSecurityContextHolder reactiveSecurityContextHolder;
    private JSONObject getNasabah(){
        JSONObject reqData = new JSONObject();
        reqData.put("firstName","test");
        reqData.put("lastName","test last name");
        reqData.put("tanggalLahir", LocalDate.parse("1999-10-29"));
        reqData.put("alamat","alamat test");
        return reqData;
    }
    @BeforeEach
    public void initialize(){
        MockitoAnnotations.openMocks(this);
        List<SqlParam> paramList= new ArrayList<>();
        paramList.add(new SqlParam("insOn", LocalDateTime.now()));
        paramList.add(new SqlParam("insBy","test"));
        paramList.add(new SqlParam("modOn",LocalDateTime.now()));
        paramList.add(new SqlParam("modBy","test"));
        SecurityContext ctx = new SecurityContext() {
            @Override
            public Authentication getAuthentication() {
                Authentication authentication=new UsernamePasswordAuthenticationToken("test","",new ArrayList<>());
                return null;
            }

            @Override
            public void setAuthentication(Authentication authentication) {

            }
        };
        Mockito.when(paramUtils.getMapParam())
                .thenReturn(Mono.just(paramList));
        Mockito.when(reactiveSecurityContextHolder.getContext()).thenReturn(Mono.just(ctx));
    }
//    @Test
//    public void testData(){
//        if (apiNasabah==null){
//            throw new IllegalStateException("nasabah api null");
//        }
//        apiNasabah.test().subscribe();
//    }
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
//        if (apiNasabah==null){
//            throw new IllegalStateException();
//        }
//        apiNasabah.createNasabah().block();
//    }
}
