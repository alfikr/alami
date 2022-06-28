package com.jasavast.core.security;


import com.jasavast.core.error.UserNotActivatedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component("userDetailsService")
@Slf4j
public class SecurityUserService implements ReactiveUserDetailsService {
    @Autowired
    private DatabaseClient databaseClient;
    @Override
    public Mono<UserDetails> findByUsername(String s) {
        return databaseClient.sql("select * from public.jvs_user ju where login = :login limit 1")
                .bind("login",s)
                .fetch()
                .first()
                .map(m->{
                    if (Optional.ofNullable(m.get("aktif")).isPresent() && !m.get("aktif").equals(true)){
                        throw new UserNotActivatedException("user "+ s+ " belum melakukan aktivasi");
                    }
                    return m;
                })
                .flatMap(u->{
                    if (u==null) throw new UsernameNotFoundException("Username "+s+ " not found");
                    String sql="select a.* from authority a left join user_authority ua on a.\"name\" =ua.auth where ua.user_id = :id";
                    return databaseClient.sql(sql).bind("id",u.get("id"))
                            .fetch().all().collectList().zipWith(Mono.just(u));
                })
                .map(tupple->{
                    System.out.println("tupple disini");
                    log.info("result granted auth {}",tupple);
                    List<GrantedAuthority> grantedAuthorities=new ArrayList<>();
                    tupple.getT1().forEach((m)->{
                        grantedAuthorities.add(new SimpleGrantedAuthority(m.get("name").toString()));
                    });

                    return new User(tupple.getT2().get("login").toString(),tupple.getT2().get("password").toString(),grantedAuthorities);
                });
    }

}
