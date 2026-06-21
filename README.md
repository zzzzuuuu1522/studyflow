# StudyFlow 学习任务管理系统

StudyFlow 是一个面向个人学习计划管理的全栈练习项目。它将任务规划、状态跟踪、优先级管理与学习时长记录集中在一个简洁的工作台中，并通过统计卡片、每日柱状图和任务排行帮助使用者了解自己的学习投入。

项目采用前后端分离架构，不包含登录功能。后端提供 REST API 并使用 MySQL 持久化数据，前端通过原生 Fetch API 调用后端。

## 核心功能

- 学习任务新增、查询、编辑和删除
- 按任务状态筛选：未开始、进行中、已完成
- 任务状态快速更新
- 高、中、低三级任务优先级与优先级筛选
- 学习记录新增、查询、编辑和删除
- 学习记录按任务及日期范围筛选
- 最近 7 天、本月和全部记录统计
- 今日、本周、本月及当前范围学习时长汇总
- 纯 CSS 每日学习时长柱状图
- 按学习分钟数排序的任务排行
- 删除任务时同步删除关联学习记录
- 统一的参数校验与 JSON 异常响应

## 技术栈

| 层级 | 技术 |
| --- | --- |
| 前端 | Vue 3、Vite、JavaScript、原生 Fetch API、CSS |
| 后端 | Java 21、Spring Boot 3、Spring Web、Spring Data JPA、Validation |
| 数据库 | MySQL 8、MySQL Connector/J |
| 测试 | JUnit 5、Mockito、MockMvc |
| 构建 | Maven Wrapper、npm |

## 项目目录

```text
study-task/
├─ .github/workflows/       # GitHub Actions 持续集成
├─ docs/                    # 架构与截图说明
├─ frontend/                # Vue 3 前端项目
│  ├─ src/components/       # 页面组件
│  ├─ src/services/         # 后端 API 请求模块
│  ├─ package.json
│  └─ vite.config.js
├─ src/main/java/com/example/studytask/
│  ├─ controller/           # REST API 控制器
│  ├─ dto/                  # 请求与响应 DTO
│  ├─ entity/               # JPA 实体与枚举
│  ├─ exception/            # 统一异常处理
│  ├─ repository/           # 数据访问层
│  └─ service/              # 业务逻辑层
├─ src/main/resources/      # Spring Boot 通用及本地示例配置
├─ src/test/                # 后端自动化测试
├─ pom.xml
└─ README.md
```

## 系统架构

浏览器访问 Vue 3 单页应用，前端通过 HTTP/JSON 请求 Spring Boot REST API；后端在 Service 层处理任务、学习记录与统计逻辑，并通过 Spring Data JPA 访问 MySQL。

```text
Vue 3 + Vite（localhost:5173）
              │ HTTP / JSON
              ▼
Spring Boot REST API（localhost:8080）
              │ JPA / JDBC
              ▼
        MySQL 8（study_task）
```

更详细的模块关系见 [系统架构文档](docs/architecture.md)。

## 本地环境要求

- Java 21
- Node.js 20.19+ 或 22.12+
- npm
- MySQL 8

## 本地运行

### 1. 创建数据库

在 MySQL Workbench 或 MySQL 命令行中执行：

```sql
CREATE DATABASE study_task
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
```

JPA 会在后端首次启动时自动创建或增量维护数据表，不需要手动创建 `tasks` 或 `study_records` 表。

### 2. 配置本地数据库连接

复制安全示例文件：

```powershell
Copy-Item src/main/resources/application-local.example.properties `
  src/main/resources/application-local.properties
```

打开本机的 `application-local.properties`，仅将下面的占位符替换为自己的 MySQL 密码：

```properties
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

`application-local.properties` 已被 Git 忽略，请勿提交真实密码。示例文件只保留占位符，可以安全提交。

### 3. 启动后端

Windows PowerShell：

```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=local"
```

macOS / Linux：

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

后端默认地址：`http://localhost:8080`。

### 4. 启动前端

打开另一个终端：

```bash
cd frontend
npm ci
npm run dev
```

前端默认地址：`http://localhost:5173`。

## REST API

### 学习任务 `/api/tasks`

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| POST | `/api/tasks` | 创建任务 |
| GET | `/api/tasks` | 查询任务列表，可选 `status`、`priority` |
| GET | `/api/tasks/{id}` | 查询任务详情 |
| PUT | `/api/tasks/{id}` | 完整更新任务 |
| PATCH | `/api/tasks/{id}/status` | 快速更新任务状态 |
| DELETE | `/api/tasks/{id}` | 删除任务及其学习记录 |

### 学习记录 `/api/study-records`

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| POST | `/api/study-records` | 创建学习记录 |
| GET | `/api/study-records` | 查询记录，可选 `taskId`、`startDate`、`endDate` |
| PUT | `/api/study-records/{id}` | 更新学习记录 |
| DELETE | `/api/study-records/{id}` | 删除学习记录 |

### 统计 `/api/statistics`

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| GET | `/api/statistics/dashboard` | 获取统计面板，可选 `startDate`、`endDate`，默认最近 7 天 |

接口参数错误会返回统一 JSON，包含 `timestamp`、`status`、`message` 和 `path`。

## 测试与构建

运行后端测试：

```powershell
.\mvnw.cmd test
```

macOS / Linux 使用：

```bash
./mvnw test
```

运行前端生产构建：

```bash
cd frontend
npm ci
npm run build
```

项目的 GitHub Actions 会在推送到 `main` 或创建 Pull Request 时自动执行后端测试和前端构建。测试使用 Mockito 与 MockMvc，不依赖本地 MySQL 或任何数据库密码。

## 页面截图

项目目前不提交或引用未经确认的页面图片。截图命名、隐私检查和后续添加方式见 [截图目录说明](docs/images/README.md)。

## 后续计划

- 使用 Docker Compose 简化前后端与数据库部署
- 增加登录鉴权与用户数据隔离
- 增加任务搜索与分页
- 增加任务截止日期提醒
