# 知华科技 ZhuaTech CDP

[简体中文](README.md) | [English](README.en.md)

**客户数据平台 · Community Source Edition**

[![Java 21](https://img.shields.io/badge/Java-21-2f6f75)](backend/pom.xml) [![Vue 3](https://img.shields.io/badge/Vue-3-42b883)](frontend/package.json) [![MySQL 8](https://img.shields.io/badge/MySQL-8-4479a1)](compose.yaml) [![个人非商用](https://img.shields.io/badge/license-personal%20non--commercial-b47b3a)](LICENSE)

汇聚多源客户数据，完成身份合并、人群洞察和可审计的渠道激活。

这是上海如静知华信息科技有限公司提供的企业应用工程示例，官网：[www.zhuatech.cn](https://www.zhuatech.cn/)。仓库重点呈现一套能启动、能测试、有数据库迁移、有角色权限、有管理端与岗位端的完整应用骨架。

## 场景切片

### 客户数据运营驾驶舱

![客户数据运营驾驶舱](docs/images/cdp-customer-dashboard.png)

### 受众分群与激活台账

![受众分群与激活台账](docs/images/cdp-audience-register.png)

### 营销运营工作台

![营销运营工作台](docs/images/cdp-activation-workbench.png)

## 系统覆盖什么

1. 数据源、客户身份与统一档案
2. 标签规则、动态分群与身份问题
3. 激活任务、渠道记录和人群效果分析

业务链路：`数据接入 → 身份解析 → 标签计算 → 人群圈选 → 渠道激活 → 效果回流`

## 面向开发者

| 部分 | 技术与职责 |
| --- | --- |
| 后端 | Java 21、Spring Boot、Spring Security、JPA、Flyway |
| 前端 | Vue 3、Pinia、Vue Router、Axios、Vite，响应式管理端与 H5 岗位端 |
| 数据 | MySQL 8；H2 集成测试 |
| 交付 | Docker Compose、Nginx、环境变量配置 |

Java 工程包名为 `cn.zhuatech.cdp`，数据库名为 `zhuatech_cdp`。角色覆盖营销运营专员、CDP 经理、数据治理人员、系统管理员。

### 目录

- `backend/`：REST API、JWT、领域模型、Flyway 与测试
- `frontend/`：Vue 管理端、响应式 H5 工作台与演示数据
- `docs/`：架构、接口、数据库说明与页面截图
- `compose.yaml`：MySQL、Java 服务与 Nginx 一键编排

## 快速上手

仅看演示界面：

```bash
cd frontend
npm install
npm run dev:demo
```

打开 `http://localhost:5173`。管理端账号 `planner / Demo@2026`，岗位端账号 `operator / Demo@2026`。

完整启动：

```bash
cp .env.example .env
# 修改数据库密码与 JWT_SECRET
docker compose up --build
```

## 新增：客户激活就绪度

新增 `POST /api/admin/activation-readiness`，从身份匹配率、授权覆盖率、可触达率、画像陈旧率和可用渠道数量评估受众能否激活，返回 `READY`、`REVIEW` 或 `BLOCK` 决策，避免未授权或数据质量不足的人群进入营销渠道。

## 上线检查

仓库中的账号、客户、指标、工单和经营数据均为虚构演示数据。正式落地时应更换默认密码与 JWT 密钥，配置 HTTPS、最小权限、数据库备份、操作审计、脱敏策略，并按照所在行业完成安全与合规评估。

## 非商业许可

本工程仅允许个人、非商业性的学习、研究和技术交流，**不得商用**。企业内部使用、生产部署、SaaS、客户交付、收费培训、咨询实施及品牌替换，均须事先取得上海如静知华信息科技有限公司书面授权。完整条款见 [LICENSE](LICENSE)。

需要深度开发、私有化部署、系统集成或商业授权，请访问[知华科技官网](https://www.zhuatech.cn/)，也可扫码添加微信咨询：

| 微信咨询 1 | 微信咨询 2 |
| --- | --- |
| ![知华科技微信咨询二维码 1](docs/images/zhuatech-wechat-consulting.png) | ![知华科技微信咨询二维码 2](docs/images/zhuatech-wechat-consulting-2.png) |

相关检索：CDP 源码、客户数据平台、用户标签、人群分群、Java CDP、Vue CDP、上海如静知华信息科技有限公司。

## 渠道授权激活门禁

新增 `POST /api/cdp/insights/consent-activation-guard`，在 EMAIL、SMS、PUSH、ADS 和 WECHAT 人群下发前扣除退订抑制名单与陈旧身份，计算可触达人群和授权覆盖率，并输出 `RELEASE / REVIEW / BLOCK`。未配置处理目的或授权覆盖不足时将阻止发布并给出修复动作。

## 企业级客户身份合并治理

新增 `POST /api/enterprise/cdp/identity-merge-governance`，将匹配置信度、来源字段、授权、数据驻留、敏感标识与黄金档案责任统一纳入合并决策，返回 `AUTO_MERGE / REVIEW / BLOCKED`。详见 [身份合并说明](docs/ENTERPRISE_IDENTITY_MERGE.md)。
