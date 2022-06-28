package com.jasavast.resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
public class IndexResources {
    @GetMapping({"","/"})
    public Mono<Void> indexController(ServerHttpResponse response){
        response.setStatusCode(HttpStatus.PERMANENT_REDIRECT);
        response.getHeaders().setLocation(URI.create("/swagger-ui/index.html"));
        return response.setComplete();
    }
}
