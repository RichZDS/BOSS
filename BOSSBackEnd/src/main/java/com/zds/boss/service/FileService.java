package com.zds.boss.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 本地文件服务
 * 提供文件上传、删除等功能，文件存储在 src/main/resources/static 目录下
 */
@Service
@Slf4j
public class FileService {

    /**
     * 文件存储基础路径
     */
    private String basePath;

    /**
     * 文件存储路径（相对于项目根目录）
     */
    @Value("${file.storage.path:src/main/resources/static}")
    private String storagePath;

    /**
     * 文件访问基础URL（用于构建访问地址）
     */
    @Value("${server.port:8081}")
    private int serverPort;

    private final ResourceLoader resourceLoader;

    public FileService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    /**
     * 初始化方法，在Bean创建后执行
     */
    @PostConstruct
    public void init() {
        this.basePath = resolveBasePath();
        initStorageDirectory();
        log.info("文件存储路径已初始化: {}", basePath);
    }

    private String resolveBasePath() {
        String projectRoot = System.getProperty("user.dir");
        Path configuredPath = Paths.get(storagePath);
        Path resolvedPath = configuredPath.isAbsolute()
            ? configuredPath
            : Paths.get(projectRoot).resolve(configuredPath);

        if (Files.exists(resolvedPath)) {
            return resolvedPath.toAbsolutePath().toString();
        }

        try {
            Resource resource = resourceLoader.getResource("classpath:/static/");
            File staticDir = resource.getFile();
            if (staticDir.exists()) {
                return staticDir.getAbsolutePath();
            }
        } catch (IOException e) {
            log.warn("无法从classpath解析静态目录，尝试降级路径: {}", e.getMessage());
        }

        Path targetPath = Paths.get(projectRoot, "target/classes/static");
        if (Files.exists(targetPath)) {
            return targetPath.toAbsolutePath().toString();
        }

        return resolvedPath.toAbsolutePath().toString();
    }

    /**
     * 初始化存储目录
     */
    private void initStorageDirectory() {
        try {
            Path path = Paths.get(basePath);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                log.info("创建文件存储目录: {}", basePath);
            }
            log.info("文件存储目录已就绪: {}", basePath);
        } catch (IOException e) {
            log.error("创建文件存储目录失败: {}", basePath, e);
            throw new RuntimeException("文件存储目录初始化失败", e);
        }
    }

    /**
     * 构建文件的访问URL
     * 
     * @param relativePath 文件相对路径（相对于 basePath），例如：resume/123/Resume.pdf
     * @return 文件的访问URL（用于HTTP访问），例如：/static/resume/123/Resume.pdf
     */
    public String buildUrl(String relativePath) {
        // 移除开头的 /，确保路径正确
        String cleanPath = relativePath.startsWith("/") ? relativePath.substring(1) : relativePath;
        // 构建静态资源访问URL，映射到 /static/** 路径
        String url = String.format("/static/%s", cleanPath.replace("\\", "/"));
        log.debug("构建文件访问URL: {}", url);
        return url;
    }

    /**
     * 上传文件
     * 
     * @param relativePath 文件相对路径（相对于 basePath），例如：resume/123/Resume.pdf
     * @param file 要上传的文件
     * @throws IOException 文件操作异常
     */
    public void upload(String relativePath, File file) throws IOException {
        log.info("开始上传文件到本地存储，路径: {}", relativePath);
        
        try {
            // 构建完整路径
            Path targetPath = Paths.get(basePath, relativePath);
            
            // 确保父目录存在
            Path parentDir = targetPath.getParent();
            if (parentDir != null && !Files.exists(parentDir)) {
                Files.createDirectories(parentDir);
                log.debug("创建文件目录: {}", parentDir);
            }
            
            // 复制文件到目标位置
            Files.copy(file.toPath(), targetPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            
            log.info("文件成功上传到本地存储，路径: {}", targetPath.toAbsolutePath());
        } catch (Exception e) {
            log.error("文件上传失败，路径: {}, 错误信息: {}", relativePath, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 上传文件（使用MultipartFile）
     * 
     * @param relativePath 文件相对路径（相对于 basePath）
     * @param multipartFile 要上传的文件
     * @throws IOException 文件操作异常
     */
    public void upload(String relativePath, MultipartFile multipartFile) throws IOException {
        log.info("开始上传文件到本地存储，路径: {}", relativePath);
        
        try {
            // 构建完整路径
            Path targetPath = Paths.get(basePath, relativePath);
            
            // 确保父目录存在
            Path parentDir = targetPath.getParent();
            if (parentDir != null && !Files.exists(parentDir)) {
                Files.createDirectories(parentDir);
                log.debug("创建文件目录: {}", parentDir);
            }
            
            // 保存文件
            multipartFile.transferTo(targetPath.toFile());
            
            log.info("文件成功上传到本地存储，路径: {}", targetPath.toAbsolutePath());
        } catch (Exception e) {
            log.error("文件上传失败，路径: {}, 错误信息: {}", relativePath, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 删除文件
     * 
     * @param relativePath 文件相对路径（相对于 basePath）
     * @throws IOException 文件操作异常
     */
    public void delete(String relativePath) throws IOException {
        log.info("删除本地文件，路径: {}", relativePath);
        
        try {
            Path filePath = Paths.get(basePath, relativePath);
            
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("文件删除成功，路径: {}", filePath.toAbsolutePath());
            } else {
                log.warn("文件不存在，无法删除，路径: {}", filePath.toAbsolutePath());
            }
        } catch (Exception e) {
            log.error("文件删除失败，路径: {}, 错误信息: {}", relativePath, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 检查文件是否存在
     * 
     * @param relativePath 文件相对路径
     * @return 文件是否存在
     */
    public boolean exists(String relativePath) {
        Path filePath = Paths.get(basePath, relativePath);
        return Files.exists(filePath);
    }

    /**
     * 获取文件的完整路径
     * 
     * @param relativePath 文件相对路径
     * @return 文件的完整路径
     */
    public String getFullPath(String relativePath) {
        Path filePath = Paths.get(basePath, relativePath);
        return filePath.toAbsolutePath().toString();
    }
}
