## clickhouse 简单入门
#### docker 服务端运行
```
docker run -p 8123:8123 -p 9000:9000 -p 9009:9009 --name my-clickhouse-server -d yandex/clickhouse-server
```
可以通过 docker ps 查看运行的实例
### docker 客户端运营

```
docker run -it --rm --link my-clickhouse-server:clickhouse-server yandex/clickhouse-client --host clickhouse-server
```
进入客户端连接
** 我是在图形客户端 DBeaver执行的sql语句,如果在自带的客户端执行,在换行语句前的每行前 需要加 '\'
#### 从MySQL导入数据 
> 首先创建对应的表
``` SQL
-- 创建表
CREATE TABLE IF NOT EXISTS admin
(
  `id` UInt16,
  `name` String,
  `password` String,
  `email` String,
  `phone` FixedString(11),
  `logo` String,
  `loginIp` String,
  `loginTime` DateTime,
  `status` UInt8,
  `cTime` String,
  `uTime` String
) ENGINE = MergeTree ORDER BY id ;
```
> 用insert to 语句导入数据
```SQL
insert
	into
		`default`.admin SELECT
			*
		FROM
			mysql('10.0.20.202:3306',
			'dct',
			'admin',
			'duocaitou',
			'J8UfRvLEiUPz')

```
#### 从kafka中导入数据
### 更多知识学习中 参考文档 https://clickhouse.yandex/docs/zh/getting_started/