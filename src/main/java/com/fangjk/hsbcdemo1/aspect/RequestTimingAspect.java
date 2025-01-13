/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fangjk.hsbcdemo1.aspect;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 *
 * @author fangj
 */
@Component
public class RequestTimingAspect {
    
    // 定义切点，拦截service方法
    @Pointcut("execution(* com.fangjk.hsbcdemo1.transaction.controller.*.*(..))")
    public void methods() {}
    
    // 在方法执行前记录开始时间
    @Before("methods()")
    public void beforeMethod() {
        long startTime = System.currentTimeMillis();
        RequestContextHolder.getRequestAttributes().setAttribute("startTime", startTime, RequestAttributes.SCOPE_REQUEST);
    }
    
    // 在方法执行后计算消耗时间
    @After("methods()")
    public void afterMethod() {
        long startTime = (Long) RequestContextHolder.getRequestAttributes().getAttribute("startTime", RequestAttributes.SCOPE_REQUEST);
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        System.out.println(Thread.currentThread().getName() + ": Request processing time: " + duration + " ms");
    }
    
}
