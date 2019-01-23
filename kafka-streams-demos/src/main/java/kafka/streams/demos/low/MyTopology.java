package kafka.streams.demos.low;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.processor.StateStoreSupplier;
import org.apache.kafka.streams.processor.TopologyBuilder;
import org.apache.kafka.streams.state.Stores;

import java.util.Properties;

public class MyTopology {

    public static void main(String[] args) {

        StateStoreSupplier countStore = Stores.create("Counts")
                .withKeys(Serdes.String())
                .withValues(Serdes.Long())
                .persistent()
                .build();

        StateStoreSupplier contentStore = Stores.create("Contents")
                .withKeys(Serdes.String())
                .withValues(Serdes.String())
                .persistent()
                .build();

        TopologyBuilder builder = new TopologyBuilder();

        builder.addSource("SOURCE", "src-topic")
                // add "PROCESS1" node which takes the source processor "SOURCE" as its upstream processor
                .addProcessor("PROCESS1", () -> new MyProcessor(), "SOURCE")

                .addStateStore(countStore, "PROCESS1")
                // add "PROCESS2" node which takes "PROCESS1" as its upstream processor
//                .addProcessor("PROCESS2", () -> new MyProcessor2(), "PROCESS1")

                // add "PROCESS3" node which takes "PROCESS1" as its upstream processor
//                .addProcessor("PROCESS3", () -> new MyProcessor3(), "PROCESS1")

                // add the sink processor node "SINK1" that takes Kafka topic "sink-topic1"
                // as output and the "PROCESS1" node as its upstream processor
                .addSink("SINK1", "sink-topic1", "PROCESS1")

                // add the sink processor node "SINK2" that takes Kafka topic "sink-topic2"
                // as output and the "PROCESS2" node as its upstream processor
//                .addSink("SINK2", "sink-topic2", "PROCESS2")

                // add the sink processor node "SINK3" that takes Kafka topic "sink-topic3"
                // as output and the "PROCESS3" node as its upstream processor
//                .addSink("SINK3", "sink-topic3", "PROCESS3")
        ;

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-topology-example");

        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

        props.put(StreamsConfig.KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        KafkaStreams ks = new KafkaStreams(builder, props);
        ks.start();
    }
}
