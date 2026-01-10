/* =========================
   Boss 招聘系统 - 重构后全量建表SQL
   MySQL 8.x / InnoDB / utf8mb4
   ========================= */

CREATE DATABASE IF NOT EXISTS boss
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_0900_ai_ci;

USE boss;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 2) 清理
DROP TABLE IF EXISTS `interview`;
DROP TABLE IF EXISTS `boss_application_decision`;
DROP TABLE IF EXISTS `application`;
DROP TABLE IF EXISTS `job_posting`;
DROP TABLE IF EXISTS `resume`;
DROP TABLE IF EXISTS `company`;
DROP TABLE IF EXISTS `boss`; 
DROP TABLE IF EXISTS `admin`;
DROP TABLE IF EXISTS `user`;

SET FOREIGN_KEY_CHECKS = 1;

-- =========================
-- user 用户表 (整合了求职者、Boss、Admin)
-- =========================
CREATE TABLE `user`
(
    `id`           BIGINT UNSIGNED AUTO_INCREMENT COMMENT '用户ID',
    `user_account` VARCHAR(64)                        NOT NULL COMMENT '登录名（唯一）',
    `password`     VARCHAR(255)                       NOT NULL COMMENT '密码哈希',
    `phone`        VARCHAR(32)                        NULL COMMENT '手机号',
    `email`        VARCHAR(128)                       NULL COMMENT '邮箱',
    `username`     VARCHAR(64)                        NULL COMMENT '昵称/姓名',
    `user_role`    VARCHAR(20) DEFAULT 'user'         NOT NULL COMMENT '用户角色：user-求职者, boss-Boss, admin-管理员',
    `company_id`   BIGINT UNSIGNED                    NULL COMMENT '公司ID（仅Boss角色有效）',
    `status`       TINYINT  DEFAULT 1                 NOT NULL COMMENT '状态：0禁用 1正常',
    `profile`      TINYTEXT                           NULL COMMENT '简介',
    `created_at`   DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    `updated_at`   DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP,
    `is_deleted`   TINYINT  DEFAULT 0                 NOT NULL COMMENT '软删：0否 1是',
    `deleted_at`   DATETIME                           NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_account` (`user_account`)
) ENGINE=InnoDB COMMENT '用户表';

CREATE INDEX `idx_user_role`   ON `user` (`user_role`);
CREATE INDEX `idx_user_email`  ON `user` (`email`);
CREATE INDEX `idx_user_phone`  ON `user` (`phone`);

-- =========================
-- company 公司表
-- =========================
CREATE TABLE `company`
(
    `id`         BIGINT UNSIGNED AUTO_INCREMENT COMMENT '公司ID',
    `name`       VARCHAR(128)                       NOT NULL COMMENT '公司名',
    `industry`   VARCHAR(128)                       NULL COMMENT '行业',
    `size_range` VARCHAR(64)                        NULL COMMENT '规模（如1-50/50-200）',
    `website`    VARCHAR(255)                       NULL COMMENT '官网',
    `address`    VARCHAR(255)                       NULL COMMENT '地址',
    `intro`      TEXT                               NULL COMMENT '公司介绍',
    `status`     TINYINT  DEFAULT 1                 NOT NULL COMMENT '状态：0禁用 1正常',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP,
    `is_deleted` TINYINT  DEFAULT 0                 NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_company_name` (`name`)
) ENGINE=InnoDB COMMENT '公司表';

