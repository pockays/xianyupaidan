# 系统架构图

```mermaid
%%{init: {'theme': 'base', 'themeVariables': { 'background': '#fff', 'primaryColor': 'transparent', 'primaryBorderColor': '#333', 'primaryTextColor': '#333', 'lineColor': '#333' }, 'flowchart': { 'curve': 'linear', 'rankSpacing': 60, 'nodeSpacing': 30 }}}%%
flowchart TD
    LOGIN[登录页<br/>LoginView]
    U_HOME[首页<br/>UserHome]
    U_ORDERS[订单列表<br/>UserOrderList]
    U_EDIT[订单编辑<br/>UserOrderEdit]
    A_HOME[首页<br/>AdminHome]
    A_ORDERS[订单管理<br/>AdminOrderList]
    A_DETAIL[订单详情<br/>AdminOrderDetail]
    A_MANAGE[配置管理<br/>AdminManagement]
    S_MANAGE[管理员管理<br/>SuperAdminManage]

    AUTH_CTL[AuthController<br/>登录认证]
    PUBLIC_CTL[PublicController<br/>公告标签]
    USER_CTL[UserController<br/>用户接口]
    ADMIN_CTL[AdminController<br/>管理接口]
    SUPER_CTL[SuperAdminController<br/>超管接口]

    AUTH_SVC[AuthService<br/>认证逻辑]
    ORDER_SVC[OrderService<br/>订单业务]
    ADMIN_SVC[AdminService<br/>配置业务]
    SUPER_SVC[SuperAdminService<br/>超管业务]
    TAG_SVC[TagService<br/>标签业务]
    USER_SVC[UserService<br/>用户首页]

    MAPPER[MyBatis-Plus<br/>数据访问层]
    MYSQL[(MySQL)]

    LOGIN --- AUTH_CTL
    U_HOME --- USER_CTL
    U_ORDERS --- USER_CTL
    U_EDIT --- USER_CTL
    A_HOME --- ADMIN_CTL
    A_ORDERS --- ADMIN_CTL
    A_DETAIL --- ADMIN_CTL
    A_MANAGE --- ADMIN_CTL
    S_MANAGE --- SUPER_CTL

    AUTH_CTL --- AUTH_SVC
    PUBLIC_CTL --- TAG_SVC
    USER_CTL --- USER_SVC
    USER_CTL --- ORDER_SVC
    ADMIN_CTL --- ORDER_SVC
    ADMIN_CTL --- TAG_SVC
    ADMIN_CTL --- ADMIN_SVC
    SUPER_CTL --- SUPER_SVC

    AUTH_SVC --- MAPPER
    ORDER_SVC --- MAPPER
    ADMIN_SVC --- MAPPER
    SUPER_SVC --- MAPPER
    TAG_SVC --- MAPPER
    USER_SVC --- MAPPER
    MAPPER --- MYSQL
```

## 模块对照表

| 前端页面 | 对应 API 模块 | 后端 Controller | 后端 Service |
|---|---|---|---|
| 登录页 | auth.ts | AuthController | AuthService |
| 用户首页 | user.ts | UserController | UserService |
| 订单列表 | user.ts | UserController | OrderService |
| 订单编辑 | user.ts | UserController | OrderService |
| 管理首页 | admin.ts | AdminController | OrderService |
| 订单管理 | admin.ts | AdminController | OrderService |
| 订单详情 | admin.ts | AdminController | OrderService |
| 配置管理 | admin.ts | AdminController | TagService, AdminService |
| 超管管理 | superAdmin.ts | SuperAdminController | SuperAdminService |

## 类图

```mermaid
%%{init: {'theme': 'base', 'themeVariables': { 'background': '#fff', 'primaryColor': 'transparent', 'secondaryColor': 'transparent', 'tertiaryColor': 'transparent', 'primaryBorderColor': '#333', 'primaryTextColor': '#333', 'lineColor': '#333' }}}%%
classDiagram
    direction TB

    class 认证Controller {
        +闲鱼登录()
        +管理员登录()
    }
    class 管理Controller {
        +订单管理()
        +标签管理()
        +系统配置()
    }
    class 用户Controller {
        +首页()
        +订单操作()
    }
    class 超管Controller {
        +管理员管理()
    }

    class 认证Service {
        +闲鱼登录()
        +管理员登录()
    }
    class 订单Service {
        +创建()
        +修改()
        +删除()
        +修改状态()
    }
    class 配置Service {
        +获取配置()
        +更新配置()
    }
    class 超管Service {
        +管理员增删改查()
    }
    class 标签Service {
        +标签增删改查()
    }
    class 用户Service {
        +首页数据()
    }

    class 管理员Mapper
    class 用户Mapper
    class 排单Mapper
    class 排单分类Mapper
    class 排单项Mapper
    class 超管Mapper
    class 预设标签Mapper
    class 系统配置Mapper

    认证Controller --> 认证Service
    管理Controller --> 订单Service
    管理Controller --> 标签Service
    管理Controller --> 配置Service
    用户Controller --> 用户Service
    用户Controller --> 订单Service
    超管Controller --> 超管Service

    认证Service --> 管理员Mapper
    认证Service --> 用户Mapper
    认证Service --> 超管Mapper
    订单Service --> 排单Mapper
    订单Service --> 排单分类Mapper
    订单Service --> 排单项Mapper
    配置Service --> 系统配置Mapper
    超管Service --> 管理员Mapper
    超管Service --> 系统配置Mapper
    标签Service --> 预设标签Mapper
    用户Service --> 排单Mapper
    用户Service --> 系统配置Mapper
```

