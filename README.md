# short-url
短网址服务

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