package com.jasavast.service;

import com.jasavast.core.error.UserForgotReachLimitException;
import com.jasavast.core.error.UserNotActivatedException;
import com.jasavast.core.security.filter.TokenProvider;
import com.jasavast.core.util.DBUtils;
import com.jasavast.core.util.SqlParam;
import com.jasavast.resource.vm.ForgotVM;
import com.jasavast.resource.vm.LoginVM;
import com.jasavast.resource.vm.UserVM;
import com.jasavast.service.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.EmailValidator;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.Callable;

@Service
@Slf4j
public class AccountService {

    @Autowired
    private DBUtils dbUtils;
    @Autowired
    private TokenProvider tokenProvider;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ReactiveAuthenticationManager reactiveAuthenticationManager;

    public Mono<JSONObject> register(UserVM userVM, String password) {
        return Mono.empty();
    }
//
//    public Mono<JSONObject> forgotPassword(ForgotVM userDto) {
//        return r2dbcEntityTemplate.selectOne(Query.query(Criteria.from()),JSONObject.class);
//        return Mono.fromCallable(getUser(userDto.getEmail()))
//                .map(u->{
//                    if(u==null){
//                        throw new UsernameNotFoundException("user tidak ditemukan");
//                    }
//                    if(!u.isAktif()){
//                        throw new UserNotActivatedException("email "+ u.getEmail()+" not activated");
//                    }
//                    LocalDateTime ne=LocalDateTime.now().plus(1, ChronoUnit.DAYS);
//                    if(u.getForgotPasswordCount()>=3 && u.getLastForgot().isBefore(ne)){
//                        throw new UserForgotReachLimitException("User forgot telah melampaui batas forgot. tunggu 60 menit");
//                    }
//                    return u;
//                })
//                .flatMap(u->{
//                    u.setLastForgot(LocalDateTime.now());
//                    u.setForgotPasswordCount(u.getForgotPasswordCount()+1);
//                    return Mono.fromCallable(() -> userRepository.save(u));
//                })
//                .doOnSuccess(user -> {
//                    //kirim email jika ada email providernya
//
//                })
//                .map(u->{
//                    JSONObject object=new JSONObject();
//                    object.put("status","sukses");
//                    return object;
//                });
//    }
//
//    public Mono<JSONObject> updateProfile(UserDTO userDTO){
//        return Mono.empty();
//    }
//
    public Mono<JSONObject> createToken(LoginVM loginVM){
        Map param= new HashMap();
        param.put("login",loginVM.getLogin());
        return reactiveAuthenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginVM.getLogin(),loginVM.getPassword()))
                        .map(authentication -> tokenProvider.createToken(authentication, loginVM.isRememberMe()))
                        .doOnNext(s -> {
                            List<SqlParam> sqlParams= new ArrayList<>();
                            sqlParams.add(new SqlParam("login",loginVM.getLogin(),String.class));
                            String updateData="update jvs_user set last_login = now() where login =:login";
                            dbUtils.executeQuery(updateData,sqlParams).subscribe();
                        })
                        .map(s->{
                            JSONObject object= new JSONObject();
                            object.put("success",true);
                            object.put("token",s);
                            log.info("result request {}",object);
                            return object;
                        })
                                .doOnError(err->{
                                    log.error("error create token ",err);
                                });
    }
//    private Callable<User> getUser(String s){
//        return new Callable<User>() {
//            @Override
//            public User call() throws Exception {
//                Optional<User> p = null;
//                if (EmailValidator.getInstance().isValid(s)){
//                    p=userRepository.findOneByEmail(s);
//                }else {
//                    p=userRepository.findOneByLogin(s);
//                }
//                return p.orElseThrow(RuntimeException::new);
//            }
//        };
//    }
}
