package com.seckill.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;
@SpringBootApplication(scanBasePackages = "com.seckill")
@MapperScan("com.seckill.infrastructure")
public class SeckillApplication { public static void main(String[] args) { SpringApplication.run(SeckillApplication.class, args); } }
