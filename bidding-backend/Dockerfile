# 使用 Maven 镜像来构建 Spring Boot 应用
FROM maven:3.8.7-openjdk-17 AS build

# 设置工作目录
WORKDIR /app

# 复制 pom.xml 文件，并下载依赖，利用 Docker 缓存机制
COPY pom.xml .
RUN mvn dependency:go-offline

# 复制所有项目文件
COPY src ./src

# 打包 Spring Boot 应用
RUN mvn clean package -DskipTests

# 使用 OpenJDK 17 JRE 作为运行环境
FROM openjdk:17-jre-slim

# 设置工作目录
WORKDIR /app

# 从构建阶段复制 JAR 文件
COPY --from=build /app/target/*.jar app.jar

# 暴露后端服务端口
EXPOSE 8081

# 启动 Spring Boot 应用
ENTRYPOINT ["java", "-jar", "app.jar"]