-- =========================
-- job_posting 岗位表
-- =========================
CREATE TABLE `job_posting`
(
    `id`          BIGINT UNSIGNED AUTO_INCREMENT COMMENT '岗位ID',
    `boss_id`     BIGINT UNSIGNED                    NOT NULL COMMENT '发布者ID (User ID)',
    `company_id`  BIGINT UNSIGNED                    NOT NULL COMMENT '公司ID',
    `title`       VARCHAR(128)                       NOT NULL COMMENT '岗位名称',
    `location`    VARCHAR(128)                       NULL COMMENT '地点',
    `job_type`    VARCHAR(64)                        NULL COMMENT '类型：全职/实习',
    `salary_min`  INT                                NULL,
    `salary_max`  INT                                NULL,
    `description` TEXT                               NULL COMMENT '描述',
    `requirement` TEXT                               NULL COMMENT '要求',
    `status`      TINYINT  DEFAULT 1                 NOT NULL COMMENT '状态：0草稿 1发布 2关闭',
    `publish_at`  DATETIME                           NULL,
    `created_at`  DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    `updated_at`  DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP,
    `is_deleted`  TINYINT  DEFAULT 0                 NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB COMMENT '岗位表';

CREATE INDEX `idx_job_boss`        ON `job_posting` (`boss_id`);
CREATE INDEX `idx_job_company`     ON `job_posting` (`company_id`);

-- =========================
-- resume 简历表
-- =========================
CREATE TABLE `resume`
(
    `id`             BIGINT UNSIGNED AUTO_INCREMENT COMMENT '简历ID',
    `user_id`        BIGINT UNSIGNED                        NOT NULL COMMENT '用户ID',
    `resume_title`   VARCHAR(128) DEFAULT '默认简历'        NOT NULL COMMENT '简历标题',
    `is_default`     TINYINT      DEFAULT 0                 NOT NULL COMMENT '是否默认：0否 1是',
    `summary`        TEXT                                   NULL COMMENT '摘要',
    `content`        LONGTEXT                               NULL COMMENT '正文',
    `attachment_url` VARCHAR(512)                           NULL COMMENT '附件URL',
    `created_at`     DATETIME     DEFAULT CURRENT_TIMESTAMP NOT NULL,
    `updated_at`     DATETIME     DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP,
    `is_deleted`     TINYINT      DEFAULT 0                 NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB COMMENT '简历表';

CREATE INDEX `idx_resume_user` ON `resume` (`user_id`);

-- =========================
-- application 用户投递记录表
-- =========================
CREATE TABLE `application`
(
    `id`         BIGINT UNSIGNED AUTO_INCREMENT COMMENT '投递ID',
    `user_id`    BIGINT UNSIGNED                    NOT NULL COMMENT '求职者ID',
    `resume_id`  BIGINT UNSIGNED                    NULL COMMENT '简历ID',
    `job_id`     BIGINT UNSIGNED                    NOT NULL COMMENT '岗位ID',
    `boss_id`    BIGINT UNSIGNED                    NOT NULL COMMENT '招聘者ID (冗余)',
    `status`     TINYINT  DEFAULT 0                 NOT NULL COMMENT '状态：0已投递 1已撤回 2已过期 3面试中 4已录用 5已拒绝',
    `applied_at` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '投递时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP,
    `is_deleted` TINYINT  DEFAULT 0                 NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_apply_user_job` (`user_id`, `job_id`)
) ENGINE=InnoDB COMMENT '用户投递记录表';

-- =========================
-- boss_application_decision Boss处理投递表
-- =========================
CREATE TABLE `boss_application_decision`
(
    `id`             BIGINT UNSIGNED AUTO_INCREMENT COMMENT 'ID',
    `application_id` BIGINT UNSIGNED                    NOT NULL COMMENT '投递ID',
    `boss_id`        BIGINT UNSIGNED                    NOT NULL COMMENT '招聘者ID',
    `decision`       TINYINT                            NOT NULL COMMENT '处理结果：1接受/进入流程 2拒绝 3待定',
    `stage`          TINYINT  DEFAULT 0                 NOT NULL COMMENT '阶段：0筛选 1邀面 2面试 3Offer 4结束',
    `note`           VARCHAR(512)                       NULL COMMENT '备注',
    `decided_at`     DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '处理时间',
    `updated_at`     DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP,
    `is_deleted`     TINYINT  DEFAULT 0                 NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_decision_application` (`application_id`)
) ENGINE=InnoDB COMMENT 'Boss处理投递表';

-- =========================
-- interview 面试安排表
-- =========================
CREATE TABLE `interview`
(
    `id`             BIGINT UNSIGNED AUTO_INCREMENT COMMENT '面试ID',
    `application_id` BIGINT UNSIGNED                    NOT NULL COMMENT '投递ID',
    `boss_id`        BIGINT UNSIGNED                    NOT NULL COMMENT '招聘者ID',
    `user_id`        BIGINT UNSIGNED                    NOT NULL COMMENT '求职者ID',
    `interview_time` DATETIME                           NOT NULL COMMENT '面试时间',
    `mode`           TINYINT  DEFAULT 0                 NOT NULL COMMENT '方式：0线上 1线下',
    `location`       VARCHAR(255)                       NULL COMMENT '地点/会议链接',
    `status`         TINYINT  DEFAULT 0                 NOT NULL COMMENT '状态：0待确认 1已确认 2已完成 3取消 4拒绝',
    `note`           VARCHAR(512)                       NULL,
    `created_at`     DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    `updated_at`     DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP,
    `is_deleted`     TINYINT  DEFAULT 0                 NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB COMMENT '面试安排表';
