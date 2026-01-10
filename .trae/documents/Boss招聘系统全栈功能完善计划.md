# 系统功能完善与优化计划

## 1. 修复与优化
- **修复登录跳转**: 检查 NavBar 中的“登录”按钮，确保使用 Vue Router 进行跳转，避免页面刷新。
- **权限修正**:
  - `CompanyController`: 放宽 `add` 和 `update` 权限，允许 **Boss** 创建和修改自己的公司。
  - `ApplicationController`: 为 `listApplicationVOByPage` 添加权限控制，允许 Admin 查看所有，但限制 User/Boss 只能看相关的。
  - `UserController`: 确认 `updateUser` 逻辑，确保 User/Boss 也能更新自己的信息（目前仅 Admin）。

## 2. 后端逻辑增强
- **Boss 创建公司**:
  - 修改 `addCompany`：允许 Boss 调用。创建成功后，自动将 Boss 的 `company_id` 更新为新公司的 ID。
  - 限制：如果 Boss 已有公司，禁止创建新公司（或提示只能创建一个）。
- **管理员查询投递**:
  - 修改 `listApplicationVOByPage`：如果是 Admin，允许无条件查询所有；如果是 User/Boss，强制追加 `userId`/`bossId` 过滤条件。

## 3. 前端新功能开发
- **Boss 公司管理**:
  - 在 Boss 工作台增加“我的公司”模块。
  - 如果未绑定公司，显示“创建公司”表单。
  - 如果已绑定，显示公司详情及“编辑”按钮。
- **职位发布管理**:
  - 在 Boss 工作台增加“发布职位”入口。
  - 创建 `JobEdit.vue`：用于发布新职位和编辑现有职位。
- **管理员投递查询**:
  - 创建 `AdminApplications.vue`：仅管理员可见，展示全站投递记录，支持筛选。
- **个人信息管理**:
  - 创建 `Profile.vue`：允许 User/Boss 修改昵称、密码、头像等信息。

## 4. 主页改版
- 改造 `HomeView.vue`：
  - 展示“最新职位”列表（复用 Job 列表逻辑，或取前 6-8 条）。
  - 增加“热门公司”板块（可选）。

## 执行步骤
1. **后端**: 修改 `CompanyController` (Boss权限), `ApplicationController` (列表权限), `UserController` (更新权限)。
2. **前端**: 修复登录链接 -> 开发 Boss 公司模块 -> 开发职位发布模块 -> 开发管理员投递查询 -> 改造主页 -> 开发个人中心。