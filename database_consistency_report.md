## 数据库一致性检查报告

### 实体类结构概览

#### `Announcement.java` (对应表: `bid_announcement`)

| 字段名           | Java 类型      | 潜在数据库类型 | 备注                                   |
| :--------------- | :------------- | :------------- | :------------------------------------- |
| `id`             | `Long`         | `BIGINT`       | 主键，自增                             |
| `title`          | `String`       | `VARCHAR`      | 公告标题                               |
| `announcementNo` | `String`       | `VARCHAR`      | 公告编号                               |
| `type`           | `String`       | `VARCHAR`      | 公告类型 (BID, CHANGE, TERMINATE, RESULT) |
| `content`        | `String`       | `TEXT`         | 公告内容                               |
| `projectName`    | `String`       | `VARCHAR`      | 项目名称                               |
| `projectBudget`  | `BigDecimal`   | `DECIMAL`      | 项目预算                               |
| `region`         | `String`       | `VARCHAR`      | 地区                                   |
| `industry`       | `String`       | `VARCHAR`      | 行业                                   |
| `bidDeadline`    | `LocalDateTime`| `DATETIME`     | 投标截止时间                           |
| `isTop`          | `Integer`      | `TINYINT`      | 是否置顶 (0: 否, 1: 是)                |
| `contactPerson`  | `String`       | `VARCHAR`      | 联系人                                 |
| `contactPhone`   | `String`       | `VARCHAR`      | 联系电话                               |
| `attachmentFiles`| `String`       | `TEXT`         | 附件文件路径 (JSON 字符串)             |
| `visibilityLevel`| `String`       | `VARCHAR`      | 可见性级别 (PUBLIC, RESTRICTED)        |
| `visibleSupplierIds`| `String`    | `TEXT`         | 可见供应商ID (JSON 字符串)             |
| `status`         | `String`       | `VARCHAR`      | 状态 (DRAFT, PUBLISHED, CLOSED)        |
| `publisherId`    | `Long`         | `BIGINT`       | 发布者ID                               |
| `publishTime`    | `LocalDateTime`| `DATETIME`     | 发布时间                               |
| `createTime`     | `LocalDateTime`| `DATETIME`     | 创建时间                               |
| `updateTime`     | `LocalDateTime`| `DATETIME`     | 更新时间                               |


#### `BidRecord.java` (对应表: `bid_record`)

| 字段名           | Java 类型      | 潜在数据库类型 | 备注                                   |
| :--------------- | :------------- | :------------- | :------------------------------------- |
| `id`             | `Long`         | `BIGINT`       | 主键，自增                             |
| `announcementId` | `Long`         | `BIGINT`       | 招标公告ID                             |
| `supplierId`     | `Long`         | `BIGINT`       | 供应商ID                               |
| `bidAmount`      | `BigDecimal`   | `DECIMAL`      | 投标金额                               |
| `status`         | `String`       | `VARCHAR`      | 状态 (SUBMITTED, WIN, LOSE)            |
| `createTime`     | `LocalDateTime`| `DATETIME`     | 创建时间                               |




#### `Favorite.java` (对应表: `sys_favorite`)

| 字段名           | Java 类型      | 潜在数据库类型 | 备注                                   |
| :--------------- | :------------- | :------------- | :------------------------------------- |
| `id`             | `Long`         | `BIGINT`       | 主键，自增                             |
| `userId`         | `Long`         | `BIGINT`       | 用户ID                                 |
| `targetId`       | `Long`         | `BIGINT`       | 收藏目标ID                             |
| `targetType`     | `String`       | `VARCHAR`      | 收藏目标类型 (ANNOUNCEMENT, SUPPLIER)  |
| `createTime`     | `LocalDateTime`| `DATETIME`     | 创建时间                               |

#### `Notice.java` (对应表: `system_notice`)

| 字段名           | Java 类型      | 潜在数据库类型 | 备注                                   |
| :--------------- | :------------- | :------------- | :------------------------------------- |
| `id`             | `Long`         | `BIGINT`       | 主键，自增                             |
| `title`          | `String`       | `VARCHAR`      | 通知标题                               |
| `content`        | `String`       | `TEXT`         | 通知内容                               |
| `type`           | `String`       | `VARCHAR`      | 通知类型                               |
| `targetType`     | `String`       | `VARCHAR`      | 目标类型 (ALL, SUPPLIER)               |
| `targetSupplierIds`| `String`    | `TEXT`         | 目标供应商ID (JSON 字符串)             |
| `publisherId`    | `Long`         | `BIGINT`       | 发布者ID                               |
| `publishTime`    | `LocalDateTime`| `DATETIME`     | 发布时间                               |
| `status`         | `String`       | `VARCHAR`      | 状态                                   |
| `createTime`     | `LocalDateTime`| `DATETIME`     | 创建时间                               |
| `updateTime`     | `LocalDateTime`| `DATETIME`     | 更新时间                               |

#### `NoticeReadRecord.java` (对应表: `notice_read_record`)

| 字段名           | Java 类型      | 潜在数据库类型 | 备注                                   |
| :--------------- | :------------- | :------------- | :------------------------------------- |
| `id`             | `Long`         | `BIGINT`       | 主键，自增                             |
| `noticeId`       | `Long`         | `BIGINT`       | 通知ID                                 |
| `userId`         | `Long`         | `BIGINT`       | 用户ID                                 |
| `readTime`       | `LocalDateTime`| `DATETIME`     | 阅读时间                               |

#### `Policy.java` (对应表: `policy_regulation`)

