package kafka.streams.demos.low;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyProcessor implements Processor<String, String> {

    private ProcessorContext context;
    private KeyValueStore<String, Long> kvStore;

    private static final Logger LOGGER = LoggerFactory.getLogger(MyProcessor.class);

    @Override
    public void init(ProcessorContext processorContext) {
        // keep the processor context locally because we need it in punctuate() and commit()
        this.context = processorContext;

        // call this processor's punctuate() method every 1000 milliseconds.
        this.context.schedule(1000);

        // retrieve the key-value store named "Counts"
        this.kvStore = (KeyValueStore<String, Long>) context.getStateStore("Counts");
    }

    //    分词计数
    @Override
    public void process(String dummy, String line) {
        String[] words = line.toLowerCase().split(" ");

        for (String word : words) {
            Long oldValue = this.kvStore.get(word);

            if (oldValue == null) {
                this.kvStore.put(word, 1L);
            } else {
                this.kvStore.put(word, oldValue + 1L);
            }
            LOGGER.debug("process {} {}", word, this.kvStore.get(word));

        }
    }

    //迭代本地 state store 中的数据并聚合并发送给下游的处理器，并提交当前流状态
    @Override
    public void punctuate(long l) {
        KeyValueIterator<String, Long> iter = this.kvStore.all();

        while (iter.hasNext()) {
            KeyValue<String, Long> entry = iter.next();
            context.forward(entry.key, entry.value.toString());
            LOGGER.debug("punctuate {} {}", entry.key, entry.value.toString());
        }

        iter.close();
        // commit the current processing progress
        context.commit();
    }

    @Override
    public void close() {

    }
}
