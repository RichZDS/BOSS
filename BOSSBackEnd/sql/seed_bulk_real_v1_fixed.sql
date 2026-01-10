-- ==========================================================
-- Boss 招聘系统：高真实度种子数据（可重复执行）bulk_real_v1
-- 说明：
-- 1) 不硬编码任何主键ID；全部通过自然键/查询获取
-- 2) 幂等：对唯一键用 INSERT IGNORE；对非唯一用 NOT EXISTS
-- 3) utf8mb4：避免中文/emoji 乱码
-- 4) 数据规模：1000 个求职者 + 20 公司 + 40 Boss + 200 岗位 + 1000 投递 + 部分决策/面试
-- ==========================================================

USE boss;
SET NAMES utf8mb4;

START TRANSACTION;

-- 种子标记（用于识别/清理：只删带该标记的数据）
SET @SEED_TAG := 'SEED_TAG:bulk_real_v1';

-- ----------------------------------------------------------
-- 1) 构造 1..1000 序列（不使用窗口函数/递归，兼容性更好）
-- ----------------------------------------------------------
-- ----------------------------------------------------------
-- 1) 构造 1..1000 序列（兼容“Can't reopen table”环境）
--    说明：不再多次引用临时表 seed_digits，改用内联 digits
-- ----------------------------------------------------------
CREATE TEMPORARY TABLE IF NOT EXISTS seed_seq (n INT NOT NULL PRIMARY KEY) ENGINE=MEMORY;
TRUNCATE TABLE seed_seq;

INSERT INTO seed_seq(n)
SELECT (a.d + b.d*10 + c.d*100 + d4.d*1000) + 1 AS n
FROM
  (SELECT 0 d UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
   UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) a
CROSS JOIN
  (SELECT 0 d UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
   UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) b
CROSS JOIN
  (SELECT 0 d UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
   UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) c
CROSS JOIN
  (SELECT 0 d UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
   UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) d4
WHERE (a.d + b.d*10 + c.d*100 + d4.d*1000) < 1000;


-- ----------------------------------------------------------
-- 2) 基础字典：城市/姓名/方向（让数据“像真的”）
-- ----------------------------------------------------------
CREATE TEMPORARY TABLE IF NOT EXISTS seed_city (
  rn INT PRIMARY KEY,
  city VARCHAR(64),
  district VARCHAR(64)
) ENGINE=InnoDB;
TRUNCATE TABLE seed_city;
INSERT INTO seed_city(rn, city, district) VALUES
(0,'北京','海淀'),(1,'上海','浦东'),(2,'深圳','南山'),(3,'杭州','滨江'),(4,'广州','天河'),
(5,'成都','高新'),(6,'南京','建邺'),(7,'武汉','光谷'),(8,'西安','高新'),(9,'苏州','工业园'),
(10,'重庆','渝中'),(11,'天津','滨海'),(12,'长沙','岳麓'),(13,'合肥','高新'),(14,'厦门','思明'),
(15,'青岛','崂山'),(16,'宁波','鄞州'),(17,'济南','历下'),(18,'郑州','金水'),(19,'福州','鼓楼');

CREATE TEMPORARY TABLE IF NOT EXISTS seed_surname (rn INT PRIMARY KEY, v VARCHAR(16)) ENGINE=InnoDB;
TRUNCATE TABLE seed_surname;
INSERT INTO seed_surname(rn,v) VALUES
(0,'王'),(1,'李'),(2,'张'),(3,'刘'),(4,'陈'),(5,'杨'),(6,'黄'),(7,'赵'),(8,'周'),(9,'吴'),
(10,'徐'),(11,'孙'),(12,'胡'),(13,'朱'),(14,'高'),(15,'林'),(16,'何'),(17,'郭'),(18,'马'),(19,'罗'),
(20,'梁'),(21,'宋'),(22,'郑'),(23,'谢'),(24,'韩'),(25,'唐'),(26,'冯'),(27,'于'),(28,'董'),(29,'萧');

CREATE TEMPORARY TABLE IF NOT EXISTS seed_given (rn INT PRIMARY KEY, v VARCHAR(16)) ENGINE=InnoDB;
TRUNCATE TABLE seed_given;
INSERT INTO seed_given(rn,v) VALUES
(0,'子涵'),(1,'雨桐'),(2,'浩然'),(3,'嘉怡'),(4,'宇轩'),(5,'欣怡'),(6,'俊杰'),(7,'梓萱'),(8,'思琪'),(9,'泽宇'),
(10,'晨曦'),(11,'若曦'),(12,'明轩'),(13,'芷晴'),(14,'一凡'),(15,'可欣'),(16,'天宇'),(17,'语嫣'),(18,'书豪'),(19,'梦瑶'),
(20,'佳宁'),(21,'思源'),(22,'安然'),(23,'梓睿'),(24,'昕妍'),(25,'奕辰'),(26,'沐辰'),(27,'心怡'),(28,'知远'),(29,'嘉航');