## 用例图

### 超级管理员

```mermaid
%%{init: {'theme': 'base', 'themeVariables': { 'background': '#fff', 'primaryColor': 'transparent', 'primaryBorderColor': '#333', 'primaryTextColor': '#333', 'lineColor': '#333' }, 'flowchart': { 'curve': 'linear' }}}%%
flowchart LR
    超级管理员[/超级管理员/]

    超级管理员 --- 登录认证(登录认证)
    超级管理员 --- 管理员列表(查看管理员列表)
    超级管理员 --- 创建管理员(创建管理员)
    超级管理员 --- 修改管理员(修改管理员)
    超级管理员 --- 删除管理员(删除管理员)
    超级管理员 --- 启停管理员(启用/禁用管理员)
```

### 管理员

```mermaid
%%{init: {'theme': 'base', 'themeVariables': { 'background': '#fff', 'primaryColor': 'transparent', 'primaryBorderColor': '#333', 'primaryTextColor': '#333', 'lineColor': '#333' }, 'flowchart': { 'curve': 'linear' }}}%%
flowchart LR
    管理员[/管理员/]

    管理员 --- 登录认证(登录认证)
    管理员 --- 订单列表(查看订单列表)
    管理员 --- 订单详情(查看订单详情)
    管理员 --- 修改状态(修改订单状态)
    管理员 --- 增删改项(增删改排单项)
    管理员 --- 创建订单(代用户创建订单)
    管理员 --- 删除订单(删除订单)
    管理员 --- 标签管理(管理预设标签)
    管理员 --- 系统配置(修改系统配置)
```

### 闲鱼用户

```mermaid
%%{init: {'theme': 'base', 'themeVariables': { 'background': '#fff', 'primaryColor': 'transparent', 'primaryBorderColor': '#333', 'primaryTextColor': '#333', 'lineColor': '#333' }, 'flowchart': { 'curve': 'linear' }}}%%
flowchart LR
    闲鱼用户[/闲鱼用户/]

    闲鱼用户 --- 闲鱼登录(闲鱼ID登录)
    闲鱼用户 --- 首页概览(查看首页概览)
    闲鱼用户 --- 订单列表(查看我的订单)
    闲鱼用户 --- 创建排单(创建排单)
    闲鱼用户 --- 修改排单(修改排单)
    闲鱼用户 --- 提交排单(提交排单)
```

## 协作图

### 用户认证登录

```mermaid
%%{init: {'theme': 'base', 'themeVariables': { 'background': '#fff', 'primaryColor': 'transparent', 'primaryBorderColor': '#333', 'primaryTextColor': '#333', 'lineColor': '#333' }, 'flowchart': { 'curve': 'linear' }}}%%
flowchart TD
    用户[/用户/管理员/]
    前端[Vue前端]
    AuthC[AuthController]
    AuthS[AuthService]
    DB[(MySQL)]

    用户 --- 前端
    前端 --- AuthC
    AuthC --- AuthS
    AuthS --- DB
    AuthS --- AuthS

    用户 -->|1: 输入凭证点击登录| 前端
    前端 -->|2: POST /api/auth/login| AuthC
    AuthC -->|3: login| AuthS
    AuthS -->|4: 查询admin/super_admin表| DB
    DB -->|5: 返回记录| AuthS
    AuthS -->|6: BCrypt验证+JWT生成| AuthS
    AuthS -->|7: token+role+nickname| AuthC
    AuthC -->|8: Result响应| 前端
    前端 -->|9: localStorage+Pinia| 前端
    前端 -->|10: 跳转首页| 用户
```

### 排单创建与审核

