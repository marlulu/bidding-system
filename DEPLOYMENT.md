# 项目部署指南

本指南详细介绍了如何部署 `bidding-system` 项目，该项目包含一个基于 Spring Boot 的后端服务和一个基于 Vue 3 + Vite 的前端应用。

## 1. 环境准备

在部署项目之前，请确保您的服务器或本地环境已安装以下软件：

*   **Java Development Kit (JDK)**: 17 或更高版本 (后端)
*   **Node.js**: 18.x 或更高版本 (前端)
*   **MySQL**: 8.0 或更高版本 (数据库)
*   **Maven**: 3.x 或更高版本 (后端构建工具)
*   **Nginx**: (可选，用于生产环境反向代理和静态文件服务)

## 2. 数据库部署

1.  **创建数据库**：
    登录 MySQL 数据库，创建一个名为 `bidding_system` 的数据库。
    ```sql
    CREATE DATABASE bidding_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
    ```

2.  **导入 SQL 脚本**：
    项目根目录下提供了一个 `database.sql` 文件，其中包含了数据库表结构和初始数据。请将其导入到刚创建的 `bidding_system` 数据库中。
    ```bash
    mysql -u root -p bidding_system < /path/to/your/bidding-system/database.sql
    ```
    (请将 `/path/to/your/bidding-system/` 替换为您的项目实际路径)

3.  **配置数据库连接**：
    后端服务通过 `bidding-backend/src/main/resources/application.yml` 文件配置数据库连接。请根据您的实际情况修改以下配置：
    ```yaml
    spring:
      datasource:
        driver-class-name: com.mysql.cj.jdbc.Driver
        url: jdbc:mysql://localhost:3306/bidding_system?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
        username: root
        password: root123456
    ```
    *   `url`: 确保数据库地址、端口和数据库名正确。
    *   `username`: 您的 MySQL 用户名。
    *   `password`: 您的 MySQL 密码。

## 3. 后端部署 (Spring Boot)

1.  **进入后端项目目录**：
    ```bash
    cd /path/to/your/bidding-system/bidding-backend
    ```

2.  **打包项目**：
    使用 Maven 清理并打包项目。这将生成一个可执行的 JAR 文件。
    ```bash
    mvn clean package -DskipTests
    ```
    打包成功后，您会在 `target/` 目录下找到 `bidding-backend-1.0.0.jar` (版本号可能有所不同)。

3.  **运行后端服务**：
    您可以通过以下命令运行 JAR 文件：
    ```bash
    java -jar target/bidding-backend-1.0.0.jar
    ```
    或者，为了在后台运行并管理日志，可以使用 `nohup` 和 `&`：
    ```bash
    nohup java -jar target/bidding-backend-1.0.0.jar > backend.log 2>&1 &
    ```
    后端服务默认运行在 `http://localhost:8080/api`。

## 4. 前端部署 (Vue 3 + Vite)

1.  **进入前端项目目录**：
    ```bash
    cd /path/to/your/bidding-system/bidding-frontend
    ```

2.  **安装依赖**：
    ```bash
    npm install
    ```

3.  **构建项目**：
    这将编译前端代码并生成用于生产环境的静态文件，默认输出到 `dist` 目录。
    ```bash
    npm run build
    ```

4.  **配置 Nginx 托管前端静态文件并反向代理后端 API**：
    创建一个 Nginx 配置文件 (例如 `/etc/nginx/conf.d/bidding-system.conf`)，并添加以下内容。请根据您的实际域名和路径进行调整。

    ```nginx
    server {
        listen 80;
        server_name your_domain.com; # 替换为您的域名或IP地址

        # 前端静态文件
        location / {
            root /path/to/your/bidding-system/bidding-frontend/dist; # 替换为前端 dist 目录的绝对路径
            index index.html index.htm;
            try_files $uri $uri/ /index.html;
        }

        # 后端 API 反向代理
        location /api/ {
            proxy_pass http://localhost:8080/api/;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
        }

        error_page 500 502 503 504 /50x.html;
        location = /50x.html {
            root /usr/share/nginx/html;
        }
    }
    ```
    *   `your_domain.com`: 替换为您的实际域名或服务器 IP 地址。
    *   `/path/to/your/bidding-system/bidding-frontend/dist`: 替换为前端 `dist` 目录的绝对路径。
    *   `proxy_pass http://localhost:8080/api/`: 确保指向您的后端服务地址和端口。

5.  **测试 Nginx 配置并重载**：
    ```bash
    sudo nginx -t
    sudo nginx -s reload
    ```

## 5. 访问项目

完成上述步骤后，您可以通过浏览器访问 `http://your_domain.com` (或您的服务器 IP 地址) 来访问部署好的项目。

## 6. 常见问题与排查

*   **后端服务无法启动**：检查 `application.yml` 中的数据库配置是否正确，端口是否被占用，以及 Java 环境是否正确安装。
*   **前端页面空白或报错**：检查 Nginx 配置中 `root` 路径是否正确，`try_files` 配置是否正确，以及前端是否成功构建。
*   **API 请求失败 (404/500)**：检查 Nginx 的 `location /api/` 配置是否正确，`proxy_pass` 是否指向正确的后端地址和端口，以及后端服务是否正常运行。
*   **跨域问题**：确保 Spring Boot 后端已配置 CORS 策略，允许前端域名访问。本项目默认已配置。

如果您在部署过程中遇到任何问题，请根据日志信息进行排查，或提供详细错误信息以便进一步协助。