CREATE TEMPORARY TABLE IF NOT EXISTS seed_track (
  rn INT PRIMARY KEY,
  track VARCHAR(64),
  keywords VARCHAR(255)
) ENGINE=InnoDB;
TRUNCATE TABLE seed_track;
INSERT INTO seed_track(rn,track,keywords) VALUES
(0,'Java后端','Java, Spring Boot, MySQL, Redis, 微服务, MQ'),
(1,'前端工程化','Vue/React, TypeScript, Webpack/Vite, 性能优化'),
(2,'数据分析','SQL, Python, 可视化, 指标体系, A/B Test'),
(3,'测试开发','自动化测试, Java/Python, CI/CD, 性能测试'),
(4,'算法/推荐','Python, 机器学习, 特征工程, 推荐/排序, TensorFlow'),
(5,'产品经理','需求分析, PRD, 数据驱动, 版本迭代, 协作推进'),
(6,'运维/DevOps','Linux, Docker, Kubernetes, 监控告警, 自动化'),
(7,'Android','Kotlin/Java, Jetpack, 性能优化, 组件化'),
(8,'iOS','Swift, UIKit/SwiftUI, 性能优化, 组件化'),
(9,'安全工程','Web安全, 渗透测试, 风控, 安全审计');

SET @sn_cnt := (SELECT COUNT(*) FROM seed_surname);
SET @gn_cnt := (SELECT COUNT(*) FROM seed_given);
SET @ct_cnt := (SELECT COUNT(*) FROM seed_city);
SET @tk_cnt := (SELECT COUNT(*) FROM seed_track);

-- ----------------------------------------------------------
-- 3) 公司（20家，真实感：行业/规模/官网/地址/介绍）
-- ----------------------------------------------------------
CREATE TEMPORARY TABLE IF NOT EXISTS seed_company (
  rn INT PRIMARY KEY,
  name VARCHAR(128),
  industry VARCHAR(128),
  size_range VARCHAR(64),
  website VARCHAR(255),
  city_rn INT,
  intro TEXT
) ENGINE=InnoDB;
TRUNCATE TABLE seed_company;

INSERT INTO seed_company(rn,name,industry,size_range,website,city_rn,intro) VALUES
(0,'星环数据','企业服务','500-2000','https://www.transwarp.cn',1,'面向企业的数据平台与数智化解决方案，覆盖数据治理、湖仓一体与实时分析，强调工程落地与长期交付。'),
(1,'云帆科技','互联网','200-500','https://www.yunfan-tech.example',3,'聚焦电商与内容平台的中台能力建设，提供高可用服务、实时计算与增长工具，注重用户体验与效率。'),
(2,'极光智联','物联网','500-2000','https://www.jiguang-iot.example',2,'服务智慧城市与工业互联网，打造设备接入、边缘计算与安全运维体系，项目覆盖多地政企客户。'),
(3,'海豚互娱','游戏','1000+','https://www.haitun-game.example',1,'移动游戏与互动娱乐公司，重视玩法创新与数据驱动运营，持续投入研发与美术制作管线。'),
(4,'清风医疗','医疗健康','200-500','https://www.qingfeng-med.example',0,'医疗信息化与互联网医院解决方案提供商，围绕诊疗流程、运营管理与数据合规持续迭代。'),
(5,'墨迹物流','物流供应链','500-2000','https://www.moji-logistics.example',18,'专注仓配一体与配送调度优化，建设订单、库存、路径规划与司机端系统，强调稳定与成本效率。'),
(6,'智图视觉','人工智能','200-500','https://www.zhitu-vision.example',8,'计算机视觉与多模态应用团队，落地安防、质检与内容理解，强调算法工程化与模型迭代效率。'),
(7,'蓝鲸金融科技','金融科技','2000-10000','https://www.lanjin-fintech.example',6,'面向银行/保险的数字化产品与风控系统，重视审计合规、可追溯与高可用架构。'),
(8,'微光教育','教育','200-500','https://www.weiguang-edu.example',12,'提供在线学习与教务管理平台，关注课程体验、学习数据与教学运营工具，持续快速迭代。'),
(9,'风筝出行','出行','500-2000','https://www.fengzheng-mobility.example',4,'覆盖打车与同城出行业务，建设订单/计价/派单系统与司机生态，强调实时性与风控。'),
(10,'北斗云安','安全','200-500','https://www.beidou-sec.example',0,'企业安全与风控平台，覆盖账号安全、漏洞治理与安全运营，强调攻防思维与工程落地。'),
(11,'青岚内容','内容社区','500-2000','https://www.qinglan-content.example',7,'内容分发与社区平台，重视推荐、审核与增长实验体系，追求体验与效率的平衡。'),
(12,'橙子零售','新零售','200-500','https://www.chengzi-retail.example',5,'围绕门店数字化、会员体系与供应链协同，构建全渠道交易与运营平台。'),
(13,'图灵硬件','智能硬件','200-500','https://www.turing-hw.example',2,'硬件与软件协同研发团队，覆盖设备固件、App与云端平台，强调质量体系与交付节奏。'),
(14,'森海能源','能源','500-2000','https://www.senhai-energy.example',10,'能源企业数字化团队，建设生产运营系统与数据平台，重视稳定性与安全规范。'),
(15,'远望制造','智能制造','2000-10000','https://www.yuanwang-mfg.example',15,'面向工厂的MES/APS与数据采集平台，强调现场落地与跨部门协作能力。'),
(16,'澜星文旅','文旅','200-500','https://www.lanxing-travel.example',14,'文旅平台与票务系统，覆盖商家入驻、履约与客服体系，注重体验与稳定运营。'),
(17,'沐风房产科技','房产','500-2000','https://www.mufang-prop.example',9,'房产交易与经纪人工具平台，重视搜索推荐、交易安全与服务质量。'),
(18,'鲸准招聘','企业服务','200-500','https://www.jingzhun-hr.example',17,'招聘与组织管理SaaS，覆盖简历、面试、入职与组织协作，强调易用性与数据闭环。'),
(19,'旭日科研','科研服务','50-200','https://www.xuri-research.example',13,'科研数据管理与实验流程平台，服务高校与科研机构，注重权限、合规与可追溯。');

