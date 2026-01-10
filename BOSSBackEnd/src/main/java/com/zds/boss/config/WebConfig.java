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
        String projectRoot = System.getProperty("user.dir");
        File configuredDir = new File(fileStoragePath);
        if (!configuredDir.isAbsolute()) {
            configuredDir = new File(projectRoot, fileStoragePath);
        }

        if (configuredDir.exists()) {
            staticResourcePath = configuredDir.getAbsolutePath();
            return;
        }

        try {
            Resource resource = resourceLoader.getResource("classpath:/static/");
            File staticDir = resource.getFile();
            staticResourcePath = staticDir.getAbsolutePath();
        } catch (IOException e) {
            File targetStaticDir = new File(projectRoot, "target/classes/static");
            if (targetStaticDir.exists()) {
                staticResourcePath = targetStaticDir.getAbsolutePath();
            } else {
                staticResourcePath = new File(projectRoot, "src/main/resources/static").getAbsolutePath();
            }
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
