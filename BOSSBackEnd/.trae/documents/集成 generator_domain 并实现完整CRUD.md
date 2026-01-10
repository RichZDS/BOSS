## 目标
- 将 f:\Code\BOSS\src\main\java\generator\domain 下的 5 个实体迁移到项目标准位置（com.zds.boss.model.entity），并为每个实体完成基础 CRUD 的 Controller、Service、ServiceImpl、Mapper（含 XML 根据需要）、DTO 与 VO。
- 统一权限与风格：写操作使用 @AuthCheck(mustRole = "admin")；分页查询与条件封装复用 PageRequest；逻辑删除字段与全局配置对齐。

## 待集成实体
- 源文件：
  - [Boss.java](file:///f:/Code/BOSS/src/main/java/generator/domain/Boss.java)
  - [JobPosting.java](file:///f:/Code/BOSS/src/main/java/generator/domain/JobPosting.java)
  - [Application.java](file:///f:/Code/BOSS/src/main/java/generator/domain/Application.java)
  - [Interview.java](file:///f:/Code/BOSS/src/main/java/generator/domain/Interview.java)
  - [BossApplicationDecision.java](file:///f:/Code/BOSS/src/main/java/generator/domain/BossApplicationDecision.java)
- 目标包：com.zds.boss.model.entity（命名规范与现有 Admin/User/Resume/Company 保持一致）
- 字段策略：
  - 表名沿用 @TableName；字段默认开启 map-underscore-to-camel-case 映射。
  - 逻辑删除：如 Boss 包含 isDeleted/deletedAt，重命名为 isDelete 并添加 @TableLogic 以匹配全局配置（logic-delete-field: isDelete）。

## 每个实体的配套层次
- Mapper：XxxMapper（BaseMapper），必要时生成 generator/mapper XML（BaseResultMap + Base_Column_List）。
- Service：XxxService（IService + 扩展方法）：add、update、delete、getVO、getVOList、getQueryWrapper、listVOByPage。
- Impl：XxxServiceImpl（ServiceImpl + 业务校验与封装）。
- DTO：XxxAddRequest、XxxUpdateRequest、XxxQueryRequest（继承 PageRequest）。
- VO：XxxVO（仅展示与安全字段）。
- Controller：XxxController，路径与风格与已有模块一致，写操作加 @AuthCheck(mustRole = "admin")。

## 控制器端点设计
- Boss（招聘方账号）
  - POST /boss/add、/boss/update、/boss/delete（需 admin）
  - GET /boss/get、/boss/get/vo
  - POST /boss/list/page/vo（分页查询）
- JobPosting（岗位）
  - POST /job/add、/job/update、/job/delete（需 admin）
  - GET /job/get、/job/get/vo
  - POST /job/list/page/vo
- Application（投递记录）
  - POST /application/add（登录用户提交）
  - POST /application/update、/application/delete（本人或管理员）
  - GET /application/get、/application/get/vo
  - POST /application/list/page/vo（管理员查看；或限制仅本人）
- Interview（面试安排）
  - POST /interview/add、/interview/update、/interview/delete（需 admin）
  - GET /interview/get、/interview/get/vo
  - POST /interview/list/page/vo
- BossApplicationDecision（投递处理）
  - POST /decision/add、/decision/update、/decision/delete（需 admin）
  - GET /decision/get、/decision/get/vo
  - POST /decision/list/page/vo

## 权限与校验
- 写操作统一注解：@AuthCheck(mustRole = "admin")。
- 针对 Application 的本人操作：
  - add 时绑定 loginUser.id → userId；
  - update/delete 时校验记录 userId 与当前用户一致或 isAdmin(loginUser)（复用 UserUtils）。
- 被封禁用户（status==0）在 AOP 层直接拒绝（已在 AuthInterceptor）。

## 详细实施步骤
1. 迁移与修正实体：
   - 将 5 个实体移动到 com.zds.boss.model.entity 并修正包名。
   - Boss 增加 @TableLogic 并将 isDeleted → isDelete，保持 deletedAt 字段。
2. 为每个实体创建 Mapper 接口与（按需）XML 映射，遵循 Company/Resume 的模式。
3. 为每个实体新增 DTO（Add/Update/Query）与 VO。
4. 编写 Service 接口与实现：
   - 基础校验（空参、ID 合法性）、BeanUtil 复制、QueryWrapper 条件构造、分页封装。
5. 编写 Controller：
   - 统一 CORS、路径前缀与注解风格；写操作加 @AuthCheck；Application 的本人校验走 UserService + UserUtils。
6. 编译与运行验证：
   - mvn compile；启动后用简单请求验证 CRUD 通路与 AOP 权限。
7. 文档与代码参考补充：
   - 在 README 或接口注释中标注各字段语义与权限规则。

## 验收标准
- 五个实体在标准包结构下具备可用的 CRUD 控制器与服务层，分页与条件查询可用。
- 写操作权限正确，封禁用户在 AOP 层拒绝；Application 本人校验生效。
- 编译与运行均通过；与现有模块无冲突（命名、包结构、风格统一）。