```mermaid
%%{init: {'theme': 'base', 'themeVariables': { 'background': '#fff', 'primaryColor': 'transparent', 'primaryBorderColor': '#333', 'primaryTextColor': '#333', 'lineColor': '#333' }, 'flowchart': { 'curve': 'linear' }}}%%
flowchart TD
    用户[/闲鱼用户/]
    管理员[/管理员/]
    前端[Vue前端]
    OrderC[OrderController]
    OrderS[OrderService]
    DB[(MySQL)]
    MQ[RabbitMQ]
    Mail[EmailService]

    用户 --- 前端
    管理员 --- 前端
    前端 --- OrderC
    OrderC --- OrderS
    OrderS --- DB
    OrderS --- MQ
    MQ --- Mail

    用户 -->|1: 填写排单信息| 前端
    前端 -->|2: POST /api/user/orders| OrderC
    OrderC -->|3: createOrder| OrderS
    OrderS -->|4: INSERT order/category/item| DB
    DB -->|5: 返回数据| OrderS
    OrderS -->|6: 订单详情| 前端
    前端 -->|7: 显示已创建| 用户

    用户 -->|8: 确认无误提交| 前端
    前端 -->|9: POST submit| OrderC
    OrderC -->|10: submitOrder| OrderS
    OrderS -->|11: UPDATE submitted=1| DB
    DB -->|12: 更新成功| OrderS
    OrderS -->|13: 提交成功| 前端
    前端 -->|14: 已提交| 用户

    管理员 -->|15: 查看待处理订单| 前端
    前端 -->|16: GET /api/admin/orders| OrderC
    OrderC -->|17: getAdminOrders| OrderS
    OrderS -->|18: SELECT 租户订单| DB
    DB -->|19: 订单列表| OrderS
    OrderS -->|20: 订单列表| 前端
    前端 -->|21: 展示| 管理员

    管理员 -->|22: 改状态为进行中| 前端
    前端 -->|23: PUT status| OrderC
    OrderC -->|24: updateOrderStatus| OrderS
    OrderS -->|25: UPDATE status=CURRENT| DB
    DB -->|26: 更新成功| OrderS
    OrderS -->|27: 发送邮件通知| MQ
    MQ -->|28: 消费队列| Mail
    Mail -->|29: SMTP发送邮件| Mail
    OrderS -->|30: 状态更新成功| 前端
    前端 -->|31: 审核完成| 管理员
```

## 时序图

### 排单创建与提交

```mermaid
%%{init: {'theme': 'base', 'themeVariables': { 'background': '#fff', 'primaryColor': 'transparent', 'primaryBorderColor': '#333', 'primaryTextColor': '#333', 'lineColor': '#333', 'signalColor': '#333' }}}%%
sequenceDiagram
    actor 用户 as 闲鱼用户
    participant 前端 as Vue前端
    participant OrderC as OrderController
    participant OrderS as OrderService
    participant DB as MySQL

    用户->>前端: 填写排单信息
    前端->>OrderC: POST /api/user/orders
    OrderC->>OrderS: createOrder()
    OrderS->>DB: INSERT 排单+分类+项
    DB-->>OrderS: 返回数据
    OrderS-->>前端: 订单详情
    前端-->>用户: 创建成功(WAITING)

    用户->>前端: 确认无误提交
    前端->>OrderC: POST submit
    OrderC->>OrderS: submitOrder()
    OrderS->>DB: UPDATE submitted=1
    DB-->>OrderS: ok
    OrderS-->>前端: 提交成功
    前端-->>用户: 等待审核
```

### 订单审核与状态变更

```mermaid
%%{init: {'theme': 'base', 'themeVariables': { 'background': '#fff', 'primaryColor': 'transparent', 'primaryBorderColor': '#333', 'primaryTextColor': '#333', 'lineColor': '#333', 'signalColor': '#333' }}}%%
sequenceDiagram
    actor 管理员 as 管理员
    participant 前端 as Vue前端
    participant OrderS as OrderService
    participant DB as MySQL
    participant MQ as RabbitMQ

    管理员->>前端: 查看订单并修改状态
    前端->>OrderS: updateOrderStatus(CURRENT)
    OrderS->>DB: UPDATE status
    DB-->>OrderS: ok
    OrderS->>MQ: 发送邮件通知
    OrderS-->>前端: 状态更新成功
    前端-->>管理员: 审核完成
```

### CDC审计日志采集

```mermaid
%%{init: {'theme': 'base', 'themeVariables': { 'background': '#fff', 'primaryColor': 'transparent', 'primaryBorderColor': '#333', 'primaryTextColor': '#333', 'lineColor': '#333', 'signalColor': '#333' }}}%%
sequenceDiagram
    participant DB as MySQL
    participant Canal as Canal
    participant Kafka as Kafka
    participant Consumer as AuditLogConsumer
    participant AuditDB as audit_log

    DB->>Canal: binlog事件(INSERT/UPDATE/DELETE)
    Canal->>Kafka: FlatMessage JSON
    Kafka->>Consumer: 消费消息
    Consumer->>Consumer: 解析表名/操作类型/新旧数据
    Consumer->>AuditDB: INSERT 审计记录
```

