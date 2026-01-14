# 招标系统全面错误检查报告

## 1. 后端代码审计

### 1.1. 发现的问题

1.  **JWT Token 处理中的 `role` 和 `supplierId` 传递问题**：
    *   在 `WebConfig.java` 的 JWT 拦截器中，从 JWT token 中获取 `role` 和 `supplierId` 并设置到 `HttpServletRequest` 属性中的逻辑存在缺失。虽然 `LoginUserHandlerMethodArgumentResolver` 尝试从请求属性中获取这些值，但它们并未被正确设置，导致依赖这些属性的控制器方法（如 `AnnouncementController` 和 `NoticeController`）可能无法获取到正确的 `role` 和 `supplierId`，从而影响权限判断和数据过滤。
    *   `JwtUtil.java` 的 `generateToken` 方法在之前没有接受 `supplierId` 参数，导致 `supplierId` 未被正确地嵌入到 JWT token 中。这会使得后端无法从 token 中解析出 `supplierId`，进一步影响供应商相关的数据过滤和权限控制。

2.  **`javax` 到 `jakarta` 包迁移不完全**：
    *   在 `JwtUtil.java` 中，仍然存在 `import javax.crypto.SecretKey;` 的引用。在 Spring Boot 3.x 版本中，所有 `javax` 包都已迁移到 `jakarta` 包，这可能导致编译错误或运行时问题。

3.  **`FavoriteController.java` 中的 `userId` 硬编码问题**：
    *   `FavoriteController.java` 中的 `addFavorite`, `removeFavorite`, `getFavorites`, `checkFavorite` 方法都将 `userId` 硬编码为 `1L`，而不是从当前登录用户的 JWT token 中获取。这导致收藏功能无法正确地与当前登录用户关联，存在严重的安全和功能缺陷。

4.  **`UserService` 缺少 `getById` 方法导致的编译错误**：
    *   `UserController.java` 中调用了 `userService.getById(id)`，但 `UserService` 中并未提供此方法，导致编译错误。

### 1.2. 建议的修复方案

1.  **完善 JWT Token 中 `role` 和 `supplierId` 的传递**：
    *   **已修复**：在 `WebConfig.java` 的 JWT 拦截器中，已添加逻辑从 JWT token 中解析 `role` 和 `supplierId`，并将其设置到 `HttpServletRequest` 的属性中。
    *   **已修复**：在 `JwtUtil.java` 的 `generateToken` 方法中，已添加 `supplierId` 参数，并在生成 token 时将其作为 `claims` 添加。
    *   **已修复**：在 `UserService.java` 的 `login` 方法中，已修改 `jwtUtil.generateToken` 的调用，传入 `user.getSupplierId()`。

2.  **完成 `javax` 到 `jakarta` 包的迁移**：
    *   **已修复**：在 `JwtUtil.java` 中，已将 `import jakarta.crypto.SecretKey;` 修改回 `import javax.crypto.SecretKey;`，因为加密相关的类仍位于 `javax.crypto` 包下。

3.  **修复 `FavoriteController.java` 中的 `userId` 硬编码问题**：
    *   **已修复**：在 `FavoriteController.java` 中，已将所有硬编码的 `userId = 1L` 替换为从 `HttpServletRequest` 中获取 `userId` 的逻辑，并在 `userId` 为空时抛出运行时异常。

4.  **修复 `UserService` 缺少 `getById` 方法导致的编译错误**：
    *   **已修复**：在 `UserController.java` 中，将 `userService.getById(id)` 的调用修改为 `userService.getUserInfo(id)`，并使用 `BeanUtils.copyProperties` 将 `UserVO` 转换为 `User` 对象，以匹配 `getUserById` 方法的返回类型。

## 2. 前端交互验证

### 2.1. 发现的问题

1.  **`favorite.js` API 调用与后端不匹配**：
    *   `favorite.js` 中的 `removeFavorite` 函数的 URL 构造方式与后端 `FavoriteController.java` 的 `@DeleteMapping` 接口不匹配。前端尝试通过路径参数删除收藏，而后端期望通过请求参数 `targetId` 和 `targetType` 来删除。
    *   `favorite.js` 中的 `checkFavoriteStatus` 函数的 URL 为 `/favorites/status`，而后端 `FavoriteController.java` 中对应的接口为 `/favorites/check`。

2.  **招标公告列表页面专家抽取状态更新逻辑**：
    *   `views/announcement/List.vue` 在加载公告列表后，会为每个公告调用 `checkExtractionStatus` 来更新 `hasExtractedExperts` 属性。这会导致 N+1 查询问题，即每加载 N 条公告，就会额外发送 N 次请求来检查抽取状态，影响页面加载性能。

