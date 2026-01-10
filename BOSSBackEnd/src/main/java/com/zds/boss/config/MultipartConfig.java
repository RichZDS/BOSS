package com.zds.boss.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import jakarta.servlet.MultipartConfigElement;

@Configuration
@Slf4j
public class MultipartConfig {

    /**
     * 配置 multipart 解析器
     * 解决 "no multipart boundary was found" 错误
     * 延迟解析可以避免在请求处理前就尝试解析，从而避免 boundary 错误
     */
    @Bean(name = "multipartResolver")
    public StandardServletMultipartResolver multipartResolver() {
        StandardServletMultipartResolver resolver = new StandardServletMultipartResolver();
        resolver.setResolveLazily(true); // 延迟解析，避免在请求处理前就解析
        log.info("配置 StandardServletMultipartResolver，启用延迟解析");
        return resolver;
    }

    /**
     * 配置 multipart 文件上传参数
     * 这个配置会被 Spring Boot 自动应用
     */
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        // 单个文件最大大小
        factory.setMaxFileSize(DataSize.ofMegabytes(10));
        // 总请求大小
        factory.setMaxRequestSize(DataSize.ofMegabytes(10));
        // 文件写入磁盘的阈值（超过此大小会写入临时文件）
        factory.setFileSizeThreshold(DataSize.ofMegabytes(1));
        
        MultipartConfigElement config = factory.createMultipartConfig();
        log.info("配置 MultipartConfigElement，最大文件大小: 10MB，最大请求大小: 10MB，文件大小阈值: 1MB");
        return config;
    }
}

