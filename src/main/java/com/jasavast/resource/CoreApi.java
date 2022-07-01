package com.jasavast.resource;

import com.jasavast.core.annotation.GetExecution;
import com.jasavast.core.annotation.PostExecution;
import com.jasavast.core.annotation.PutExecution;
import com.jasavast.core.error.MethodNotAllowedException;
import com.jasavast.core.error.MethodNotFoundException;
import com.jasavast.resource.vm.ReqVM;
import com.jasavast.service.ApiAbstract;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.context.ApplicationContext;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.lang.reflect.Method;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api")
public class CoreApi {
    private final ApplicationContext context;
    @GetMapping("/request")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            allowEmptyValue = false, paramType = "header", dataTypeClass = String.class,
            example = "Bearer access_token")

    public Mono<JSONObject> getMethod(@ApiParam @RequestParam String method, @ApiParam @RequestParam(required = false) String data, ServerHttpRequest request) throws Exception {
        Map map= request.getQueryParams().toSingleValueMap();
        JSONObject req = new JSONObject(map);
        log.info("request param {}",req);
        if(!req.has("method")){
            throw new MethodNotFoundException();
        }
        String [] method2 = req.getString("method").split("\\.");
        if(method2.length!=2){
            throw new MethodNotFoundException();
        }
        Object bean = context.getBean(method2[0]);
        if (bean==null){
            throw new MethodNotFoundException();
        }
        if (ApiAbstract.class.isAssignableFrom(bean.getClass())){
            ((ApiAbstract) bean).init(req);
        }
        log.info("class api {}",bean.getClass().getName());
        Class cl = bean.getClass().getSuperclass();
        Method m =  cl.getMethod(method2[1]);
        log.info("support http method get  {}",
                m.isAnnotationPresent(GetExecution.class));
        if(!m.isAnnotationPresent(GetExecution.class)){
            throw new MethodNotAllowedException();
        }
        return (Mono<JSONObject>)m.invoke(bean);
    }
    @PostMapping("/request")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            allowEmptyValue = false, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Mono<JSONObject> postMethod(@RequestBody @Valid ReqVM req) throws Exception{
        log.info("user request {}",req);
        String[] methods = req.getMethod().split("\\.");
        if(methods.length!=2){
            throw new MethodNotFoundException();
        }
        String beanName = methods[0];
        log.info("bean name {}",beanName);
        Object bean = context.getBean(beanName);
        if (bean==null){
            log.info("bean {} is null",beanName);
            throw new MethodNotFoundException();
        }
        if (ApiAbstract.class.isAssignableFrom(bean.getClass())){
            log.info("start invoke method");
            ((ApiAbstract) bean).init(req.toJson());
        }
        log.info("class api {} method api {}",bean.getClass().getName(),methods[1]);
        Class cl = bean.getClass().getSuperclass();
        Method m = cl.getMethod(methods[1]);
        if(!m.isAnnotationPresent(PostExecution.class)){
            throw new MethodNotAllowedException();
        }
        return (Mono<JSONObject>) m.invoke(bean);
    }
    @PutMapping("/request")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, allowEmptyValue = false,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Mono<JSONObject> putMethod(@RequestParam(required = false) String community,@RequestBody @Valid ReqVM req) throws Exception{
        String[] methods = req.getMethod().split("\\.");
        if(methods.length!=2){
            throw new MethodNotFoundException();
        }
        String beanName = methods[0];
        Object bean = context.getBean(beanName);
        if (bean==null){
            log.info("bean {} is null",beanName);
            throw new MethodNotFoundException();
        }
        if (ApiAbstract.class.isAssignableFrom(bean.getClass())){
            log.info("start invoke method");
            ((ApiAbstract) bean).init(req.toJson());
        }
        log.info("class api {}",bean.getClass().getName());
        Class cl = bean.getClass().getSuperclass();
        Method m = cl.getMethod(methods[1]);
        if(!m.isAnnotationPresent(PutExecution.class)){
            throw new MethodNotAllowedException();
        }
        return (Mono<JSONObject>)m.invoke(bean);
    }
    @DeleteMapping("/request")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, allowEmptyValue = false, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Mono<JSONObject> deleteMethod(@RequestParam Map map) throws Exception {
        log.info("request param {}",map);
        JSONObject req = new JSONObject(map);
        if(!req.has("method")){
            throw new MethodNotFoundException();
        }
        String [] method = req.getString("method").split("\\.");
        if(method.length!=2){
            throw new MethodNotFoundException();
        }
        Object bean = context.getBean(method[0]);
        if (bean==null){
            throw new MethodNotFoundException();
        }
        if (ApiAbstract.class.isAssignableFrom(bean.getClass())){
            ((ApiAbstract) bean).init(req);
        }
        log.info("class api {}",bean.getClass().getName());
        Class cl = bean.getClass().getSuperclass();
        Method m = cl.getMethod(method[1]);
        log.info("support http method get  {}",
                m.isAnnotationPresent(GetExecution.class));
        if(!m.isAnnotationPresent(GetExecution.class)){
            throw new MethodNotAllowedException();
        }
        return (Mono<JSONObject>)m.invoke(bean);
    }
}