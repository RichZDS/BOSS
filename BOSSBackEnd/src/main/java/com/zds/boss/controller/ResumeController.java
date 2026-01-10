package com.zds.boss.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zds.boss.common.BaseResponse;
import com.zds.boss.common.DeleteRequest;
import com.zds.boss.common.ResultUtils;
import com.zds.boss.exception.BusinessException;
import com.zds.boss.exception.ErrorCode;
import com.zds.boss.exception.ThrowUtils;
import com.zds.boss.model.dto.resume.ResumeAddRequest;
import com.zds.boss.model.dto.resume.ResumeQueryRequest;
import com.zds.boss.model.dto.resume.ResumeUpdateRequest;
import com.zds.boss.model.entity.Resume;
import com.zds.boss.model.entity.User;
import com.zds.boss.model.enums.UserRoleEnum;
import com.zds.boss.model.vo.ResumeVO;
import com.zds.boss.service.FileService;
import com.zds.boss.service.ResumeService;
import com.zds.boss.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * 简历接口
 */
@RestController
@RequestMapping("/resume")
@Slf4j
public class ResumeController {

    @Resource
    private ResumeService resumeService;

    @Resource
    private UserService userService;

    @Resource
    private FileService fileService;

    @Value("${file.max-size-mb:10}")
    private long maxSizeMb;

    /**
     * 创建简历
     *
     * @param resumeAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addResume(@RequestBody ResumeAddRequest resumeAddRequest, HttpServletRequest request) {
        if (resumeAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        long newResumeId = resumeService.addResume(resumeAddRequest, loginUser);
        return ResultUtils.success(newResumeId);
    }

    /**
     * 删除简历
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteResume(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = resumeService.deleteResume(deleteRequest.getId(), loginUser);
        return ResultUtils.success(result);
    }

    /**
     * 更新简历
     *
     * @param resumeUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateResume(@RequestBody ResumeUpdateRequest resumeUpdateRequest, HttpServletRequest request) {
        if (resumeUpdateRequest == null || resumeUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = resumeService.updateResume(resumeUpdateRequest, loginUser);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取简历（封装类）
     *
     * @param id
     * @param request
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<ResumeVO> getResumeVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        Resume resume = resumeService.getById(id);
        if (resume == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        
        // 权限检查: 管理员、Boss、本人可查看
        boolean isAdmin = UserRoleEnum.ADMIN.getValue().equals(loginUser.getUserRole());
        boolean isBoss = UserRoleEnum.BOSS.getValue().equals(loginUser.getUserRole());
        boolean isOwner = resume.getUserId().equals(loginUser.getId());
        
        if (!isAdmin && !isBoss && !isOwner) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权查看该简历");
        }
        
        return ResultUtils.success(resumeService.getResumeVO(resume));
    }

    /**
     * 分页获取简历列表（封装类）
     *
     * @param resumeQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<ResumeVO>> listResumeVOByPage(@RequestBody ResumeQueryRequest resumeQueryRequest, HttpServletRequest request) {
        if (resumeQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        User loginUser = userService.getLoginUser(request);
        boolean isAdmin = UserRoleEnum.ADMIN.getValue().equals(loginUser.getUserRole());
        boolean isBoss = UserRoleEnum.BOSS.getValue().equals(loginUser.getUserRole());
        
        // 如果不是管理员也不是Boss，只能看自己的
        if (!isAdmin && !isBoss) {
            resumeQueryRequest.setUserId(loginUser.getId());
        }
        
        long size = resumeQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<ResumeVO> resumeVOPage = resumeService.listResumeVOByPage(resumeQueryRequest);
        return ResultUtils.success(resumeVOPage);
    }

    /**
     * 上传简历附件文件
     *
     * @param file 文件
     * @param request HTTP请求
     * @return 文件访问URL
     */
    @PostMapping("/upload")
    public BaseResponse<String> uploadFile(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件不能为空");
        }
        
        User loginUser = userService.getLoginUser(request);
        
        // 检查文件大小
        long fileSize = file.getSize();
        long maxSize = maxSizeMb * 1024 * 1024; // 转换为字节
        if (fileSize > maxSize) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, String.format("文件大小不能超过%dMB", maxSizeMb));
        }
        
        // 获取原始文件名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件名不能为空");
        }
        
        // 获取文件扩展名
        String extension = "";
        int lastDotIndex = originalFilename.lastIndexOf(".");
        if (lastDotIndex > 0) {
            extension = originalFilename.substring(lastDotIndex).toLowerCase();
        }
        
        // 验证文件类型（允许PDF和Word文档）
        String contentType = file.getContentType();
        boolean isAllowedContentType = "application/pdf".equals(contentType)
            || "application/msword".equals(contentType)
            || "application/vnd.openxmlformats-officedocument.wordprocessingml.document".equals(contentType);
        boolean isAllowedExtension = ".pdf".equals(extension) || ".doc".equals(extension) || ".docx".equals(extension);
        if (!isAllowedContentType && !isAllowedExtension) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "仅支持PDF和Word文档格式");
        }
        
        try {
            // 生成唯一文件名：用户ID_时间戳_UUID.扩展名
            String timestamp = String.valueOf(System.currentTimeMillis());
            String uniqueFileName = String.format("%d_%s_%s%s", 
                loginUser.getId(), timestamp, UUID.randomUUID().toString().replace("-", ""), extension);
            
            // 构建相对路径：resume/{userId}/{filename}
            String relativePath = String.format("resume/%d/%s", loginUser.getId(), uniqueFileName);
            
            // 上传文件
            fileService.upload(relativePath, file);
            
            // 构建文件访问URL
            String fileUrl = fileService.buildUrl(relativePath);
            
            log.info("用户 {} 上传文件成功: {}, URL: {}", loginUser.getId(), originalFilename, fileUrl);
            return ResultUtils.success(fileUrl);
        } catch (IOException e) {
            log.error("文件上传失败: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "文件上传失败: " + e.getMessage());
        }
    }
}
