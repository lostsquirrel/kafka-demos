# Kafka Stream Demos

## Kafka Server

```bash
docker run --rm --name kafka_test --net host registry.lisong.pub:5000/lisong/kafka:2.11-0.10.2.1
docker exec -it kafka_test bash

```
## WordCount 

－ 创建输入主题(topic)

```sh
export ZK_HOSTS=localhost:2181
bin/kafka-topics.sh --create \
    --zookeeper $ZK_HOSTS \
    --replication-factor 1 \
    --partitions 1 \
    --topic streams-file-input
```

－ 创建输出主题(topic)

```sh
bin/kafka-topics.sh --create \
    --zookeeper $ZK_HOSTS \
    --replication-factor 1 \
    --partitions 1 \
    --topic streams-wordcount-output \
    --config cleanup.policy=compact

```

－ 启动生产者

```bash
export KAFKA_SERVERS=localhost:9092
bin/kafka-console-producer.sh --broker-list $KAFKA_SERVERS --topic streams-plaintext-input
```
- 启动消费者

```bash
export KAFKA_SERVERS=localhost:9092
bin/kafka-console-consumer.sh --bootstrap-server $KAFKA_SERVERS \
    --topic streams-wordcount-output \
    --from-beginning \
    --formatter kafka.tools.DefaultMessageFormatter \
    --property print.key=true \
    --property print.value=true \
    --property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer \
    --property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer
```

- 测试

    在生产者发送数据
    在消费者查看结果
    
## LineSplit

－ 创建输出主题(topic)

```sh
bin/kafka-topics.sh --create \
    --zookeeper $ZK_HOSTS \
    --replication-factor 1 \
    --partitions 1 \
    --topic streams-linesplit-output

```
－　启动生产者
    可继续使用 WordCount 的生产者

- 启动消费者

```bash
export KAFKA_SERVERS=localhost:9092
bin/kafka-console-consumer.sh --bootstrap-server $KAFKA_SERVERS \
    --topic streams-linesplit-output \
    --from-beginning \
    --formatter kafka.tools.DefaultMessageFormatter \
    --property print.key=true \
    --property print.value=true \
    --property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer \
    --property value.deserializer=org.apache.kafka.common.serialization.StringDeserializer
```

## Pipe

```sh
bin/kafka-topics.sh --create \
    --zookeeper $ZK_HOSTS \
    --replication-factor 1 \
    --partitions 1 \
    --topic streams-pipe-output

```
－　启动生产者
    可继续使用 WordCount 的生产者

- 启动消费者

```bash
export KAFKA_SERVERS=localhost:9092
bin/kafka-console-consumer.sh --bootstrap-server $KAFKA_SERVERS \
    --topic streams-pipe-output \
    --from-beginning \
    --formatter kafka.tools.DefaultMessageFormatter \
    --property print.key=true \
    --property print.value=true \
    --property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer \
    --property value.deserializer=org.apache.kafka.common.serialization.StringDeserializer