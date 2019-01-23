package kafka.streams.demos.low;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TransProcessor implements Processor<byte[], byte[]> {

    private ProcessorContext context;
    
    private KeyValueStore<String, String> kvStore;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(TransProcessor.class);

    @Override
    public void init(ProcessorContext context) {
        // keep the processor context locally because we need it in punctuate() and commit()
        this.context = context;

        // call this processor's punctuate() method every 50 milliseconds.
        this.context.schedule(50);

        // retrieve the key-value store named "content"
        this.kvStore = (KeyValueStore<String, String>) context.getStateStore("Contents");
    }

    @Override
    public void process(byte[] key, byte[] value) {
        String sk = null;
        String sv = null;
        if (key != null && key.length > 0) {
            sk = new String(key);
        } else {
            sk = "" + System.currentTimeMillis();
        }
        if (value != null && value.length > 0) {
            sv = new String(value);
            this.kvStore.put(sk, sv);
        }

    }

    @Override
    public void punctuate(long timestamp) {
        KeyValueIterator<String, String> iter = this.kvStore.all();

        while (iter.hasNext()) {
            KeyValue<String, String> entry = iter.next();
            context.forward(entry.key, entry.value);
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
