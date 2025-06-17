package com.example.imagedemo.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class loggingAspect {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Before("execution(* com.example.imagedemo.controller..*(..)) || execution(* com.example.imagedemo.impl..*(..))")
    public void logBefore(JoinPoint joinPoint) {
        logger.info("Started: " + joinPoint.getSignature());
    }

    @AfterReturning(pointcut = "execution(* com.example.imagedemo.controller..*(..)) || execution(* com.example.imagedemo.impl..*(..))", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        logger.info("Completed: " + joinPoint.getSignature() + " | Returned: " + result);
    }

    @AfterThrowing(pointcut = "execution(* com.example.imagedemo.controller..*(..)) || execution(* com.example.imagedemo.impl..*(..))", throwing = "error")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable error) {
        logger.error("Exception in: " + joinPoint.getSignature() + " | Error: " + error.getMessage());
    }
}
