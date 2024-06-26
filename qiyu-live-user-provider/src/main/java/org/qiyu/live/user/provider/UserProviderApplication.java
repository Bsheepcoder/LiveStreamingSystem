package org.qiyu.live.user.provider;


import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 *
 * @Description 用户中台dubbo服务提供者
 */
@SpringBootApplication
@EnableDubbo
@EnableDiscoveryClient
public class UserProviderApplication {
    public static void main(String[] args) {
        SpringApplication springBootApplication = new SpringApplication(UserProviderApplication.class);
        springBootApplication.setWebApplicationType(WebApplicationType.NONE);  //创建一个不涉及 Web 应用程序的 Spring Boot 应用程序
        springBootApplication.run(args);
    }
}
