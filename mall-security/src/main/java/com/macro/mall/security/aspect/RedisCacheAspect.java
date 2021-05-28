package com.macro.mall.security.aspect;

import com.macro.mall.security.annotation.CacheException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * Redis缓存切面，防止Redis宕机影响正常业务逻辑
 * Created by macro on 2020/3/17.
 *
 * 由于增加了缓存，把用户信息保存到了redis中，这样每次访问接口的时候就不用每次都要访问数据库，
 *  * 但是问题是，如果redis宕机了，就会影响登录功能。因此要加一个aop处理redis的宕机异常（其实就是捕获日志）。
 */
@Aspect
@Component
@Order(2)
public class RedisCacheAspect {
    private static Logger LOGGER = LoggerFactory.getLogger(RedisCacheAspect.class);

    /**
     * 切面
     */
    @Pointcut("execution(public * com.macro.mall.portal.service.*CacheService.*(..)) || execution(public * com.macro.mall.service.*CacheService.*(..))")
    public void cacheAspect() {
    }

    @Around("cacheAspect()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        // 获得切面的方法
        Method method = methodSignature.getMethod();
        Object result = null;
        try {
            // 执行方法
            result = joinPoint.proceed();
        } catch (Throwable throwable) {
            // 捕捉到异常后，分不同情况进行处理，是直接抛出异常，提示前端，还是只是记录日志
            if (method.isAnnotationPresent(CacheException.class)) {
                // 1、加上@CacheException注解的，就直接抛出异常，这样就暂停运行，使用全局异常处理，直接提示用户不能执行 --- 注解应用到存储和获取验证码的方法上去
                throw throwable;
            } else {
                // 2、没有加该注解的，就记录日志，并不抛出异常，这样就可以正常运行
                LOGGER.error(throwable.getMessage());
            }
        }
        return result;
    }

}