-- 插入公司（name 唯一：uk_company_name）
INSERT INTO company(name, industry, size_range, website, address, intro, status, created_at, updated_at, is_deleted)
SELECT sc.name,
       sc.industry,
       sc.size_range,
       sc.website,
       CONCAT(ct.city,'·',ct.district),
       CONCAT(sc.intro, '（', @SEED_TAG, '）'),
       1,
       DATE_SUB(NOW(), INTERVAL (100 + sc.rn*3) DAY),
       DATE_SUB(NOW(), INTERVAL (10 + sc.rn) DAY),
       0
FROM seed_company sc
JOIN seed_city ct ON ct.rn = sc.city_rn
ON DUPLICATE KEY UPDATE updated_at = updated_at;

-- ----------------------------------------------------------
-- 4) Boss 用户（40个，均匀绑定公司；不硬编码 company_id）
-- ----------------------------------------------------------
INSERT IGNORE INTO `user`(
  user_account, password, phone, email, username,
  user_role, company_id, status, profile, created_at, updated_at, is_deleted
)
SELECT
  CONCAT('boss_seed_', LPAD(s.n,3,'0')) AS user_account,
  '9e7b74dc1da0327f18e3a7861929b9bc' AS password,
  CONCAT('13', LPAD(800000000 + s.n, 9, '0')) AS phone,
  CONCAT('boss', LPAD(s.n,3,'0'), '@corp-mail.cn') AS email,
  CONCAT(sn.v, gn.v) AS username,
  'boss' AS user_role,
  NULL AS company_id,
  1 AS status,
  CONCAT(
    '负责团队招聘与面试评估，关注候选人基础功与工程习惯；',
    '日常会参与需求评审与技术方案讨论，强调沟通效率与结果交付；',
    '偏好有项目闭环经验、能快速定位问题并持续学习的同学。 ',
    @SEED_TAG
  ) AS profile,
  DATE_SUB(NOW(), INTERVAL (500 + (s.n % 180)) DAY) AS created_at,
  DATE_SUB(NOW(), INTERVAL (60 + (s.n % 30)) DAY) AS updated_at,
  0 AS is_deleted
FROM seed_seq s
JOIN seed_surname sn ON sn.rn = MOD(s.n, @sn_cnt)
JOIN seed_given   gn ON gn.rn = MOD(s.n*7, @gn_cnt)
WHERE s.n <= 40;

