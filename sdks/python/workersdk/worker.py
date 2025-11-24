import json
import time
from typing import Callable, Dict
from kafka import KafkaConsumer, KafkaProducer
from .models import JobMessage, ResultMessage

class Worker:
    def __init__(self):
        self.handlers: Dict[str, Callable[[str], str]] = {}
        self.consumer = None
        self.producer = None

    def register(self, action: str, handler: Callable[[str], str]):
        self.handlers[action] = handler

    def start(self, bootstrap_servers: str = 'kafka:9092'):
        # Retry connection to Kafka
        for attempt in range(30):
            try:
                self.consumer = KafkaConsumer(
                    'workflow-jobs',
                    bootstrap_servers=bootstrap_servers,
                    group_id='workflow-workers',
                    auto_offset_reset='earliest',
                    value_deserializer=lambda m: json.loads(m.decode('utf-8'))
                )
                self.producer = KafkaProducer(
                    bootstrap_servers=bootstrap_servers,
                    value_serializer=lambda v: json.dumps(v).encode('utf-8')
                )
                break
            except Exception as e:
                print(f"Kafka not ready, retrying ({attempt + 1}/30)...")
                time.sleep(2)
        else:
            raise Exception("Could not connect to Kafka after 30 attempts")

        print("Worker started, listening for jobs...")

        for message in self.consumer:
            job = JobMessage.from_dict(message.value)

            print(f"Received job: {job}")

            handler = self.handlers.get(job.action)
            if not handler:
                print(f"No handler for {job.action}")
                continue

            try:
                result = handler(job.payload)
            except Exception as e:
                result = f'{{"error": "{str(e)}"}}'

            response = ResultMessage(job.workflow_run_id, job.action, result)

            self.producer.send('workflow-results', value=response.to_dict())

        self.producer.flush()