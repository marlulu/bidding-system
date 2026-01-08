# 内部招标网站系统 - 运行与部署指南

## 1. 项目简介

本项目是一个基于 Vue3 和 Spring Boot 的全栈内部招标网站系统，旨在提供一个安全、高效、透明的在线招标平台。系统包含门户管理、供应商库管理、用户与权限管理等核心功能模块。

### 1.1 技术栈

- **前端**: Vue3 + Vite + Pinia + Vue Router + Element Plus
- **后端**: Spring Boot 3.x + MyBatis-Plus + MySQL 8.0
- **数据库**: MySQL 8.0
- **认证**: JWT Token

## 2. 环境准备

在运行或部署本项目之前，请确保您的环境中已安装以下软件：

- **JDK 17+**: Java 开发工具包
- **Maven 3.6+**: 项目构建工具
- **Node.js 18+**: JavaScript 运行环境
- **MySQL 8.0+**: 关系型数据库

## 3. 本地开发运行

### 3.1 数据库配置

1.  **创建数据库**: 在您的 MySQL 服务器中创建一个新的数据库，例如 `bidding_system`。

    ```sql
    CREATE DATABASE bidding_system CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
    ```

2.  **导入数据**: 将项目根目录下的 `database.sql` 文件导入到您创建的数据库中。这会创建所需的表结构和初始数据。

3.  **修改配置**: 打开后端项目 `bidding-backend/src/main/resources/application.yml` 文件，根据您的 MySQL 环境修改 `spring.datasource` 下的 `url`, `username`, `password` 配置。

### 3.2 后端运行

1.  **进入后端目录**:

    ```bash
    cd bidding-backend
    ```

2.  **安装依赖并运行**:

    ```bash
    mvn spring-boot:run
    ```

    当看到类似 `Tomcat started on port(s): 8080 (http)` 的日志时，表示后端服务已成功启动。

### 3.3 前端运行

1.  **进入前端目录**:

    ```bash
    cd bidding-frontend
    ```

2.  **安装依赖**:

    ```bash
    pnpm install
    ```

3.  **启动开发服务器**:

    ```bash
    pnpm dev
    ```

    前端开发服务器默认运行在 `http://localhost:3000`。Vite 已配置代理，所有对 `/api` 的请求都会被转发到 `http://localhost:8080`，从而解决跨域问题。

4.  **访问系统**: 打开浏览器，访问 `http://localhost:3000` 即可看到登录页面。

    -   默认管理员账号：`admin` / `admin123`
    -   默认供应商账号：`supplier` / `supplier123`

## 4. 生产环境部署

### 4.1 后端部署

1.  **打包项目**: 在 `bidding-backend` 目录下执行以下命令进行打包：

    ```bash
    mvn clean package -DskipTests
    ```

    打包成功后，会在 `target` 目录下生成一个 `bidding-backend-1.0.0.jar` 文件。

2.  **上传并运行**: 将该 `.jar` 文件上传到您的服务器，并通过以下命令在后台运行：

    ```bash
    nohup java -jar bidding-backend-1.0.0.jar > bidding.log 2>&1 &
    ```

    请确保服务器的防火墙已开放后端服务所需的端口（默认为 8080）。

### 4.2 前端部署

1.  **打包项目**: 在 `bidding-frontend` 目录下执行以下命令进行打包：

    ```bash
    pnpm build
    ```

    打包成功后，会在 `dist` 目录下生成静态文件。

2.  **部署静态文件**: 将 `dist` 目录下的所有文件上传到您的 Web 服务器（如 Nginx）的网站根目录。

3.  **配置 Nginx**: 为了处理路由和 API 代理，您需要配置 Nginx。以下是一个示例配置：

    ```nginx
    server {
        listen       80;
        server_name  your.domain.com; # 替换为您的域名

        # 前端静态文件
        location / {
            root   /path/to/your/frontend/dist; # 替换为您的前端文件路径
            index  index.html;
            try_files $uri $uri/ /index.html;
        }

        # 后端 API 代理
        location /api/ {
            proxy_pass http://127.0.0.1:8080/api/;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        }

        # 错误页面
        error_page   500 502 503 504  /50x.html;
        location = /50x.html {
            root   /usr/share/nginx/html;
        }
    }
    ```

    修改配置后，请重新加载 Nginx 服务：

    ```bash
    sudo nginx -s reload
    ```

## 5. 注意事项

-   **JWT Secret**: 为了安全起见，请务必修改 `application.yml` 中的 `jwt.secret` 配置，使用一个更复杂、随机的字符串。
-   **密码安全**: 初始密码仅为示例，请在首次登录后立即修改。
-   **文件上传**: `application.yml` 中的 `file.upload-path` 配置了文件上传的存储路径，请确保该目录存在且应用有写入权限。