## 状态图

### 排单状态流转

```mermaid
%%{init: {'theme': 'base', 'themeVariables': { 'background': '#fff', 'primaryColor': 'transparent', 'primaryBorderColor': '#333', 'primaryTextColor': '#333', 'lineColor': '#333' }}}%%
stateDiagram-v2
    草稿 --> 待审核: 用户提交排单
    待审核 --> 进行中: 管理员审核通过
    进行中 --> 已完成: 管理员标记完成
    待审核 --> 已取消: 管理员取消
    进行中 --> 已取消: 管理员取消

    state 草稿 {
        创建排单 --> 编辑排单
        编辑排单 --> 确认提交
    }
```

### 订单审核活动

```mermaid
%%{init: {'theme': 'base', 'themeVariables': { 'background': '#fff', 'primaryColor': 'transparent', 'primaryBorderColor': '#333', 'primaryTextColor': '#333', 'lineColor': '#333' }}}%%
stateDiagram-v2
    查看待审核列表 --> 查看订单详情
    查看订单详情 --> 审核判断
    审核判断 --> 审核通过: 确认无误
    审核判断 --> 审核驳回: 有问题
    审核通过 --> 发送邮件通知
    审核驳回 --> 查看待审核列表
    发送邮件通知 --> [*]
```

### 用户排单活动

```mermaid
%%{init: {'theme': 'base', 'themeVariables': { 'background': '#fff', 'primaryColor': 'transparent', 'primaryBorderColor': '#333', 'primaryTextColor': '#333', 'lineColor': '#333' }}}%%
stateDiagram-v2
    进入排单页 --> 添加分类和项
    添加分类和项 --> 填写链接备注
    填写链接备注 --> 确认提交
    确认提交 --> 待审核
    待审核 --> 查看进度: 等待管理员处理
```

### 预设标签管理

```mermaid
%%{init: {'theme': 'base', 'themeVariables': { 'background': '#fff', 'primaryColor': 'transparent', 'primaryBorderColor': '#333', 'primaryTextColor': '#333', 'lineColor': '#333' }}}%%
stateDiagram-v2
    进入配置页 --> 标签列表
    标签列表 --> 新增标签
    标签列表 --> 编辑标签
    标签列表 --> 删除标签
    新增标签 --> 填写名称排序
    填写名称排序 --> 保存
    编辑标签 --> 修改名称排序
    修改名称排序 --> 保存
    删除标签 --> 标签列表
    保存 --> 标签列表
```

## 数据库表

表4-2-1 super_admin表

字段	类型	字段长	名称
id	BIGINT	null	主键
username	VARCHAR	50	用户名
password_hash	VARCHAR	255	密码哈希
created_at	DATETIME	null	创建时间

表4-2-3 user表

字段	类型	字段长	名称
id	BIGINT	null	主键
tenant_id	VARCHAR	32	租户ID
xianyu_id	VARCHAR	100	闲鱼ID
nickname	VARCHAR	100	用户昵称
avatar_url	VARCHAR	500	用户头像
created_at	DATETIME	null	创建时间

表4-2-4 order表

字段	类型	字段长	名称
id	BIGINT	null	主键
tenant_id	VARCHAR	32	租户ID
user_id	BIGINT	null	用户ID
email	VARCHAR	100	电子邮箱
status	VARCHAR	20	排单状态
total_price	DECIMAL	10,2	总价
submitted	TINYINT	null	是否已提交
created_at	DATETIME	null	创建时间
updated_at	DATETIME	null	更新时间

表4-2-5 order_category表

字段	类型	字段长	名称
id	BIGINT	null	主键
order_id	BIGINT	null	所属排单ID
category_name	VARCHAR	50	分类名称
sort_order	INT	null	排序号
created_at	DATETIME	null	创建时间

表4-2-6 order_item表

字段	类型	字段长	名称
id	BIGINT	null	主键
category_id	BIGINT	null	所属分类ID
link_url	VARCHAR	500	链接URL
note	VARCHAR	500	备注
price	DECIMAL	10,2	价格
status	VARCHAR	20	单项状态
sort_order	INT	null	排序号
created_at	DATETIME	null	创建时间

表4-2-7 preset_tag表

字段	类型	字段长	名称
id	BIGINT	null	主键
tenant_id	VARCHAR	32	租户ID
name	VARCHAR	50	标签名称
sort_order	INT	null	排序号
created_at	DATETIME	null	创建时间

表4-2-8 system_config表

字段	类型	字段长	名称
id	BIGINT	null	主键
tenant_id	VARCHAR	32	租户ID
order_enabled	TINYINT	null	是否启用排单
announcement	TEXT	null	公告内容
