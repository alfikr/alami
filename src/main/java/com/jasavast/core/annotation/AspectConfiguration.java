package com.jasavast.core.annotation;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;

@Configuration
@EnableAspectJAutoProxy
@Aspect
public class AspectConfiguration {

    @Autowired
    private R2dbcEntityTemplate r2dbcEntityTemplate;

    @Around("@annotation(com.jasavast.core.annotation.GetExecution) || " +
            "@annotation(com.jasavast.core.annotation.PostExecution ) ||" +
            "@annotation(com.jasavast.core.annotation.PutExecution) || " +
            "@annotation(com.jasavast.core.annotation.DeleteExecution)")
    public Object proses(ProceedingJoinPoint joinPoint) throws Throwable{
        long start = System.currentTimeMillis();
        try {
            Object o = joinPoint.proceed();
            long end = System.currentTimeMillis();
//            ReactiveSecurityContextHolder.getContext().
//                    flatMap(securityContext -> r2dbcEntityTemplate.insert(AuditEventModel.builder()
//                    .logType("log")
//                    .executionTime((end-start))
//                    .executionStart(start)
//                    .executionEnd(end)
//                    .className(joinPoint.getSignature().getDeclaringTypeName())
//                    .methodName(joinPoint.getSignature().getName())
//                    .message(String.format("request {}",joinPoint.getArgs()))
//                    .title("executed method")
//                            .principal(securityContext.getAuthentication()!=null? securityContext.getAuthentication()
//                                    .getPrincipal().toString():null)
//                    .build())).subscribe();

            return o;
        }catch (Throwable e){
            long end = System.currentTimeMillis();
            int line =e.getStackTrace().length>0?e.getStackTrace()[0].getLineNumber():-1;
//            ReactiveSecurityContextHolder.getContext().subscribe(securityContext -> r2dbcEntityTemplate.insert(AuditEventModel.builder()
//                    .error(true)
//                    .logType("log")
//                    .executionTime((end-start))
//                    .executionStart(start)
//                    .executionEnd(end)
//                    .className(joinPoint.getSignature().getDeclaringTypeName())
//                    .methodName(joinPoint.getSignature().getName())
//                    .errorMessage(e.getMessage())
//                    .message(String.format("request {} error on line {}",joinPoint.getArgs(),line))
//                    .title("executed method")
//                    .principal(securityContext.getAuthentication()!=null? securityContext.getAuthentication()
//                            .getPrincipal().toString():null)
//                    .errorMessage(e.getMessage())
//                    .build()));
            throw e;
        }
    }
}
