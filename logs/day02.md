# Day02 日志（2026-06-02）

## 一、今日完成内容

### 1. 数据库操作

- 成功连接上mysql数据库，一切顺利。

![6.2.1](.\assets\6.2.1.png)



- 使用命令行建新库以及初始的表，问题遇到一些，但是不大，只是些忘记打 $";"$ 写错单词之类的小问题。

![6.2.2](.\assets\6.2.2.png)



- 展示结果表如下。

![6.2.3](.\assets\6.2.3.png)



后来在我准备继续做的时候，一位好心的学长看到我还在使用简陋的命令行操作，于是丢下了一个好用且免费的软件并头也不回的离开了（%%%）

- Navicat展示，后面写mysql估计会方便不少

![6.2.4](.\assets\6.2.4.png)

将数据库结构整理为 SQL 脚本文件: 
house_db.sql 用于记录完整建库、建表、插入数据过程便于项目复现与提交.

---

### 2. 前端项目初始化

- 完成了 Vue + Vite 前端项目基础结构搭建

- 文件结构：
  
  - `frontend/index.html`：网页入口
  - `frontend/src/main.js`：Vue 程序入口
  - `frontend/src/App.vue`、`frontend/src/components/HelloWorld.vue`：基础组件
  - `frontend/src/assets/`：图片资源
  - `frontend/src/style.css`：公共样式
  - `frontend/public/`：网页图标资源
  - `frontend/package.json` / `package-lock.json`：npm 配置
  - `.gitignore` / `.vscode/extensions.json`：项目配置和 VS Code 推荐插件

---

## 二、收获总结

- 熟悉 Git 的核心流程（add / status / commit / push）
  
  ```
  git add xxx //讲xxx文件提交到缓存区
  git status //查看缓存区状态
  git commit -m "xxx"//提交到本地仓库并记录这次操作
  git push //将本次更新提交到github（远程仓库）
  ```

- 明确课设项目结构

- 完成前端基础搭建（Vue + Vite）

- 完成数据库 SQL 脚本规范化

---

## 三、下一步计划

- 开始 Spring Boot 后端搭建
- 连接 MySQL 数据库
- 实现第一个查询接口（user表）
