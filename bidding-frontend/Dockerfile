# 构建阶段
FROM node:18-alpine AS build

WORKDIR /app

COPY package.json .
COPY package-lock.json .
RUN npm install

COPY . .
RUN npm run build

# 运行阶段
FROM nginx:stable-alpine

# 复制构建好的前端静态文件到 Nginx 默认的静态文件目录
COPY --from=build /app/dist /usr/share/nginx/html

# 复制自定义的 Nginx 配置文件
# 注意：这里的 nginx.conf 稍后会创建
COPY nginx.conf /etc/nginx/conf.d/default.conf

# 暴露 Nginx 默认的 80 端口
EXPOSE 80

# 启动 Nginx 服务
CMD ["nginx", "-g", "daemon off;"]
