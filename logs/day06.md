# Day06 日志（2026-06-09）

**完成内容：**

### 前端全面美化
- 深色侧边栏 → 顶部玻璃导航 + 星空粒子背景 + 动态流星
- 全局 CSS 设计系统重写：毛玻璃卡片（backdrop-filter blur）、蓝色主题配色
- 六页面全部重写：统一样式、弹窗编辑、彩色标签
- 动态效果：卡片悬停浮起、按钮光晕、导航下滑线、统计数字脉冲

### 登录系统
- user 表新增 password 字段，BCrypt 加密存储
- AuthController：POST /auth/login、/auth/register、/auth/send-code
- 登录/注册页面：邮箱验证码注册，支持邮箱或姓名登录
- 角色权限：管理员全部菜单，住户仅「首页」「我的申请」「我的住房」
- EmailService：QQ 邮箱 SMTP 真实发送验证码（secret.properties 排除 Git）

### 核心功能完善
- 调房逻辑：findHousesInGrade() 按面积范围匹配同等级空房
- 分房/调房/退房返回具体结果消息（成功/失败/无房提示）
- 住房标准改为 min_area~max_area 范围显示
- 住房分配单：GET /records/{id}/receipt + 打印弹窗
- 房屋管理增删改功能补全（POST/DELETE 端点）
- 统计查询 housingSummary 数组/对象取值修复

### 新增首页仪表盘
- Dashboard.vue：登录默认页，汇总统计 + 快捷入口 + 最近入住
- 按角色过滤快捷按钮

### 数据
- 用户 10 人（张三 admin + 9 位教职工）
- 房屋 30 套（A-E栋，30~150㎡）
- 住房标准 4 级（0-60/60-80/80-120/120-160㎡）

### 修复
- 房屋 PUT 端点路径不匹配
- 管理员角色 owner→admin
- 前端页面闪烁

**提交记录：** 29 commits pushed

**次日计划：** 更新 README，系统测试