-- 绑定 Boss 到公司（通过 company.name，自然键）
UPDATE `user` u
JOIN (
  SELECT
    CONCAT('boss_seed_', LPAD(s.n,3,'0')) AS boss_account,
    sc.name AS company_name
  FROM seed_seq s
  JOIN seed_company sc ON sc.rn = MOD(((s.n-1) DIV 2), 20)
  WHERE s.n <= 40
) bm ON bm.boss_account = u.user_account
JOIN company c ON c.name = bm.company_name
SET u.company_id = c.id
WHERE u.user_role='boss' AND (u.company_id IS NULL OR u.company_id=0);

-- ----------------------------------------------------------
-- 5) 岗位模板（10类） -> 每家公司 10 个岗位，共 200
-- ----------------------------------------------------------
CREATE TEMPORARY TABLE IF NOT EXISTS seed_job_tpl(
  rn INT PRIMARY KEY,
  title VARCHAR(128),
  job_type VARCHAR(64),
  salary_min INT,
  salary_max INT,
  job_desc TEXT,
  req TEXT
) ENGINE=InnoDB;
TRUNCATE TABLE seed_job_tpl;

INSERT INTO seed_job_tpl(rn,title,job_type,salary_min,salary_max,job_desc,req) VALUES
(0,'Java后端工程师','全职',20000,42000,
'你将参与核心业务服务的设计与开发，负责接口设计、数据建模与性能优化；与产品、测试协作推进版本交付，保障线上稳定性与可观测性；有机会参与中台建设与治理体系落地。',
'熟悉Java与常用框架（Spring Boot等），理解HTTP/SQL基础；具备MySQL索引/事务/慢SQL优化经验；了解Redis/消息队列/微服务基本理念，能写出可维护的代码并进行单元测试。'),
(1,'前端工程师（Web）','全职',18000,38000,
'负责Web端功能迭代与工程化建设，推进组件化、性能优化与可用性提升；与后端联调并落地埋点、监控与灰度发布；参与页面体验优化与跨端适配。',
'熟悉Vue/React任一技术栈，掌握TypeScript与常见构建工具；理解浏览器渲染与性能指标，有定位线上问题经验；具备良好代码规范与组件设计能力，能编写单元测试更佳。'),
(2,'数据分析师','全职',16000,32000,
'围绕业务目标搭建指标体系与数据看板，进行专题分析并输出可执行建议；参与A/B实验设计、效果评估与用户行为洞察；与产品/运营协作推动策略落地与复盘。',
'熟练使用SQL进行数据抽取与建模，掌握Python进行分析与可视化；理解常见统计概念与实验方法；具备结构化表达与沟通能力，能够将结论转化为行动方案。'),
(3,'测试开发工程师','全职',16000,30000,
'建设自动化测试与质量平台，完善用例体系、回归流程与持续集成；负责关键链路的稳定性保障、性能压测与缺陷分析；推动研发流程优化与质量度量。',
'熟悉Java/Python任一语言，了解常见测试框架与CI/CD；理解接口/性能/安全基础测试方法；具备问题定位能力与良好沟通推动能力，能持续输出可复用工具。'),
(4,'算法工程师（推荐/排序）','全职',30000,60000,
'参与推荐/搜索相关模型训练与效果迭代，负责特征工程、样本构建与线上服务部署；与业务一起定义指标并进行实验验证；推动模型工程化与成本优化。',
'熟悉机器学习基础与常用算法，掌握Python与至少一种深度学习框架；了解特征工程、评估指标与线上A/B；有大规模数据处理或模型线上化经验更佳。'),
(5,'产品经理','全职',18000,35000,
'负责需求调研、PRD撰写与版本规划，推动研发/设计/测试协作交付；跟踪数据与用户反馈，持续迭代产品体验；参与跨部门沟通协调与目标拆解。',
'具备清晰的结构化思维与沟通能力，能把复杂问题拆解为可执行需求；理解数据分析与基本技术概念；有互联网产品经验或B端协作经验更佳。'),
(6,'DevOps工程师','全职',20000,40000,
'负责服务部署、监控告警与自动化运维，完善发布流程与故障应急预案；推进容器化与基础设施规范化；提升系统可用性、可观测性与发布效率。',
'熟悉Linux与网络基础，掌握Docker/K8s或相关工具；了解监控日志体系（Prometheus/ELK等）；具备脚本能力与故障排查经验，安全意识强。'),
(7,'运营专员','全职',12000,22000,
'负责内容/活动/社群运营，制定节奏并跟踪效果；参与用户增长与留存策略设计，沉淀运营方法论；与产品协作推动功能优化与活动落地。',
'具备较强的数据敏感度与执行力，能独立推进活动方案与复盘；熟悉常见运营工具与渠道；沟通协作好，能在不确定中快速迭代。'),
(8,'后端实习生（Java）','实习',3000,8000,
'参与业务模块开发与Bug修复，学习工程规范与协作流程；在导师指导下完成接口开发、单测与文档；有机会参与小型性能优化与工具建设。',
'计算机相关专业优先，具备Java/SQL基础；能使用Git并理解基本开发流程；学习能力强，愿意写文档和单元测试，有项目/竞赛经历更佳。'),
(9,'前端实习生（Web）','实习',3000,8000,
'参与页面功能开发与样式优化，完善组件与工程化配置；在导师指导下完成需求拆解与联调；沉淀可复用的组件/工具。',
'具备HTML/CSS/JavaScript基础，了解Vue/React任一框架；愿意在代码规范与工程化上投入；具备沟通协作意识与持续学习能力。');

