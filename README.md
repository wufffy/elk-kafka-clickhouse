# elkstack_log
# 使用环境和包版本
> linux      ELK6.6.0   JDK 1.8.0


## 整体架构图 
![avatar](https://upload-images.jianshu.io/upload_images/13279618-8eb518a8339eba4a.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1000/format/webp)

>  FileBeat 做为日志代理收集器,在每个需要收集日志的机器都安装,将日志处理收集加工,分发到logstash或者kafka中
### FileBeat 安装配置 默认配置文件是 filebeat.yml

``` yaml
filebeat.prospectors:
- type: log
  paths:
  - /data/wwwlogs/*.log
  exclude_files: ['XxlRpcRequest']
  tail_files: true
  ignore_older: 2h
  fields:
    logSource: nginx-test
  fields_under_root: true
processors:
  - drop_fields:
      fields: ["host","beat","prospector","log","input","offset"]
#  ------ out local file -----------------
output.file:
  enabled: false
  path: "/tmp/filebeat"
  filename: filebeat
#  ------ out kafka -----------------
output.kafka:
  enabled: true
  hosts: ["10.0.20.116:9092"]
  version: 2.0.0
  topic: 'filebeat_nginx_test'
  partition.round_robin:
    reachable_only: false
  required_acks: 1
  compression: gzip
  max_message_bytes: 1000000
#  ------ out elasticsearch -----------------
output.elasticsearch:
  enabled: false
  hosts: ["http://10.0.20.115:9200"]
  index: "dadaokk-%{+yyyy.MM}"
  setup.template.name: "dadaokk"
  setup.template.pattern: "dadaokk*"
#------log--------------------------
logging.level: info
logging.to_files: true
logging.to_syslog: false
logging.files:
  path: /home/filebeat/logfilebeat/
  keepfiles: 4
  name: mybeat.log
```
读取指定目录下日志文件,
-- paths 支持通配符  

-- exclude_files 如果这一行日志中包含某个匹配符 就排除  

-- tail_files  从日志文件中末尾开始读取,就是在filebeat 读取的第一次启动时开始末尾的日志文件   

-- ignore_older 如果日志文件超过两个小时没有变化,将不会在继续监听这个日志的变化  

-- fields 自定义添加的字段  

-- drop_fields 去掉一些不需要的filebeat本身附加的字段  

-- enabled: false   打开或者关闭

在这里输出到kafka,logstash 读取kafka的输入以便后续的处理

### logstash 的安装配置

logstash 在新版本中增加了通道的配置,解决之前多个输入,需要启动多个logstash的实例或者在配置文件中写if-else的问题,通过通道得到的解决

首先配置通道的配置文件
``` yaml
- pipeline.id: java
  pipeline.workers: 1
  pipeline.batch.size: 125
  path.config: "/var/data/application/logstash/logstash-6.6.0/config/conf/logstash_java.conf"
- pipeline.id: php
  pipeline.workers: 1
  pipeline.batch.size: 125
  path.config: "/var/data/application/logstash/logstash-6.6.0/config/conf/logstash_php.conf"
- pipeline.id: nginx
  pipeline.workers: 2
  pipeline.batch.size: 125
  path.config: "/var/data/application/logstash/logstash-6.6.0/config/conf/logstash_nginx.conf"

- pipeline.id: kafka
  pipeline.workers: 2
  pipeline.batch.size: 125
  path.config: "/var/data/application/logstash/logstash-6.6.0/config/conf/in-kafka-out-es.conf"
```
> path.config 就是之前具体的logstash的配置文件,可以在kibana监控页面看到启动了三个通道,分别读取java php 和 nginx kafka 输入的数据流
> 
####  logstash 具体配置

> 示例 配置kafka 输入输出到elasticsearch 中
``` js 
input{
      kafka{
        bootstrap_servers => ["10.0.20.116:9092"]
        client_id => "kafaka-test"
        group_id => "kafaka-test"
        auto_offset_reset => "latest"
        consumer_threads => 5
        decorate_events => true
        topics => ["filebeat_nginx_test_01"]
        type => "nginx"
        codec => "json"
      }
}

filter {
          mutate {
                add_field => {
                    "kafka" => "%{[@metadata][kafka]}"
                }
            }
}

output {
     elasticsearch {
         hosts => "10.0.20.115:9200"
         index => "logstash-nginx-kafka-%{+YYYY.MM}"
     }
    kafka {
        bootstrap_servers => "10.0.20.116:9092"
        topic_id => "nginx-access-log"
        compression_type => "snappy"
  }
}
```

