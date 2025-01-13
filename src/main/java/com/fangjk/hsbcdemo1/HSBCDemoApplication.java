/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.fangjk.hsbcdemo1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.retry.annotation.EnableRetry;

/**
 *
 * @author fangj
 */
@SpringBootApplication
@EnableRetry
@EnableCaching
public class HSBCDemoApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(HSBCDemoApplication.class, args);
    }
}