-- 插入岗位（非唯一表：用 NOT EXISTS 防重复）
INSERT INTO job_posting(
  boss_id, company_id, title, location, job_type,
  salary_min, salary_max, description, requirement,
  status, publish_at, created_at, updated_at, is_deleted
)
SELECT
  (SELECT ub.id
     FROM `user` ub
     WHERE ub.user_role='boss' AND ub.company_id=c.id
     ORDER BY ub.id
     LIMIT 1) AS boss_id,
  c.id AS company_id,
  jt.title,
  ct.city AS location,
  jt.job_type,
  jt.salary_min,
  jt.salary_max,
  CONCAT(
    jt.job_desc,
    '\n\n福利：五险一金、年度体检、弹性办公、技术分享、带薪年假；',
    '\n团队：代码评审+文档规范，强调稳定性与可维护性。',
    '\n（', @SEED_TAG, '|COMPANY=', c.name, '|TPL=', LPAD(jt.rn+1,2,'0'), '）'
  ) AS description,
  CONCAT(
    jt.req,
    '\n\n加分项：开源贡献/竞赛经历/英文技术文档能力/具备从0到1的项目闭环。'
  ) AS requirement,
  1 AS status,
  DATE_SUB(NOW(), INTERVAL (MOD((sc.rn*7 + jt.rn*3), 56) + 5) DAY) AS publish_at,
  DATE_SUB(NOW(), INTERVAL (MOD((sc.rn*7 + jt.rn*3), 56) + 5) DAY) AS created_at,
  DATE_ADD(
    DATE_SUB(NOW(), INTERVAL (MOD((sc.rn*7 + jt.rn*3), 56) + 5) DAY),
    INTERVAL MOD(jt.rn, 5) DAY
  ) AS updated_at,
  0 AS is_deleted
FROM company c
JOIN seed_company sc ON sc.name = c.name
JOIN seed_city ct ON ct.rn = sc.city_rn
JOIN seed_job_tpl jt
WHERE NOT EXISTS (
  SELECT 1
  FROM job_posting j
  WHERE j.company_id = c.id
    AND j.title = jt.title
    AND j.location = ct.city
    AND j.job_type = jt.job_type
    AND j.salary_min = jt.salary_min
    AND j.salary_max = jt.salary_max
    AND j.is_deleted = 0
);

-- ----------------------------------------------------------
-- 6) 求职者用户（1000个）：bulkuser_0001 ~ bulkuser_1000
-- ----------------------------------------------------------
INSERT IGNORE INTO `user`(
  user_account, password, phone, email, username,
  user_role, company_id, status, profile, created_at, updated_at, is_deleted
)
SELECT
  CONCAT('bulkuser_', LPAD(s.n,4,'0')) AS user_account,
  '9e7b74dc1da0327f18e3a7861929b9bc' AS password,
  CONCAT('13', LPAD(100000000 + s.n, 9, '0')) AS phone,
  CONCAT('u', LPAD(s.n,4,'0'), '@mailbox.cn') AS email,
  CONCAT(sn.v, gn.v) AS username,
  'user' AS user_role,
  NULL AS company_id,
  1 AS status,
  CONCAT(
    '（', @SEED_TAG, '）',
    ' 求职意向：', tk.track, '；',
    '技术关键词：', tk.keywords, '；',
    '期望城市：', ct.city, '；',
    '经验：', CASE
              WHEN MOD(s.n,10)=0 THEN '应届/实习经历'
              WHEN MOD(s.n,10)=1 THEN '1-3年'
              WHEN MOD(s.n,10)=2 THEN '3-5年'
              ELSE '2-4年'
            END, '；',
    '个人特点：学习快、沟通清晰、能把问题拆解并推进到落地，重视代码规范与复盘。 ',
    @SEED_TAG
  ) AS profile,
  DATE_SUB(NOW(), INTERVAL (120 + MOD(s.n, 720)) DAY) AS created_at,
  DATE_SUB(NOW(), INTERVAL (5 + MOD(s.n, 60)) DAY) AS updated_at,
  0 AS is_deleted
