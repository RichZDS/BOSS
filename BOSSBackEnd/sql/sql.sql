/* =========================
   Boss 招聘系统 - 全量建表SQL（无外键）
   MySQL 8.x / InnoDB / utf8mb4
   ========================= */

-- 1) 建库（可选：你已有库就注释掉）
CREATE DATABASE IF NOT EXISTS boss
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_0900_ai_ci;

USE boss;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 2) 清理（按依赖大致逆序，避免报错）
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
-- admin 管理员表
-- =========================
CREATE TABLE `admin`
(
    `id`            BIGINT UNSIGNED AUTO_INCREMENT COMMENT '管理员ID',
    `username`      VARCHAR(64)                        NOT NULL COMMENT '登录名（唯一）',
    `password_hash` VARCHAR(255)                       NOT NULL COMMENT '密码哈希',
    `admin_name`    VARCHAR(64)                        NULL COMMENT '管理员姓名',
    `phone`         VARCHAR(32)                        NULL,
    `email`         VARCHAR(128)                       NULL,
    `status`        TINYINT  DEFAULT 1                 NOT NULL COMMENT '状态：0禁用 1正常',
    `created_at`    DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    `updated_at`    DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP,
    `is_deleted`    TINYINT  DEFAULT 0                 NOT NULL,
    `deleted_at`    DATETIME                           NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_admin_username` (`username`)
) ENGINE=InnoDB COMMENT '管理员表';

CREATE INDEX `idx_admin_status` ON `admin` (`status`, `created_at`);

-- =========================
-- user 候选人用户表（注意：user 用反引号）
-- =========================
CREATE TABLE `user`
(
    `id`           BIGINT UNSIGNED AUTO_INCREMENT COMMENT '用户ID',
    `user_account` VARCHAR(64)                        NOT NULL COMMENT '登录名（唯一）',
    `password`     VARCHAR(255)                       NOT NULL COMMENT '密码哈希',
    `phone`        VARCHAR(32)                        NULL COMMENT '手机号',
    `email`        VARCHAR(128)                       NULL COMMENT '邮箱',
    `username`     VARCHAR(64)                        NULL COMMENT '昵称',
    `status`       TINYINT  DEFAULT 1                 NOT NULL COMMENT '状态：0禁用 1正常',
    `created_at`   DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    `updated_at`   DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP,
    `is_deleted`   TINYINT  DEFAULT 0                 NOT NULL COMMENT '软删：0否 1是',
    `deleted_at`   DATETIME                           NULL,
    `profile`      TINYTEXT                           NULL COMMENT '简介',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_username` (`user_account`)
) ENGINE=InnoDB COMMENT '候选人用户表';

CREATE INDEX `idx_user_email`  ON `user` (`email`);
CREATE INDEX `idx_user_phone`  ON `user` (`phone`);
CREATE INDEX `idx_user_status` ON `user` (`status`, `created_at`);

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
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_company_name` (`name`)
) ENGINE=InnoDB COMMENT '公司表';

CREATE INDEX `idx_company_industry` ON `company` (`industry`);
CREATE INDEX `idx_company_status`   ON `company` (`status`, `created_at`);

-- =========================
-- boss 招聘方账号表
-- =========================
CREATE TABLE `boss`
(
    `id`            BIGINT UNSIGNED AUTO_INCREMENT COMMENT 'BossID',
    `username`      VARCHAR(64)                        NOT NULL COMMENT '登录名（唯一）',
    `password_hash` VARCHAR(255)                       NOT NULL COMMENT '密码哈希',
    `boss_name`     VARCHAR(64)                        NULL COMMENT '负责人姓名',
    `phone`         VARCHAR(32)                        NULL COMMENT '手机号',
    `email`         VARCHAR(128)                       NULL COMMENT '邮箱',
    `company_id`    BIGINT UNSIGNED                    NULL COMMENT '公司ID（无外键，建议配公司表）',
    `company_name`  VARCHAR(128)                       NULL COMMENT '公司名（冗余字段，方便展示）',
    `status`        TINYINT  DEFAULT 1                 NOT NULL COMMENT '状态：0禁用 1正常',
    `created_at`    DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    `updated_at`    DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP,
    `is_deleted`    TINYINT  DEFAULT 0                 NOT NULL,
    `deleted_at`    DATETIME                           NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_boss_username` (`username`)
) ENGINE=InnoDB COMMENT 'Boss招聘方账号表';

CREATE INDEX `idx_boss_company` ON `boss` (`company_id`);
CREATE INDEX `idx_boss_email`   ON `boss` (`email`);
CREATE INDEX `idx_boss_phone`   ON `boss` (`phone`);

-- =========================
-- job_posting 岗位表
-- =========================
CREATE TABLE `job_posting`
(
    `id`          BIGINT UNSIGNED AUTO_INCREMENT COMMENT '岗位ID',
    `boss_id`     BIGINT UNSIGNED                    NOT NULL COMMENT 'BossID（无外键）',
    `company_id`  BIGINT UNSIGNED                    NULL COMMENT '公司ID（无外键）',
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
    PRIMARY KEY (`id`)
) ENGINE=InnoDB COMMENT '岗位表';

CREATE INDEX `idx_job_boss`        ON `job_posting` (`boss_id`, `status`, `publish_at`);
CREATE INDEX `idx_job_company`     ON `job_posting` (`company_id`, `status`, `publish_at`);
CREATE INDEX `idx_job_status_time` ON `job_posting` (`status`, `publish_at`);
CREATE INDEX `idx_job_title`       ON `job_posting` (`title`);

