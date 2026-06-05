# Day05 日志（2026-06-05）

**完成内容：**

### 数据层全面修复

- House 实体补全 `area`、`rentPerM2` 字段
- HousingRecord 补全题目要求的 7 个冗余字段：户主、部门、职称、家庭人口、房号、住房面积、月租金
- Application 新增 `originalHouseArea`（调房用）
- 新建 `rent` 房租表及完整 Entity/Mapper/Service/Controller
- 补全所有 Mapper 缺失的 CRUD SQL（getById、update、insert、deleteById）
- UserMapper 修复 updateUser 中错误的 `status` 字段
- MyBatis 增加驼峰自动映射配置 `map-underscore-to-camel-case`
- 建库脚本改为 `DROP DATABASE IF EXISTS` 彻底重建，避免重复执行报错
- 创建 `migration_v2.sql` 旧库增量迁移脚本

### 核心业务逻辑重写

- 重写 `ApplicationService.approve()` 分房流程：
  - 分数计算（部门 15-30 + 职称 10-50 + 家庭人数×5）
  - 阈值校验（查住房标准表，分数不达标直接拒绝）
  - 好房优先分配（空房按面积从大到小排列）
  - 创建住房记录（含所有冗余快照字段）
  - 计算房租写入 rent 表
- 完善 `release()` 退房：删除 housing_record + rent + 释放房屋
- 完善 `transfer()` 调房：释放原房 + 分配新房
- 新增 `insert()` 申请提交，含业务校验：
  - 分房：已有住房的用户禁止申请
  - 调房/退房：没有住房的用户禁止申请

### 前端完善

- HouseList：显示面积、每平米租金、月租金列
- HousingRecordList：显示完整的 10 列住房信息
- Stats：新增按面积查阈值、按房号查租金、租金一览表
- ApplicationList 全面升级：
  - 新增申请提交表单（支持分房/调房/退房三种类型，字段动态切换）
  - 申请人按类型过滤（分房→无房者，调房退房→有房者）
  - 部门改为 13 个固定选项的下拉框
  - 调房/退房自动匹配用户当前住房
  - 类型和状态彩色标签显示

### 文档整理

- 数据库设计报告合并至 README
- 删除项目设计说明书
- 新增 `使用指南.md`（环境搭建、操作步骤、评分公式、API 速查、FAQ）

### 评分公式（最终版）

| 维度 | 分值 |
|---|---|
| 计算机/电子信息学院 | 30 |
| 数理化生学院 | 25 |
| 经管/法学/外语/马克思学院 | 20 |
| 后勤/图书馆/校机关 | 15 |
| 教授/副教授/讲师/助教/其他 | 50/40/30/20/10 |
| 家庭人数 | 每人 5 分 |

**问题记录：**

- MySQL 不支持 `CREATE INDEX IF NOT EXISTS`，建库脚本改为先 DROP DATABASE
- MySQL 不支持 `ADD COLUMN IF NOT EXISTS`，迁移脚本最终改为纯 ALTER TABLE
- curl 命令行测试时中文编码导致 400 错误，前端浏览器不受影响
- MyBatis 默认不转换驼峰字段，`rent_per_m2` → `rentPerM2` 映射为 null，加配置解决
- 后端重启时端口被旧进程占用，需要先 kill Java 进程

**提交记录：**

- `1ff502b` 修复数据层缺陷
- `0c4cc3b` 重写分房业务逻辑，新增使用指南
- `f76807f` 整合文档至 README
- `caf8902` 新增申请提交表单
- `3f6de6b` 申请表单业务校验
- `dd2ee24` 部门改为下拉选择

**次日计划：**

- 完善调房逻辑（确定住房等级 → 找同等级空房）
- 前端住房标准页增加编辑/删除功能
- 前端房屋/用户页增加编辑/删除按钮