FROM seed_seq s
JOIN seed_surname sn ON sn.rn = MOD(s.n*3, @sn_cnt)
JOIN seed_given   gn ON gn.rn = MOD(s.n*11, @gn_cnt)
JOIN seed_track   tk ON tk.rn = MOD(s.n, @tk_cnt)
JOIN seed_city    ct ON ct.rn = MOD(s.n*5, @ct_cnt);

-- ----------------------------------------------------------
-- 7) 简历：每个求职者至少 1 份默认简历（summary>=50, content>=200）
-- ----------------------------------------------------------
INSERT INTO resume(
  user_id, resume_title, is_default, summary, content, attachment_url,
  created_at, updated_at, is_deleted
)
SELECT
  u.id,
  CONCAT(u.username, ' - 默认简历') AS resume_title,
  1 AS is_default,
  CONCAT(
    '候选人概述：', u.username, '，目标方向为', tk.track,
    '，具备', CASE WHEN MOD(u.id,4)=0 THEN '扎实的基础与良好工程意识'
                  WHEN MOD(u.id,4)=1 THEN '较强的数据分析与问题拆解能力'
                  WHEN MOD(u.id,4)=2 THEN '前后端协作经验与交付意识'
                  ELSE '快速学习与持续复盘习惯' END,
    '；期望在', ct.city, '长期发展，重视团队协作与技术成长。 ', @SEED_TAG
  ) AS summary,
  CONCAT(
    '教育背景：\n',
    '- 本科：计算机/软件/信息相关专业（2019-2023），核心课程：数据结构、数据库、操作系统、计算机网络。\n\n',
    '工作/实习经历：\n',
    '- 参与业务需求评审、接口对齐与版本迭代，能在不确定中快速定位关键问题并推动落地。\n',
    '- 熟悉基础开发流程：需求->开发->自测->联调->灰度->上线->复盘，重视文档与可维护性。\n\n',
    '项目经历：\n',
    '- 项目A：订单/内容/会员模块（负责接口设计、数据建模、缓存与慢SQL优化；支持高峰期稳定运行）。\n',
    '- 项目B：数据分析/指标看板（使用SQL+Python完成指标体系搭建与自动化报表，支持业务决策）。\n',
    '- 项目C：质量与自动化（补齐单测、接口自动化、CI流水线，提升回归效率与稳定性）。\n\n',
    '技能清单：\n',
    '- ', tk.keywords, '；\n',
    '- 通用能力：沟通协作、问题定位、复盘总结、文档表达。\n\n',
    '自我评价：\n',
    '对结果负责，愿意把事情做“闭环”；遇到问题先定位根因再优化方案；能接受节奏与压力，持续学习新技术并落地到项目中。\n',
    '（', @SEED_TAG, '）'
  ) AS content,
  CASE
    WHEN MOD(u.id,10)=0 THEN CONCAT('https://cdn.example.com/resume/', u.user_account, '.pdf')
    ELSE NULL
  END AS attachment_url,
  u.created_at AS created_at,
  u.updated_at AS updated_at,
  0 AS is_deleted
FROM `user` u
JOIN seed_track tk ON tk.rn = MOD(u.id, @tk_cnt)
JOIN seed_city  ct ON ct.rn = MOD(u.id*3, @ct_cnt)
WHERE u.user_role='user'
  AND u.user_account LIKE 'bulkuser_%'
  AND NOT EXISTS (
    SELECT 1 FROM resume r
    WHERE r.user_id = u.id AND r.is_default=1 AND r.is_deleted=0
  );

-- 额外简历（约 30% 用户多一份非默认）
INSERT INTO resume(
  user_id, resume_title, is_default, summary, content, attachment_url,
  created_at, updated_at, is_deleted
)
SELECT
  u.id,
  CONCAT(u.username, ' - 投递版简历') AS resume_title,
  0 AS is_default,
  CONCAT('投递版摘要：突出与岗位匹配的关键经验与项目闭环，强调可量化结果与协作推进能力。 ', @SEED_TAG) AS summary,
  CONCAT(
    '核心亮点：\n',
    '- 负责过关键模块交付，能独立完成需求拆解、接口设计与联调上线。\n',
    '- 有性能与稳定性优化经验：慢SQL分析、缓存策略、监控告警与故障复盘。\n',
    '- 注重工程质量：代码规范、单元测试、文档与可维护性。\n\n',
    '项目补充：\n',
    '- 需求背景、方案选择、数据指标与上线效果复盘，能用数据证明价值。\n',
    '- 跨团队沟通：产品/测试/运营协作推进，能对齐目标并落地执行。\n',
    '（', @SEED_TAG, '）'
  ) AS content,
  NULL AS attachment_url,
  DATE_SUB(u.created_at, INTERVAL 5 DAY) AS created_at,
  u.created_at AS updated_at,
  0 AS is_deleted