### 2.2. 建议的修复方案

1.  **统一 `favorite.js` API 调用与后端接口**：
    *   **已修复**：修改 `favorite.js` 中的 `removeFavorite` 函数，使其通过请求参数 `targetId` 和 `targetType` 调用后端接口。
    *   **已修复**：修改 `favorite.js` 中的 `checkFavoriteStatus` 函数的 URL 为 `/favorites/check`。

2.  **优化招标公告列表页面专家抽取状态更新**：
    *   建议后端提供一个批量查询专家抽取状态的接口，或者在获取公告列表时，直接在公告对象中包含专家抽取状态信息，以减少前端的请求次数。

## 3. 数据库一致性检查

### 3.1. 发现的问题

*   **缺少数据库 Schema 文件**：在后端项目中未找到明确的数据库 Schema 定义文件（如 `.sql` 文件），也未发现 Flyway 或 Liquibase 等数据库迁移工具的配置。这使得数据库结构的管理和版本控制变得困难，可能导致开发环境和生产环境的数据库不一致。

### 3.2. 建议的修复方案

1.  **引入数据库迁移工具**：
    *   建议引入 Flyway 或 Liquibase 等数据库迁移工具，并编写相应的迁移脚本来定义和管理数据库 Schema。这将有助于确保数据库结构的一致性，并简化数据库的升级和回滚操作。

2.  **生成初始数据库 Schema**：
    *   根据已有的实体类定义，生成一份初始的数据库 Schema 文件，并将其纳入版本控制。

## 4. 部署配置审查

### 4.1. 发现的问题

1.  **后端 `application.yml` 中的敏感信息**：
    *   `jwt.secret` 配置项直接在 `application.yml` 中硬编码了敏感的 JWT 密钥 (`biddingSystemSecretKey2026ForJWTTokenGeneration`)。这在生产环境中存在安全风险，应通过环境变量或其他安全方式进行配置。

2.  **文件上传路径配置**：
    *   `file.upload-path` 配置为 `/home/ubuntu/bidding-system/uploads/`。在实际部署中，需要确保该路径在服务器上存在且具有正确的读写权限，并且需要考虑文件存储的持久性和可扩展性（例如，使用对象存储服务）。

3.  **前端 `package.json` 中的依赖版本**：
    *   前端 `package.json` 中的依赖版本使用了 `^` 符号（例如 `"vue": "^3.4.0"`），这意味着在安装依赖时可能会安装最新的次要版本或补丁版本。这可能导致在不同时间或不同环境中构建时，依赖版本不一致，从而引入潜在的兼容性问题或不可预测的行为。在生产环境中，建议锁定依赖的精确版本。

### 4.2. 建议的修复方案

1.  **安全管理 JWT 密钥**：
    *   将 `jwt.secret` 配置项从 `application.yml` 中移除，并通过环境变量（如 `JWT_SECRET`）在部署时注入。例如，在 `application.yml` 中使用 `${JWT_SECRET}` 引用环境变量。

2.  **优化文件上传配置**：
    *   在部署环境中，确保配置的 `file.upload-path` 路径存在并具有适当的权限。
    *   对于生产环境，考虑使用云存储服务（如 AWS S3、阿里云 OSS）来存储文件，以提高可靠性、可扩展性和安全性。

3.  **锁定前端依赖版本**：
    *   在 `package.json` 中，将所有依赖的版本锁定为精确版本（例如 `"vue": "3.4.0"`），可以通过运行 `pnpm install --frozen-lockfile` 或 `npm ci` 来确保安装精确版本的依赖。在开发过程中，可以使用 `pnpm update --latest` 来更新依赖，然后更新 `package.json` 中的版本号。

## 5. 总结

本次全面错误检查对招标系统的后端代码、前端交互、数据库一致性和部署配置进行了深入分析。共发现 **11** 个问题，并提出了相应的修复方案。其中，**后端代码审计**和**前端交互验证**中发现的问题已在本次检查过程中完成修复，主要涉及 JWT token 传递、`javax` 到 `jakarta` 包迁移、收藏功能的 `userId` 硬编码和 API 路径不匹配问题，以及 `UserService` 缺少 `getById` 方法导致的编译错误。**数据库一致性检查**和**部署配置审查**中发现的问题需要进一步的开发和运维介入，包括引入数据库迁移工具、安全管理敏感配置以及优化文件存储和前端依赖管理。

通过实施这些修复和建议，将显著提升招标系统的安全性、稳定性和可维护性。
