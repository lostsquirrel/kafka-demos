# encoding: utf-8
import multiprocessing
import threading
import time
from typing import List

from kafka.consumer import KafkaConsumer


class Consumer(threading.Thread):
    daemon = True

    def __init__(self, bootstrap_servers: str, group: str, topics: List):
        threading.Thread.__init__(self)
        self.stop_event = multiprocessing.Event()

        self.bootstrap_servers = bootstrap_servers
        self.group = group
        self.topics = topics

    def run(self):
        consumer = KafkaConsumer(bootstrap_servers=self.bootstrap_servers,
                                 auto_offset_reset='earliest',
                                 group_id=self.group,
                                 consumer_timeout_ms=1000)
        consumer.subscribe(self.topics)
        while not self.stop_event.is_set():
            for message in consumer:
                print(message)
                if self.stop_event.is_set():
                    break

        consumer.close()


if __name__ == '__main__':
    s = "localhost:9092"
    x = Consumer(s, "test-123", ["sygs_hc_data"])
    x.start()
    time.sleep(60)
