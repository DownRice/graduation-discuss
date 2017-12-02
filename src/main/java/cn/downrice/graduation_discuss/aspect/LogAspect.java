package cn.downrice.graduation_discuss.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LogAspect {
    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);

    @Before("execution(* cn.downrice.graduation_discuss.controller.*.*(..))")
    public void beforeMethod(JoinPoint joinPoint){

        logger.info("beforeMethod:");
    }
}
