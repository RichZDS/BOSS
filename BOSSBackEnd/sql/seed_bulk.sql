USE boss;

-- 批量插入 300 个用户（user_account: bulkuser_001 ~ bulkuser_300），密码均为 11111111（加盐MD5）
SET @n := 0;
INSERT IGNORE INTO `user` (user_account, password, phone, email, username, user_role, company_id, status, profile)
SELECT
  CONCAT('bulkuser_', LPAD(@n := @n + 1, 3, '0')) AS user_account,
  '9e7b74dc1da0327f18e3a7861929b9bc'               AS password,
  CONCAT('139', LPAD(@n, 8, '0'))                  AS phone,
  CONCAT('bulkuser_', LPAD(@n, 3, '0'), '@example.com') AS email,
  CONCAT('用户', LPAD(@n, 3, '0'))                  AS username,
  'user'                                           AS user_role,
  NULL                                             AS company_id,
  1                                                AS status,
  CONCAT('批量用户', @n, '，用于联调与压力测试')       AS profile
FROM information_schema.columns
LIMIT 300;

-- 为上述 300 个用户各创建 1 份默认简历
INSERT INTO `resume` (user_id, resume_title, is_default, summary, content, attachment_url)
SELECT u.id,
       CONCAT(u.username, '-默认简历') AS resume_title,
       1 AS is_default,
       CONCAT(u.username, '：通用技能概述') AS summary,
       CONCAT('项目经历：', u.username, ' 参与电商/IM/内容平台等系统开发，具备良好工程实践。') AS content,
       '' AS attachment_url
FROM `user` u
WHERE u.user_account LIKE 'bulkuser_%'
  AND NOT EXISTS (SELECT 1 FROM resume r WHERE r.user_id = u.id);

-- 批量为每个用户创建 1 条投递记录，均匀分布到现有岗位
-- 均匀分布到现有岗位，避免重复键：每个用户选择 (user_id % 岗位数) 对应的职位
DROP TEMPORARY TABLE IF EXISTS job_index;
CREATE TEMPORARY TABLE job_index (id BIGINT, rn INT);
SET @rn := -1;
INSERT INTO job_index
SELECT jp.id, (@rn := @rn + 1) AS rn
FROM job_posting jp ORDER BY jp.id;
SET @job_cnt := (SELECT COUNT(*) FROM job_index);

INSERT INTO `application` (user_id, resume_id, job_id, boss_id, status, applied_at)
SELECT 
  u.id AS user_id,
  (SELECT r.id FROM resume r WHERE r.user_id = u.id LIMIT 1) AS resume_id,
  ji.id AS job_id,
  (SELECT jp.boss_id FROM job_posting jp WHERE jp.id = ji.id) AS boss_id,
  0 AS status,
  NOW() AS applied_at
FROM `user` u
JOIN job_index ji ON ji.rn = (u.id % @job_cnt)
WHERE u.user_account LIKE 'bulkuser_%'
  AND NOT EXISTS (SELECT 1 FROM application a WHERE a.user_id = u.id);

-- 可选：为前 50 条批量投递添加 Boss 决策（初筛通过）
-- 为前 50 条批量投递添加 Boss 决策（初筛通过）
INSERT INTO `boss_application_decision` (application_id, boss_id, decision, stage, note, decided_at)
SELECT a.id,
       (SELECT jp.boss_id FROM job_posting jp WHERE jp.id = a.job_id) AS boss_id,
       1 AS decision,
       1 AS stage,
       '批量初筛通过' AS note,
       NOW() AS decided_at
FROM `application` a
JOIN (SELECT id FROM `user` WHERE user_account LIKE 'bulkuser_%' ORDER BY id LIMIT 50) u50
  ON a.user_id = u50.id
WHERE NOT EXISTS (SELECT 1 FROM boss_application_decision d WHERE d.application_id = a.id);

