# Low-Level Processor API

## src

```sh
export ZK_HOSTS=localhost:2181
bin/kafka-topics.sh --create \
    --zookeeper $ZK_HOSTS \
    --replication-factor 1 \
    --partitions 1 \
    --topic src-topic
```

## sink

```sh
export ZK_HOSTS=localhost:2181
bin/kafka-topics.sh --create \
    --zookeeper $ZK_HOSTS \
    --replication-factor 1 \
    --partitions 1 \
    --topic sink-topic1
bin/kafka-topics.sh --create \
    --zookeeper $ZK_HOSTS \
    --replication-factor 1 \
    --partitions 1 \
    --topic sink-topic2
bin/kafka-topics.sh --create \
    --zookeeper $ZK_HOSTS \
    --replication-factor 1 \
    --partitions 1 \
    --topic sink-topic3
```

－ 启动生产者

```bash
export KAFKA_SERVERS=localhost:9092
bin/kafka-console-producer.sh --broker-list $KAFKA_SERVERS --topic src-topic
```
- 启动消费者

```bash
export KAFKA_SERVERS=localhost:9092
bin/kafka-console-consumer.sh --bootstrap-server $KAFKA_SERVERS \
    --topic sink-topic1 \
    --from-beginning \
    --formatter kafka.tools.DefaultMessageFormatter \
    --property print.key=true \
    --property print.value=true \
    --property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer \
    --property value.deserializer=org.apache.kafka.common.serialization.StringDeserializer
```