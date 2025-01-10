package com.jusfan.transaction.management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * @author jiasifan
 * Created on 2025-01-10
 */
@SpringBootApplication
@EnableCaching
public class BankTransactionSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankTransactionSystemApplication.class, args);
    }
}