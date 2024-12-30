package com.xuan.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Knife4jConfig {
 
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                // 配置接口文档基本信息
                .info(this.getApiInfo())
                ;
    }
 
    private Info getApiInfo() {
        return new Info()
                // 配置文档标题
                .title("AI智能刷题平台接口文档")
                // 配置文档描述
                .description("AI智能刷题平台接口文档")
                // 概述信息
                .summary("AI智能刷题平台接口文档")
                // 配置版本号
                .version("2.0");
    }
}