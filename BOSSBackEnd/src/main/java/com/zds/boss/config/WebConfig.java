package com.zds.boss.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;

/**
 * Web配置类
 * 配置静态资源访问
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.storage.path:src/main/resources/static}")
    private String fileStoragePath;

    @Autowired
    private ResourceLoader resourceLoader;

    private String staticResourcePath;

    /**
     * 初始化静态资源路径
     */
    @PostConstruct
    public void init() {
        try {
            // 尝试获取classpath下的static目录
            Resource resource = resourceLoader.getResource("classpath:/static/");
            try {
                File staticDir = resource.getFile();
                staticResourcePath = staticDir.getAbsolutePath();
            } catch (IOException e) {
                // 如果在jar包中，使用项目根目录
                String projectRoot = System.getProperty("user.dir");
                File staticDir = new File(projectRoot, "src/main/resources/static");
                if (!staticDir.exists()) {
                    staticDir = new File(projectRoot, "target/classes/static");
                }
                staticResourcePath = staticDir.getAbsolutePath();
            }
        } catch (Exception e) {
            // 降级方案：使用项目根目录
            String projectRoot = System.getProperty("user.dir");
            staticResourcePath = new File(projectRoot, "src/main/resources/static").getAbsolutePath();
        }
    }

    /**
     * 配置静态资源访问
     * 使 src/main/resources/static 目录下的文件可以通过 HTTP 访问
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置 /static/** 映射到 src/main/resources/static 目录
        // 优先使用文件系统路径，如果不存在则使用classpath
        if (staticResourcePath != null) {
            String absolutePath = staticResourcePath.replace("\\", "/");
            if (!absolutePath.endsWith("/")) {
                absolutePath += "/";
            }
            registry.addResourceHandler("/static/**")
                    .addResourceLocations("file:" + absolutePath)
                    .setCachePeriod(3600); // 缓存1小时
        }
        
        // 同时支持 classpath 下的 static 目录（作为备用）
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(3600);
    }
}