-- =========================
-- resume 简历表
-- =========================
CREATE TABLE `resume`
(
    `id`             BIGINT UNSIGNED AUTO_INCREMENT COMMENT '简历ID',
    `user_id`        BIGINT UNSIGNED                        NOT NULL COMMENT '用户ID（无外键）',
    `resume_title`   VARCHAR(128) DEFAULT '默认简历'        NOT NULL COMMENT '简历标题',
    `is_default`     TINYINT      DEFAULT 0                 NOT NULL COMMENT '是否默认：0否 1是',
    `summary`        TEXT                                   NULL COMMENT '摘要',
    `content`        LONGTEXT                               NULL COMMENT '正文（可存Markdown/JSON/HTML）',
    `attachment_url` VARCHAR(512)                           NULL COMMENT '附件URL（PDF/Word）',
    `updated_at`     DATETIME     DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP,
    `created_at`     DATETIME     DEFAULT CURRENT_TIMESTAMP NOT NULL,
    `is_deleted`     TINYINT      DEFAULT 0                 NOT NULL,
    `deleted_at`     DATETIME                               NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB COMMENT '简历表';

CREATE INDEX `idx_resume_user`         ON `resume` (`user_id`, `created_at`);
CREATE INDEX `idx_resume_user_default` ON `resume` (`user_id`, `is_default`);

-- =========================
-- application 用户投递记录表
-- =========================
CREATE TABLE `application`
(
    `id`         BIGINT UNSIGNED AUTO_INCREMENT COMMENT '投递ID',
    `user_id`    BIGINT UNSIGNED                    NOT NULL COMMENT '用户ID（无外键）',
    `resume_id`  BIGINT UNSIGNED                    NULL COMMENT '简历ID（无外键，可为空）',
    `job_id`     BIGINT UNSIGNED                    NULL COMMENT '岗位ID（无外键，建议配岗位表）',
    `boss_id`    BIGINT UNSIGNED                    NULL COMMENT 'BossID（无外键，冗余字段便于查）',
    `status`     TINYINT  DEFAULT 0                 NOT NULL COMMENT '状态：0已投递 1已撤回 2已过期',
    `applied_at` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '投递时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_apply_user_job` (`user_id`, `job_id`)
) ENGINE=InnoDB COMMENT '用户投递记录表';

CREATE INDEX `idx_apply_boss_time` ON `application` (`boss_id`, `applied_at`);
CREATE INDEX `idx_apply_job_time`  ON `application` (`job_id`, `applied_at`);
CREATE INDEX `idx_apply_status`    ON `application` (`status`, `applied_at`);
CREATE INDEX `idx_apply_user_time` ON `application` (`user_id`, `applied_at`);

-- =========================
-- boss_application_decision Boss处理投递表
-- =========================
CREATE TABLE `boss_application_decision`
(
    `id`             BIGINT UNSIGNED AUTO_INCREMENT COMMENT 'ID',
    `application_id` BIGINT UNSIGNED                    NOT NULL COMMENT '投递ID（无外键）',
    `boss_id`        BIGINT UNSIGNED                    NOT NULL COMMENT 'BossID（无外键）',
    `decision`       TINYINT                            NOT NULL COMMENT '处理结果：1接受/进入流程 2拒绝 3待定',
    `stage`          TINYINT  DEFAULT 0                 NOT NULL COMMENT '阶段：0筛选 1邀面 2面试 3Offer 4结束',
    `note`           VARCHAR(512)                       NULL COMMENT '备注',
    `decided_at`     DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '处理时间',
    `updated_at`     DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_decision_application` (`application_id`)
) ENGINE=InnoDB COMMENT 'Boss处理投递（接受/拒绝）表';

CREATE INDEX `idx_decision_boss_time` ON `boss_application_decision` (`boss_id`, `decided_at`);
CREATE INDEX `idx_decision_decision`  ON `boss_application_decision` (`decision`, `decided_at`);
CREATE INDEX `idx_decision_stage`     ON `boss_application_decision` (`stage`, `decided_at`);

-- =========================
-- interview 面试安排表
-- =========================
CREATE TABLE `interview`
(
    `id`             BIGINT UNSIGNED AUTO_INCREMENT COMMENT '面试ID',
    `application_id` BIGINT UNSIGNED                    NOT NULL COMMENT '投递ID（无外键）',
    `boss_id`        BIGINT UNSIGNED                    NOT NULL COMMENT 'BossID（无外键）',
    `user_id`        BIGINT UNSIGNED                    NOT NULL COMMENT '用户ID（无外键）',
    `interview_time` DATETIME                           NOT NULL COMMENT '面试时间',
    `mode`           TINYINT  DEFAULT 0                 NOT NULL COMMENT '方式：0线上 1线下',
    `location`       VARCHAR(255)                       NULL COMMENT '地点/会议链接',
    `status`         TINYINT  DEFAULT 0                 NOT NULL COMMENT '状态：0待确认 1已确认 2已完成 3取消',
    `note`           VARCHAR(512)                       NULL,
    `created_at`     DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    `updated_at`     DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_interview_app` (`application_id`)
) ENGINE=InnoDB COMMENT '面试安排表';

CREATE INDEX `idx_interview_boss_time` ON `interview` (`boss_id`, `interview_time`);
CREATE INDEX `idx_interview_status`    ON `interview` (`status`, `interview_time`);
CREATE INDEX `idx_interview_user_time` ON `interview` (`user_id`, `interview_time`);
