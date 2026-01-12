# 招投标系统部署指南

## 1. 概述

本指南详细介绍了如何将招投标系统部署到云服务器上。招投标系统采用前后端分离架构，后端使用 Spring Boot，前端使用 Vue 3，数据库为 MySQL。为了简化部署流程并确保环境一致性，我们推荐使用 Docker Compose 进行容器化部署。

## 2. 部署架构

系统将通过 Docker Compose 编排以下三个容器：

*   **`db`**: MySQL 8.0 数据库容器，用于存储所有业务数据。
*   **`backend`**: Spring Boot 后端服务容器，提供 RESTful API 接口。
*   **`frontend`**: Nginx 容器，用于托管前端静态资源，并作为反向代理将 `/api/` 请求转发至后端服务。

![Deployment Architecture Diagram](https://www.plantuml.com/plantuml/png/SoWkikCKr89AoGaAAR1G0000)

## 3. 环境准备

在开始部署之前，请确保您的云服务器满足以下条件：

### 3.1 操作系统

*   推荐使用 **Ubuntu 20.04 LTS** 或更高版本，或 **CentOS 7/8**。

### 3.2 软件安装

您需要在服务器上安装 **Docker** 和 **Docker Compose**。以下是针对 Ubuntu/Debian 系统的安装步骤：

#### 3.2.1 安装 Docker Engine

```bash
# 更新 apt 包索引
sudo apt-get update

# 安装必要的软件包，允许 apt 通过 HTTPS 使用仓库
sudo apt-get install -y ca-certificates curl gnupg

# 添加 Docker 的官方 GPG 密钥
sudo install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
sudo chmod a+r /etc/apt/keyrings/docker.gpg

# 添加 Docker 仓库到 Apt 源
echo \
  "deb [arch=\"$(dpkg --print-architecture)\" signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu \
  \"$(. /etc/os-release && echo "$VERSION_CODENAME")\" stable" | \
sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

# 再次更新 apt 包索引
sudo apt-get update

# 安装 Docker Engine、CLI 和 Containerd
sudo apt-get install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

# 验证 Docker 是否安装成功
sudo docker run hello-world
```

#### 3.2.2 安装 Docker Compose (如果 `docker-compose-plugin` 未自动安装)

在某些系统上，`docker-compose-plugin` 可能不会自动安装为 `docker compose` 命令。如果 `docker compose version` 命令不工作，您可以手动安装 Docker Compose V2：

```bash
# 下载 Docker Compose 二进制文件
sudo curl -L "https://github.com/docker/compose/releases/download/v2.24.5/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose

# 赋予执行权限
sudo chmod +x /usr/local/bin/docker-compose

# 验证 Docker Compose 是否安装成功
docker compose version
```

#### 3.2.3 安装 Git

用于从 GitHub 克隆项目代码。

```bash
sudo apt-get install -y git
```

## 4. 项目配置

在部署之前，请确保您的项目配置正确。本项目的 Docker Compose 配置已为您准备好，但了解其内部工作原理有助于后续维护。

### 4.1 后端 `application.yml` 配置

`bidding-backend/src/main/resources/application.yml` 文件已修改为从环境变量中读取数据库连接信息，以适应 Docker Compose 的配置：

```yaml
spring:
  datasource:
    url: ${SPRING_DATASOURCE_URL}
    username: ${SPRING_DATASOURCE_USERNAME}
    password: ${SPRING_DATASOURCE_PASSWORD}
  # ... 其他配置 ...
```

这些环境变量将在 `docker-compose.yml` 中定义。

### 4.2 `docker-compose.yml` 配置

项目根目录下的 `docker-compose.yml` 文件定义了所有服务的编排：

```yaml
version: '3.8'

services:
  db:
    image: mysql:8.0
    container_name: bidding-db
    restart: always
    environment:
      MYSQL_ROOT_PASSWORD: 123456
      MYSQL_DATABASE: bidding_system
    ports:
      - "3307:3306" # 可选：如果需要从外部访问MySQL，请保留
    volumes:
      - db_data:/var/lib/mysql
      - ./database.sql:/docker-entrypoint-initdb.d/database.sql # 启动时自动导入SQL

  backend:
    build:
      context: ./bidding-backend
      dockerfile: Dockerfile
    container_name: bidding-backend
    restart: always
    ports:
      - "8083:8083" # 后端服务端口
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://db:3306/bidding_system?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: 123456
    depends_on:
      - db

  frontend:
    build:
      context: ./bidding-frontend
      dockerfile: Dockerfile
    container_name: bidding-frontend
    restart: always
    ports:
      - "8082:80" # 前端通过 8082 端口访问
    depends_on:
      - backend

volumes:
  db_data: # 用于MySQL数据持久化
```

**重要说明**：
*   `MYSQL_ROOT_PASSWORD` 和 `SPRING_DATASOURCE_PASSWORD` 均设置为 `123456`，请根据实际情况修改为更安全的密码。
*   `database.sql` 文件位于项目根目录，会在 `db` 容器首次启动时自动导入。

### 4.3 前端 `nginx.conf` 配置

`bidding-frontend/nginx.conf` 文件用于配置前端 Nginx 容器：

```nginx
server {
    listen 80;
    server_name localhost; # 替换为您的域名或IP

    location / {
        root   /usr/share/nginx/html;
        index  index.html index.htm;
        try_files $uri $uri/ /index.html;
    }

    location /api/ {
        proxy_pass http://backend:8083/api/; # 转发到后端服务
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    error_page   500 502 503 504  /50x.html;
    location = /50x.html {
        root   /usr/share/nginx/html;
    }
}
```

## 5. 部署步骤

完成环境准备和项目配置后，您可以按照以下步骤进行部署：

### 5.1 克隆项目代码

登录到您的云服务器，并克隆项目代码到 `/opt` 目录：

```bash
# 如果 /opt/bidding-system 目录已存在，请先删除
sudo rm -rf /opt/bidding-system

# 克隆项目
sudo git clone https://github.com/marlulu/bidding-system.git /opt/bidding-system

# 进入项目目录
cd /opt/bidding-system
```

### 5.2 启动 Docker Compose 服务

在 `/opt/bidding-system` 目录下，执行以下命令启动所有服务：

```bash
sudo docker compose up -d --build
```

*   `-d`：表示在后台运行容器。
*   `--build`：表示在启动前重新构建镜像（首次部署或修改 Dockerfile 后需要）。

### 5.3 停止和删除服务

如果您需要停止并删除所有容器、网络和卷：

```bash
sudo docker compose down -v
```

*   `-v`：同时删除匿名卷，包括 MySQL 的数据卷 `db_data`。**请谨慎使用，这会删除数据库数据！**

## 6. 部署验证

部署完成后，请进行以下验证：

### 6.1 检查容器状态

```bash
sudo docker compose ps
```

您应该能看到 `bidding-db`、`bidding-backend` 和 `bidding-frontend` 三个容器都处于 `Up` 状态。

### 6.2 访问前端应用

在浏览器中访问您的云服务器 IP 地址和前端端口（例如 `http://您的云服务器IP:8082`）。您应该能看到招投标系统的登录页面。

### 6.3 检查后端 API

尝试进行登录操作，验证前端是否能正常与后端 API 进行交互。您也可以通过浏览器开发者工具查看网络请求，确认 `/api/` 请求是否成功。

### 6.4 查看服务日志

您可以使用 `sudo docker compose logs -f <service_name>` 命令来查看特定服务的实时日志，例如：

```bash
sudo docker compose logs -f backend
sudo docker compose logs -f frontend
sudo docker compose logs -f db
```

## 7. 常见问题与排查

### 7.1 容器无法启动

*   **检查日志**：使用 `sudo docker compose logs <service_name>` 查看具体服务的启动日志，通常能找到错误原因。
*   **端口冲突**：确保宿主机上没有其他服务占用了 8082、8083、3307 端口。如果存在冲突，请修改 `docker-compose.yml` 中的 `ports` 映射。

### 7.2 前端页面空白或加载失败

*   **检查 Nginx 容器日志**：`sudo docker compose logs -f frontend`，看是否有文件找不到或配置错误。
*   **检查前端构建**：确保 `bidding-frontend/dist` 目录存在且包含静态文件。
*   **浏览器缓存**：尝试清除浏览器缓存或使用无痕模式访问。

### 7.3 API 请求失败 (404, 500 等)

*   **检查后端容器日志**：`sudo docker compose logs -f backend`，查看后端服务是否有异常。
*   **检查 Nginx 反向代理配置**：确保 `bidding-frontend/nginx.conf` 中的 `proxy_pass` 地址正确（`http://backend:8081/api/`）。
*   **网络连通性**：确保 `backend` 容器可以访问 `db` 容器，`frontend` 容器可以访问 `backend` 容器（Docker Compose 会自动创建内部网络）。

### 7.4 数据库连接失败

*   **检查 `db` 容器日志**：`sudo docker compose logs -f db`，看 MySQL 是否正常启动。
*   **检查 `docker-compose.yml` 中的数据库环境变量**：确保 `MYSQL_ROOT_PASSWORD` 和 `MYSQL_DATABASE` 配置正确。
*   **检查 `backend` 容器的环境变量**：确保 `SPRING_DATASOURCE_URL`、`SPRING_DATASOURCE_USERNAME` 和 `SPRING_DATASOURCE_PASSWORD` 配置与 `db` 服务匹配。

### 7.5 防火墙问题

*   **云服务器防火墙/安全组**：确保您的云服务商控制台中，已开放 8082 端口（用于 HTTP 访问）和 3307 端口（如果需要从外部连接 MySQL）。

## 8. 总结

通过 Docker Compose 部署，您可以快速、可靠地将招投标系统部署到任何支持 Docker 的环境中。请仔细遵循本指南的步骤，并利用日志进行问题排查。如果您遇到任何无法解决的问题，请提供详细的错误信息和日志，以便进一步协助。
