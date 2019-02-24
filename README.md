# short-url QuickStart
# 短网址服务 快速开始

## 运行环境
- Mysql（standalone模式或proxy代理均可，本demo的DB客户端不适配分布式DB）
- REDIS（standalone模式或proxy代理均可，本demo的REDIS客户端不适配主从模式或分布式模式）
- Maven
- Java 8

## DDL
- 运行本demo前请先执行本DDL
```
CREATE TABLE `short_url` (
  `id` bigint(13) NOT NULL AUTO_INCREMENT,
  `url` VARCHAR(255) NOT NULL,
  `code` VARCHAR(32) NOT NULL UNIQUE,
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  `count` bigint(13) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`),
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
```

## application.properties配置
- 请至少确保以下几个配置正确
```
spring.datasource.url=jdbc:mysql://127.0.0.1:3306/short_url
spring.datasource.username=root
spring.datasource.password=123456

spring.redis.host=127.0.0.1
spring.redis.port=6379
```

## 运行
```
cd ${project.dir}
mvn clean package
java -jar ./target/short-url-0.0.1-SNAPSHOT.jar
```

## 访问demo页面
请访问 `http://127.0.0.1:8080` 即可