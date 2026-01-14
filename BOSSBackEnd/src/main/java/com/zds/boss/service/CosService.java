package com.zds.boss.service;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.*;
import com.zds.boss.config.CosConfig;
import com.zds.boss.exception.BusinessException;
import com.zds.boss.exception.ErrorCode;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * 腾讯云COS对象存储服务
 */
@Service
@Slf4j
public class CosService {

    @Resource
    private COSClient cosClient;

    @Resource
    private CosConfig cosConfig;

    /**
     * 上传文件到COS
     *
     * @param file       要上传的文件
     * @param directory  存储目录（如：resume/123）
     * @param customName 自定义文件名（不含扩展名），为null时使用UUID
     * @return 文件的访问URL
     */
    public String uploadFile(MultipartFile file, String directory, String customName) {
        // 获取原始文件名和扩展名
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        }

        // 生成文件名
        String fileName;
        if (customName != null && !customName.isEmpty()) {
            fileName = customName + extension;
        } else {
            fileName = UUID.randomUUID().toString().replace("-", "") + extension;
        }

        // 构建完整的对象Key（路径）
        String key = directory + "/" + fileName;
        // 移除开头的斜杠（如果有）
        if (key.startsWith("/")) {
            key = key.substring(1);
        }

        try (InputStream inputStream = file.getInputStream()) {
            // 设置对象元数据
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            // 创建上传请求
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    cosConfig.getBucketName(),
                    key,
                    inputStream,
                    metadata
            );

            // 执行上传
            PutObjectResult result = cosClient.putObject(putObjectRequest);
            log.info("文件上传成功，Key: {}, ETag: {}", key, result.getETag());

            // 返回文件访问URL
            return cosConfig.getUrlPrefix() + "/" + key;
        } catch (IOException e) {
            log.error("文件上传失败: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 上传文件到COS（使用默认UUID文件名）
     *
     * @param file      要上传的文件
     * @param directory 存储目录
     * @return 文件的访问URL
     */
    public String uploadFile(MultipartFile file, String directory) {
        return uploadFile(file, directory, null);
    }

    /**
     * 删除COS中的文件
     *
     * @param fileUrl 文件的完整URL
     * @return 是否删除成功
     */
    public boolean deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return false;
        }

        try {
            // 从URL中提取对象Key
            String key = extractKeyFromUrl(fileUrl);
            if (key == null) {
                log.warn("无法从URL中提取Key: {}", fileUrl);
                return false;
            }

            // 删除对象
            cosClient.deleteObject(cosConfig.getBucketName(), key);
            log.info("文件删除成功，Key: {}", key);
            return true;
        } catch (Exception e) {
            log.error("文件删除失败: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 从URL中提取对象Key
     *
     * @param fileUrl 文件URL
     * @return 对象Key
     */
    private String extractKeyFromUrl(String fileUrl) {
        String urlPrefix = cosConfig.getUrlPrefix();
        if (fileUrl.startsWith(urlPrefix)) {
            String key = fileUrl.substring(urlPrefix.length());
            if (key.startsWith("/")) {
                key = key.substring(1);
            }
            return key;
        }
        return null;
    }

    /**
     * 检查文件是否存在
     *
     * @param key 对象Key
     * @return 是否存在
     */
    public boolean doesObjectExist(String key) {
        try {
            return cosClient.doesObjectExist(cosConfig.getBucketName(), key);
        } catch (Exception e) {
            log.error("检查文件是否存在失败: {}", e.getMessage(), e);
            return false;
        }
    }
}
