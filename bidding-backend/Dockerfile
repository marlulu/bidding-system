# 使用 Eclipse Temurin 17 JRE 作为运行环境
# 假设 JAR 包已在本地构建完成，并位于 bidding-backend/target 目录下
FROM eclipse-temurin:17-jre-alpine

# 设置工作目录
WORKDIR /app

# 从构建阶段复制 JAR 文件
COPY target/*.jar app.jar

# 暴露后端服务端口
EXPOSE 8083

# 启动 Spring Boot 应用
ENTRYPOINT ["java", "-jar", "app.jar"]