| 字段名           | Java 类型      | 潜在数据库类型 | 备注                                   |
| :--------------- | :------------- | :------------- | :------------------------------------- |
| `id`             | `Long`         | `BIGINT`       | 主键，自增                             |
| `title`          | `String`       | `VARCHAR`      | 政策法规标题                           |
| `category`       | `String`       | `VARCHAR`      | 政策法规类别                           |
| `content`        | `String`       | `TEXT`         | 政策法规内容                           |
| `attachmentFiles`| `String`       | `TEXT`         | 附件文件路径 (JSON 字符串)             |
| `publisherId`    | `Long`         | `BIGINT`       | 发布者ID                               |
| `publishTime`    | `LocalDateTime`| `DATETIME`     | 发布时间                               |
| `status`         | `String`       | `VARCHAR`      | 状态                                   |
| `createTime`     | `LocalDateTime`| `DATETIME`     | 创建时间                               |
| `updateTime`     | `LocalDateTime`| `DATETIME`     | 更新时间                               |

#### `Supplier.java` (对应表: `supplier`)

| 字段名           | Java 类型      | 潜在数据库类型 | 备注                                   |
| :--------------- | :------------- | :------------- | :------------------------------------- |
| `id`             | `Long`         | `BIGINT`       | 主键，自增                             |
| `companyName`    | `String`       | `VARCHAR`      | 公司名称                               |
| `companyCode`    | `String`       | `VARCHAR`      | 公司代码                               |
| `legalPerson`    | `String`       | `VARCHAR`      | 法人代表                               |
| `contactName`    | `String`       | `VARCHAR`      | 联系人姓名                             |
| `contactPhone`   | `String`       | `VARCHAR`      | 联系电话                               |
| `contactEmail`   | `String`       | `VARCHAR`      | 联系邮箱                               |
| `address`        | `String`       | `VARCHAR`      | 公司地址                               |
| `industry`       | `String`       | `VARCHAR`      | 所属行业                               |
| `scale`          | `String`       | `VARCHAR`      | 公司规模                               |
| `qualificationLevel`| `String`    | `VARCHAR`      | 资质等级                               |
| `description`    | `String`       | `TEXT`         | 公司描述                               |
| `qualificationFiles`| `String`    | `TEXT`         | 资质文件路径 (JSON 字符串)             |
| `status`         | `Integer`      | `TINYINT`      | 状态 (0-待审核, 1-审核通过, 2-审核不通过)|
| `auditRemark`    | `String`       | `TEXT`         | 审核意见                               |
| `auditorId`      | `Long`         | `BIGINT`       | 审核人ID                               |
| `createTime`     | `LocalDateTime`| `DATETIME`     | 创建时间                               |
| `updateTime`     | `LocalDateTime`| `DATETIME`     | 更新时间                               |

#### `User.java` (对应表: `sys_user`)

| 字段名           | Java 类型      | 潜在数据库类型 | 备注                                   |
| :--------------- | :------------- | :------------- | :------------------------------------- |
| `id`             | `Long`         | `BIGINT`       | 主键，自增                             |
| `username`       | `String`       | `VARCHAR`      | 用户名                                 |
| `password`       | `String`       | `VARCHAR`      | 密码 (加密后)                          |
| `realName`       | `String`       | `VARCHAR`      | 真实姓名                               |
| `phone`          | `String`       | `VARCHAR`      | 手机号                                 |
| `email`          | `String`       | `VARCHAR`      | 邮箱                                   |
| `avatar`         | `String`       | `VARCHAR`      | 头像URL                                |
| `role`           | `String`       | `VARCHAR`      | 角色 (ADMIN, SUPPLIER, EXPERT)         |
| `status`         | `Integer`      | `TINYINT`      | 状态 (0-禁用, 1-启用, 2-待审核)        |
| `supplierId`     | `Long`         | `BIGINT`       | 供应商ID (如果角色是 SUPPLIER)         |
| `createTime`     | `LocalDateTime`| `DATETIME`     | 创建时间                               |
| `updateTime`     | `LocalDateTime`| `DATETIME`     | 更新时间                               |

#### `Expert.java` (对应表: `sys_expert`)

| 字段名           | Java 类型      | 潜在数据库类型 | 备注                                   |
| :--------------- | :------------- | :------------- | :------------------------------------- |
| `id`             | `Long`         | `BIGINT`       | 主键，自增                             |
| `name`           | `String`       | `VARCHAR`      | 专家姓名                               |
| `specialty`      | `String`       | `VARCHAR`      | 专业领域                               |
| `title`          | `String`       | `VARCHAR`      | 职称                                   |
| `phone`          | `String`       | `VARCHAR`      | 联系电话                               |
| `email`          | `String`       | `VARCHAR`      | 邮箱                                   |
| `description`    | `String`       | `TEXT`         | 专家简介                               |
| `createTime`     | `LocalDateTime`| `DATETIME`     | 创建时间                               |
| `updateTime`     | `LocalDateTime`| `DATETIME`     | 更新时间                               |
| `deleted`        | `Integer`      | `TINYINT`      | 是否删除 (0-否, 1-是)                  |

#### `ExpertExtraction.java` (对应表: `sys_expert_extraction`)

| 字段名           | Java 类型      | 潜在数据库类型 | 备注                                   |
| :--------------- | :------------- | :------------- | :------------------------------------- |
| `id`             | `Long`         | `BIGINT`       | 主键，自增                             |
| `announcementId` | `Long`         | `BIGINT`       | 招标公告ID                             |
| `expertId`       | `Long`         | `BIGINT`       | 专家ID                                 |
| `extractTime`    | `LocalDateTime`| `DATETIME`     | 抽取时间                               |
| `extractorId`    | `Long`         | `BIGINT`       | 抽取人ID                               |
| `deleted`        | `Integer`      | `TINYINT`      | 是否删除 (0-否, 1-是)                  |

