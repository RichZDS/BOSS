package com.zds.boss.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zds.boss.common.BaseResponse;
import com.zds.boss.common.DeleteRequest;
import com.zds.boss.common.ResultUtils;
import com.zds.boss.exception.BusinessException;
import com.zds.boss.exception.ErrorCode;
import com.zds.boss.exception.ThrowUtils;
import com.zds.boss.model.dto.resume.ResumeAddRequest;
import com.zds.boss.model.dto.resume.ResumeAiOptimizeRequest;
import com.zds.boss.model.dto.resume.ResumeQueryRequest;
import com.zds.boss.model.dto.resume.ResumeUpdateRequest;
import com.zds.boss.model.entity.Resume;
import com.zds.boss.model.entity.User;
import com.zds.boss.model.enums.UserRoleEnum;
import com.zds.boss.model.vo.ResumeAiOptimizeVO;
import com.zds.boss.model.vo.ResumeVO;
import com.zds.boss.service.AiService;
import com.zds.boss.service.CosService;
import com.zds.boss.service.FileService;
import com.zds.boss.service.ResumeAddressService;
import com.zds.boss.service.ResumeService;
import com.zds.boss.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @Resource
    private CosService cosService;

    @Resource
    private ResumeAddressService resumeAddressService;

    @Resource
    private AiService aiService;

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
     * 上传简历附件文件到腾讯云COS
     *
     * @param file     文件
     * @param resumeId 简历ID（可选，如果传入则同时更新resume的attachment_url）
     * @param request  HTTP请求
     * @return 文件访问URL
     */
    @PostMapping("/upload")
    public BaseResponse<String> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "resumeId", required = false) Long resumeId,
            HttpServletRequest request) {
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
        
        // 验证文件类型（仅允许PDF）
        String contentType = file.getContentType();
        boolean isAllowedContentType = "application/pdf".equals(contentType);
        boolean isAllowedExtension = ".pdf".equals(extension);
        if (!isAllowedContentType && !isAllowedExtension) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "仅支持PDF格式");
        }
        
        // 如果传入了resumeId，检查是否有权限操作该简历
        if (resumeId != null && resumeId > 0) {
            Resume resume = resumeService.getById(resumeId);
            if (resume == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "简历不存在");
            }
            if (!resume.getUserId().equals(loginUser.getId())) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权操作该简历");
            }
        }
        
        // 生成随机文件Key（用于resume_address表）
        String fileKey = UUID.randomUUID().toString().replace("-", "");
        
        // 生成唯一文件名：用户ID_时间戳_UUID
        String timestamp = String.valueOf(System.currentTimeMillis());
        String customFileName = String.format("%d_%s_%s", 
            loginUser.getId(), timestamp, fileKey);
        
        // 构建存储目录：resume/{userId}
        String directory = String.format("resume/%d", loginUser.getId());
        
        // 上传文件到腾讯云COS
        String fileUrl = cosService.uploadFile(file, directory, customFileName);
        
        // 如果传入了resumeId，更新resume的attachment_url
        if (resumeId != null && resumeId > 0) {
            Resume resume = new Resume();
            resume.setId(resumeId);
            resume.setAttachmentUrl(fileUrl);
            resumeService.updateById(resume);
            log.info("已更新简历 {} 的附件URL", resumeId);
        }
        
        // 保存或更新resume_address记录
        resumeAddressService.saveOrUpdateByUserId(loginUser.getId(), resumeId, fileUrl, fileKey);
        log.info("已保存简历地址记录，userId: {}, fileKey: {}", loginUser.getId(), fileKey);
        
        log.info("用户 {} 上传简历PDF成功: {}, URL: {}", loginUser.getId(), originalFilename, fileUrl);
        return ResultUtils.success(fileUrl);
    }

    /**
     * 删除COS中的简历附件文件
     *
     * @param fileUrl  文件URL
     * @param resumeId 简历ID（可选，如果传入则同时清空resume的attachment_url）
     * @param request  HTTP请求
     * @return 是否删除成功
     */
    @PostMapping("/delete-file")
    public BaseResponse<Boolean> deleteFile(
            @RequestParam("fileUrl") String fileUrl,
            @RequestParam(value = "resumeId", required = false) Long resumeId,
            HttpServletRequest request) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件URL不能为空");
        }
        
        User loginUser = userService.getLoginUser(request);
        
        // 验证URL是否属于当前用户（安全检查）
        String userDirectory = String.format("resume/%d/", loginUser.getId());
        if (!fileUrl.contains(userDirectory)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权删除该文件");
        }
        
        // 删除COS中的文件
        boolean result = cosService.deleteFile(fileUrl);
        
        if (result) {
            log.info("用户 {} 删除文件成功: {}", loginUser.getId(), fileUrl);
            
            // 如果传入了resumeId，清空resume的attachment_url
            if (resumeId != null && resumeId > 0) {
                Resume resume = resumeService.getById(resumeId);
                if (resume != null && resume.getUserId().equals(loginUser.getId())) {
                    resume.setAttachmentUrl("");
                    resumeService.updateById(resume);
                    log.info("已清空简历 {} 的附件URL", resumeId);
                }
            }
            
            // 删除resume_address记录
            resumeAddressService.deleteByUserId(loginUser.getId());
            log.info("已删除用户 {} 的简历地址记录", loginUser.getId());
        } else {
            log.warn("用户 {} 删除文件失败: {}", loginUser.getId(), fileUrl);
        }
        
        return ResultUtils.success(result);
    }

    /**
     * AI优化简历
     *
     * @param request     AI优化请求
     * @param httpRequest HTTP请求
     * @return 优化后的简历内容
     */
    @PostMapping("/ai/optimize")
    public BaseResponse<ResumeAiOptimizeVO> aiOptimizeResume(
            @RequestBody ResumeAiOptimizeRequest request,
            HttpServletRequest httpRequest) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }
        
        // 验证用户登录
        userService.getLoginUser(httpRequest);
        
        // 检查是否有内容需要优化
        boolean hasContent = (request.getResumeTitle() != null && !request.getResumeTitle().trim().isEmpty())
                || (request.getSummary() != null && !request.getSummary().trim().isEmpty())
                || (request.getContent() != null && !request.getContent().trim().isEmpty());
        
        if (!hasContent) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请至少填写一项内容后再进行AI优化");
        }
        
        // 调用AI服务优化简历
        ResumeAiOptimizeVO result = aiService.optimizeResume(request);
        
        return ResultUtils.success(result);
    }
}