FROM `user` u
WHERE u.user_role='user'
  AND u.user_account LIKE 'bulkuser_%'
  AND MOD(u.id, 10) IN (1,2,3)
  AND NOT EXISTS (
    SELECT 1 FROM resume r
    WHERE r.user_id = u.id AND r.is_default=0 AND r.is_deleted=0
      AND r.resume_title LIKE '%投递版%'
  );

-- ----------------------------------------------------------
-- 8) 投递：1000 条（每个 bulkuser_0001..1000 各投 1 个岗位）
--    - boss_id 必须等于岗位的 boss_id
--    - resume_id 必须属于该 user
--    - applied_at：近 30~45 天分布；updated_at >= applied_at
-- ----------------------------------------------------------
CREATE TEMPORARY TABLE IF NOT EXISTS job_index (
  id BIGINT UNSIGNED NOT NULL PRIMARY KEY,
  rn INT NOT NULL
) ENGINE=MEMORY;
TRUNCATE TABLE job_index;

SET @rn := -1;
INSERT INTO job_index(id, rn)
SELECT id, (@rn := @rn + 1) AS rn
FROM job_posting
WHERE is_deleted=0
ORDER BY id;

SET @job_cnt := (SELECT COUNT(*) FROM job_index);

-- 若岗位为空则不插入投递（保护）
INSERT IGNORE INTO application(
  user_id, resume_id, job_id, boss_id, status, applied_at, updated_at, is_deleted
)
SELECT
  u.id AS user_id,
  (SELECT r.id
     FROM resume r
     WHERE r.user_id=u.id AND r.is_deleted=0
     ORDER BY r.is_default DESC, r.id ASC
     LIMIT 1) AS resume_id,
  j.id AS job_id,
  j.boss_id AS boss_id,
  CASE
    WHEN MOD(s.n, 20)=0 THEN 1   -- 撤回
    WHEN MOD(s.n, 17)=0 THEN 2   -- 过期
    WHEN MOD(s.n, 9)=0  THEN 3   -- 面试中
    WHEN MOD(s.n, 33)=0 THEN 4   -- 已录用
    WHEN MOD(s.n, 25)=0 THEN 5   -- 已拒绝
    ELSE 0                       -- 已投递
  END AS status,
  DATE_ADD(
    DATE_SUB(NOW(), INTERVAL (MOD(s.n*7,16) + 30) DAY),
    INTERVAL MOD(s.n*13, 600) MINUTE
  ) AS applied_at,
  DATE_ADD(
    DATE_ADD(
      DATE_SUB(NOW(), INTERVAL (MOD(s.n*7,16) + 30) DAY),
      INTERVAL MOD(s.n*13, 600) MINUTE
    ),
    INTERVAL MOD(s.n, 6) DAY
  ) AS updated_at,
  0 AS is_deleted
FROM seed_seq s
JOIN `user` u
  ON u.user_account = CONCAT('bulkuser_', LPAD(s.n,4,'0'))
 AND u.user_account LIKE 'bulkuser_%'
JOIN job_index ji
  ON ji.rn = MOD(s.n-1, @job_cnt)
JOIN job_posting j
  ON j.id = ji.id
WHERE @job_cnt > 0;

