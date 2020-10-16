package com.supwisdom.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication(scanBasePackages = { "com.supwisdom.platform.framework.config","com.supwisdom.platform" })
@EnableSwagger2
public class WechatSyncApp {
    public static void main(String[] args) {
        SpringApplication.run(WechatSyncApp.class, args);
    }
}
