# 个性化饮食健康管理系统

基于 Spring Boot 的饮食计划管理系统，根据减重、增肌和均衡目标生成早餐、午餐、晚餐与加餐组合，并汇总热量、蛋白质、脂肪和碳水化合物。

## 功能

- 用户注册、登录与个人资料管理
- 按健康目标生成每日饮食计划
- 计划与历史记录查询
- 餐次和食物明细的事务化保存
- Chart.js 营养构成图表
- 管理员用户检索

## 技术栈

- Java 8
- Spring Boot 2.7
- MyBatis-Plus
- MySQL
- Thymeleaf
- Chart.js

## 本地运行

1. 在 MySQL 中导入 `dhm.sql`。
2. 设置 `DB_URL`、`DB_USERNAME` 和 `DB_PASSWORD` 环境变量；变量示例见 `.env.example`。
3. 使用 Maven 启动：

```bash
./mvnw spring-boot:run
```

若本机未保留 Maven Wrapper，可使用 `mvn spring-boot:run`。

## 说明

本项目形成于 2025 年课程实践，用于展示规则驱动的饮食计划编排、关系数据持久化和服务端页面渲染。源码按学习项目原貌公开，未按现代生产系统标准进行安全加固。