-- ----------------------------------------------------------
-- 9) Boss 处理：对约 80% 的投递生成决策（每投递最多 1 条）
--    - decided_at 在 applied_at 后 0~7 天
-- ----------------------------------------------------------
INSERT INTO boss_application_decision(
  application_id, boss_id, decision, stage, note, decided_at, updated_at, is_deleted
)
SELECT
  a.id AS application_id,
  a.boss_id,
  CASE
    WHEN a.status IN (5) THEN 2                 -- 已拒绝
    WHEN a.status IN (1,2) THEN 3               -- 撤回/过期 -> 待定
    WHEN MOD(a.id, 9)=0 THEN 3                  -- 少量待定
    ELSE 1                                      -- 进入流程
  END AS decision,
  CASE
    WHEN a.status=4 THEN 3                      -- Offer
    WHEN a.status=3 THEN 2                      -- 面试
    WHEN MOD(a.id, 6)=0 THEN 1                  -- 邀面
    WHEN MOD(a.id, 5)=0 THEN 0                  -- 筛选
    ELSE 1
  END AS stage,
  CONCAT(
    '处理备注：',
    CASE
      WHEN a.status=5 THEN '匹配度不足，建议后续补齐项目深度与量化结果。'
      WHEN a.status=4 THEN '综合表现优秀，进入Offer流程并沟通入职时间。'
      WHEN a.status=3 THEN '安排面试流程，重点考察基础、项目细节与协作能力。'
      WHEN a.status IN (1,2) THEN '候选人状态变化，暂挂起并记录。'
      ELSE '初筛通过，进入下一阶段跟进。'
    END,
    '（', @SEED_TAG, '）'
  ) AS note,
  DATE_ADD(a.applied_at, INTERVAL MOD(a.id, 8) DAY) AS decided_at,
  DATE_ADD(a.applied_at, INTERVAL MOD(a.id, 8) DAY) AS updated_at,
  0 AS is_deleted
FROM application a
JOIN `user` u ON u.id = a.user_id AND u.user_account LIKE 'bulkuser_%'
WHERE MOD(a.id, 5) <> 0  -- 约 80% 生成决策
  AND NOT EXISTS (
    SELECT 1 FROM boss_application_decision d
    WHERE d.application_id = a.id
  );

-- ----------------------------------------------------------
-- 10) 面试安排：对部分“邀面/面试阶段”的投递生成面试（未来 2~10 天）
-- ----------------------------------------------------------
INSERT INTO interview(
  application_id, boss_id, user_id,
  interview_time, mode, location, status, note,
  created_at, updated_at, is_deleted
)
SELECT
  a.id AS application_id,
  a.boss_id,
  a.user_id,
  DATE_ADD(
    DATE_ADD(NOW(), INTERVAL (MOD(a.id, 9) + 2) DAY),
    INTERVAL (MOD(a.id, 8) + 9) HOUR
  ) AS interview_time,
  CASE WHEN MOD(a.id, 3)=0 THEN 1 ELSE 0 END AS mode,
  CASE
    WHEN MOD(a.id, 3)=0 THEN CONCAT(c.address, ' ', '（', c.name, '）')
    ELSE CONCAT('https://meeting.example.com/', a.id, '?pwd=', LPAD(MOD(a.id*97,10000),4,'0'))
  END AS location,
  CASE WHEN MOD(a.id, 4)=0 THEN 0 ELSE 1 END AS status,
  CONCAT(
    '面试说明：请准备自我介绍与最近项目的架构、难点与取舍；',
    '线上请提前 10 分钟入会并测试网络设备。 ',
    '（', @SEED_TAG, '）'
  ) AS note,
  NOW() AS created_at,
  NOW() AS updated_at,
  0 AS is_deleted
FROM boss_application_decision d
JOIN application a ON a.id = d.application_id
JOIN job_posting j ON j.id = a.job_id
JOIN company c ON c.id = j.company_id
JOIN `user` u ON u.id = a.user_id AND u.user_account LIKE 'bulkuser_%'
WHERE d.decision = 1
  AND d.stage >= 1
  AND MOD(a.id, 2)=0     -- 约 50% 生成面试
  AND NOT EXISTS (
    SELECT 1 FROM interview i
    WHERE i.application_id = a.id AND i.is_deleted=0
  );

COMMIT;

-- 可选：快速检查（执行后你也可以手动跑）
-- SELECT COUNT(*) FROM `user` WHERE profile LIKE '%SEED_TAG:bulk_real_v1%';
-- SELECT COUNT(*) FROM company WHERE intro LIKE '%SEED_TAG:bulk_real_v1%';
-- SELECT COUNT(*) FROM job_posting WHERE description LIKE '%SEED_TAG:bulk_real_v1%';
-- SELECT COUNT(*) FROM resume WHERE content LIKE '%SEED_TAG:bulk_real_v1%';
-- SELECT COUNT(*) FROM application a JOIN `user` u ON u.id=a.user_id WHERE u.profile LIKE '%SEED_TAG:bulk_real_v1%';
-- SELECT COUNT(*) FROM boss_application_decision d JOIN application a ON a.id=d.application_id JOIN `user` u ON u.id=a.user_id WHERE u.profile LIKE '%SEED_TAG:bulk_real_v1%';
-- SELECT COUNT(*) FROM interview i JOIN application a ON a.id=i.application_id JOIN `user` u ON u.id=a.user_id WHERE u.profile LIKE '%SEED_TAG:bulk_real_v1%';
