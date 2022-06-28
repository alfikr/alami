package com.jasavast.resource;

import com.jasavast.core.security.filter.TokenProvider;
import com.jasavast.resource.vm.LoginVM;
import com.jasavast.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.Map;

@RestController()
@RequestMapping("/api")
@Slf4j
public class AuthenticationResource {
    @Autowired
    TokenProvider tokenProvider;
    @Autowired
    private AccountService accountService;


    @PostMapping("/auth")
    public Mono<JSONObject> getAuth(@Valid @RequestBody LoginVM data){
        return accountService.createToken(data).doOnError(e->{
            log.error("error return object {}",e);
        });
    }
}
