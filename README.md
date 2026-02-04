# Workflow Orchestrator

A lightweight, distributed workflow orchestration engine designed to decouple state management from task execution. This project demonstrates a scalable pattern where a central Orchestrator manages the lifecycle of a workflow, while distributed Workers (written in any language) execute the actual business logic via Kafka.

## Architecture

The system consists of three main parts:
1. Orchestrator (Java/Spring Boot): Exposes a REST API to define and trigger workflows. It manages state in PostgreSQL and Redis, pushing "Jobs" to Kafka and listening for "Results".
2. Message Broker (Kafka): Acts as the asynchronous communication layer.
    * workflow-jobs: The Orchestrator publishes tasks here.
    * workflow-results: Workers publish completed task results here.
3. Workers (Polyglot): Stateless services that listen for specific tasks, execute them, and return results. SDKs are provided for Java, Python, and Node.js.

## Getting Started

The entire infrastructure can be spun up using Docker Compose. A Makefile is provided for convenience.

### Prerequisites
* Docker & Docker Compose
* Make (optional, but recommended)

### Run the Stack

**Option 1: Using Make (Recommended)**

```bash
# Start everything (Java, Node, Python workers)
make all

# Start specific workers
make java
make node
make python

# Stop everything
make stop

# Wipe data (Database & Redis)
make clean
```

**Option 2: Using Docker Compose directly**

```bash
docker compose --profile all up -d --build
```

### Dashboard
Once running, verify the system status:
* Active Runs: http://localhost:8080/workflow/runs
* Workflow Definitions: http://localhost:8080/workflow/

## Reliability and Fault Tolerance

This engine is built for resilience. It handles network failures, worker crashes, and duplicate requests gracefully.

### 1. Idempotency (Redis Locks)
To prevent duplicate execution, the Orchestrator enforces idempotency via Redis.
* Mechanism: When triggering a workflow, clients can send an Idempotency-Key header.
* Behavior: The system checks Redis for this key. If found, it returns the existing runId immediately without spawning a new workflow.

### 2. Automatic Retries
Workers are decoupled from the Orchestrator via Kafka. If a worker fails (e.g., database glitch, API timeout):
* The system automatically retries the task 3 times.
* It uses a 1-second delay between attempts to allow the downstream system to recover.

### 3. Dead Letter Queues (DLQ)
If a task fails after all retries (e.g., a "Poison Pill" message that always crashes the worker):
* The message is moved to a specific Dead Letter Topic (workflow-jobs.DLT).
* This prevents the bad message from blocking the partition and allows the rest of the system to continue processing.

## API Usage

### 1. Define a Workflow
Create a new workflow definition by specifying the sequence of steps (actions).

**POST** http://localhost:8080/workflow/

```json
{
  "id": "my-first-workflow",
  "steps": [
    {
      "action": "step1",
      "stepIndex": 0
    },
    {
      "action": "step2",
      "stepIndex": 1
    }
  ]
}
```

### 2. Trigger a Workflow
Start an instance of a defined workflow with an initial input.

**POST** http://localhost:8080/workflow/my-first-workflow/trigger

**Headers:**
* Idempotency-Key: (Optional) unique-request-id to prevent double-execution.

**Body:**
```json
{
  "input": "Hello World"
}
```
*Returns a runId (e.g., 550e8400-e29b-41d4-a716-446655440000).*

### 3. Check Status
Monitor the progress of a specific run.

**GET** http://localhost:8080/workflow/run/{runId}

---

## Developing Workers

Workers use the provided SDKs to connect to the orchestrator. You simply register a handler function for a specific action string.

### Python Worker
Located in examples/python/.

```python
from workersdk import Worker

def main():
    worker = Worker()

    # Register handlers for specific actions
    worker.register("step1", lambda payload: f"Processed: {payload}")
    
    # Connects to Kafka and starts listening
    worker.start()

if __name__ == "__main__":
    main()
```

### Node.js Worker
Located in examples/node/.

```typescript
import { Worker } from 'workersdk';

const worker = new Worker();

worker.register('step1', (payload: string) => {
    console.log('Handling step 1');
    return `Node processed: ${payload}`;
});

// Unique Group ID to avoid conflicts
worker.start("kafka:9092", "workflow-workers-node");
```

### Java Worker
Located in examples/java/.

```java
import com.creedpetitt.workersdk.Worker;

public class ExampleWorkerApp {
    public static void main(String[] args) {
        Worker worker = new Worker();

        worker.register("step1", payload -> {
            return "Java processed: " + payload;
        });

        worker.start();
    }
}
```

## Tech Stack

* Core: Java 21, Spring Boot 3
* Messaging: Apache Kafka (KRaft Mode)
* Database: PostgreSQL 16
* Caching: Redis 7
* SDK Languages: Java, Python 3.11, Node.js 20 (TypeScript)
