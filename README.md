
# Sifan-TransactionManagement
仅用于 HSBC Home Task。
对题目的理解：我认为题目中的 "application related to transaction management within a banking system" 更可能是银行/金融机构工作人员操作的内部管理系统，用于检视和管理用户真实交易记录。基于此，我们设计了以下系统，以满足这一需求。

## 环境要求和依赖

**系统要求**：
- 操作系统：Windows, macOS, Linux
- JDK：版本 21

## 部署和运行方式

### Docker

配置见 `Dockerfile`。

### 部署步骤

1. 执行以下命令打包项目：
    ```sh
    mvn clean package
    ```

2. 构建 Docker 镜像：
    ```sh
    docker build -t sifan-home-transaction-management .
    ```

3. 运行 Docker 容器：
    ```sh
    docker run -d -p 8080:8080 sifan-home-transaction-management
    ```
    仅包含后端，通过 Postman 或 Curl 请求测试 [http://localhost:8080](http://localhost:8080)

## 实体设计

### 数据库表结构

```sql
CREATE TABLE transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    from_account_id VARCHAR(255) NOT NULL,
    to_account_id VARCHAR(255) NOT NULL,
    amount DECIMAL(19, 4) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    description VARCHAR(255),
    type INT NOT NULL DEFAULT 0,
    status INT NOT NULL DEFAULT 0,
    scene INT NOT NULL DEFAULT 0,
    created_time BIGINT NOT NULL,
    initiated_time BIGINT,
    processed_time BIGINT,
    completed_time BIGINT,
    failed_time BIGINT,
    ext_params TEXT,
    INDEX idx_from_account_id (from_account_id),
    INDEX idx_to_account_id (to_account_id),
    INDEX idx_created_time (created_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

### 实体设计逻辑

- **`id`**：自增主键，唯一标识每个交易记录。真实场景下可能是用复杂的发号器生成的而不是自增的，这里暂用自增逻辑
- **`from_account_id` 和 `to_account_id`**：记录资金流动的源账户和目标账户。设计为 `VARCHAR(255)` 以兼容多种账户编码格式。
- **`amount` 和 `currency`**：记录交易金额及其货币类型。使用 `DECIMAL(19, 4)` 以保证足够的精度， 避免货币计算中的精度问题。
- **`description`**：对交易的简要描述，便于日后查询和管理。
- **`type`**：交易类型。用于区分不同类别的交易，有助于后期统计和分析。目前只列出了DEBIT和CREDIT两种
- **`status`**：交易状态（0 表示初始状态，1 表示进行中，2 表示完成，3 表示失败，4 表示已退款）。一般 ’进行中‘状态可认为完成
- **`scene`**：交易场景（如线上交易、ATM、pos等）。帮助业务根据场景定制化处理流程。
- **`created_time`, `initiated_time`, `processed_time`, `completed_time`, `failed_time`**：用时间戳记录各关键时间点，方便追踪和审计。
- **`ext_params`**：文本字段，用于存储额外参数，灵活支持业务扩展。json格式。
- **索引 (`INDEX`)**：对 `from_account_id`, `to_account_id` 以及 `created_time` 建立索引，提高查询效率。按业务需要可新增。
- **根据实际情况可能存在分表，建议form_account_id作为分表字段
  
## 分层架构

### 存储层

`TransactionRepository` 使用 `ConcurrentHashMap`（线程安全）模拟数据库行为。

### 服务层

`TransactionService` 处理业务逻辑
使用 Spring 缓存，缓存必要数据（实际业务可按需选择不同的分布式缓存架构）。

### 控制器层

`TransactionController` 实现接口：
- `create`
- `getById`
- `getAll`
- `query`（筛选分页 & 排序）
- `update`
- `delete`

在不同接口中添加必要的校验和异常处理逻辑。使用 `GlobalExceptionHandler` 捕获全局异常，确保接口可用性。添加 `TransactionInterceptor`，内部逻辑待填充，以注释形式标注（可能是一些操作记录、上下文填充、权限校验逻辑等）。在不同层级中数据的传递分别以 'Param'、DTO、VO 等形式存在，降低逻辑耦合度。

## 业务逻辑相关

在 `create`、`update` 等关键写入接口中，需要更加复杂的校验逻辑，比如校验用户信息是否合法、风控校验、重复提交校验、校验用户权限等，需要根据实际场景补充，本项目中未完全实现。

数据查询接口 `query`，为保证接口性能和查询效率，在实际场景中应当接入 `Elasticsearch` 搜索引擎，提高索引能力（本项目中未实现）。

实际应用场景中， `create` ， `update`，`delete` 后台接口应严格限制，交易管理系统上游数据源应来自真实交易产生的消息记录，通过校验后存储在交易管理系统的持久化存储中。交易管理系统对交易记录大部分应为只读操作（特殊情况除外）。

## POM 依赖解释

项目的依赖请参考 `pom.xml` 文件。其中包含了详细的注释说明每个依赖的用途。

## 测试相关

### 单测

单元测试覆盖所有核心业务逻辑，执行以下命令运行测试：

```sh
mvn test
```

测试覆盖率：
- 服务方法覆盖率：100%
- 成功率：100%

![测试示意图](https://github.com/user-attachments/assets/08151e1d-ded5-4092-9d20-e02d0df091c0)

### 压测配置

使用 `k6` 工具进行压测，配置见 `stress_test.js` 文件。

### 压测报告

**概览**

```
scenarios: (100.00%) 1 scenario, 20 max VUs, 2m40s max duration (incl. graceful stop):
          * default: Up to 20 looping VUs for 2m10s over 5 stages (gracefulRampDown: 30s, gracefulStop: 30s)
```

**性能指标**

```
checks.........................: 100.00% 15912 out of 15912
data_received..................: 17 MB   125 kB/s
data_sent......................: 8.1 MB  58 kB/s
http_req_duration..............: avg=2.86ms min=415µs med=2.58ms max=98.63ms 
http_req_failed................: 0.00% 0 out of 15912
```

**创建交易**

```
Create Transactions
   ✓ transaction created
```

**查询所有交易**

```
Get All Transactions
   ✓ transactions fetched
```

**按条件查询交易**

```
Query Transactions
   ✓ transactions queried
```

- 检查通过率：100.00%，15912 次检查全部通过。
- 数据接收和发送：
  - 数据接收量：17MB，速率：125kB/s；
  - 数据发送量：8.1MB，速率：58kB/s。
- HTTP 请求失败率：0.00%，所有请求均成功。
- 平均请求处理时间：2.86ms，90% 的请求处理时间在 4.5ms 以内，95% 的请求处理时间在 5.17ms 以内。
- 最大请求处理时间：98.63ms，尽管大多数请求处理时间较短，但仍需关注少数长尾请求的原因。
- 数据接收和发送时间：
  - 接收时间：平均 104.55µs，90% 的请求在 158µs 内完成。
  - 发送时间：平均 55.29µs，90% 的请求在 73µs 内完成。
  - 等待时间：平均 2.7ms，90% 的请求在 4.27ms 内完成，95% 的请求在 4.95ms 内完成。

## 总结

- 本项目中实现了银行交易管理后台系统的基本功能，合理设计核心实体“Trascation”。
- 实现了复杂场景下的查询，创建，更新，删除功能。
- 所有功能经过全面测试，包括单元测试和压力测试。
- 三层架构设计，扩展性强，耦合度低。
- 核心操作通过缓存保障性能。
- 具备校验和异常处理能力。
- 通过Docker进行容器化。
