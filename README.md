# Workflow Orchestrator

A lightweight, distributed workflow orchestration engine designed to decouple state management from task execution. This project demonstrates a scalable pattern where a central **Orchestrator** manages the lifecycle of a workflow, while distributed **Workers** (written in any language) execute the actual business logic via **Kafka**.

## Architecture

The system consists of three main parts:
1.  **Orchestrator (Java/Spring Boot):** Exposes a REST API to define and trigger workflows. It manages state in **PostgreSQL** and **Redis**, pushing "Jobs" to Kafka and listening for "Results".
2.  **Message Broker (Kafka):** Acts as the asynchronous communication layer.
    *   `workflow-jobs`: The Orchestrator publishes tasks here.
    *   `workflow-results`: Workers publish completed task results here.
3.  **Workers (Polyglot):** Stateless services that listen for specific tasks, execute them, and return results. SDKs are provided for **Java**, **Python**, and **Node.js**.

## Getting Started

The entire infrastructure, including the orchestrator, databases, message broker, and example workers, can be spun up using Docker Compose.

### Prerequisites
*   Docker & Docker Compose

### Run the Stack

1. Navigate to the infrastructure directory:
   ```bash
   cd infra
   ```

2. Start the services:
   ```bash
   docker-compose up --build
   ```

This will spin up the following containers:
*   **`orchestrator`**: The main engine (Port `8080`).
*   **`kafka`**: Message broker (Ports `9092`, `9094`).
*   **`postgres`**: Persists workflow definitions and run history.
*   **`redis`**: Caches active workflow state for high performance.
*   **`example-worker-java`**: A sample worker written in Java.
*   **`example-worker-node`**: A sample worker written in TypeScript/Node.
*   **`example-worker-python`**: A sample worker written in Python.

## API Usage

### 1. Define a Workflow
Create a new workflow definition by specifying the sequence of steps (actions).

**POST** `http://localhost:8080/workflow/`

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

**POST** `http://localhost:8080/workflow/my-first-workflow/trigger`

```json
{
  "input": "Hello World"
}
```
*Returns a `runId` (e.g., `550e8400-e29b-41d4-a716-446655440000`).*

### 3. Check Status
Monitor the progress of a specific run.

**GET** `http://localhost:8080/workflow/run/{runId}`

---

## Developing Workers

Workers use the provided SDKs to connect to the orchestrator. You simply register a handler function for a specific `action` string.

### Python Worker
Located in `examples/python/`.

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
Located in `examples/node/`.

```typescript
import { Worker } from 'workersdk';

const worker = new Worker();

worker.register('step1', (payload: string) => {
    console.log('Handling step 1');
    return `Node processed: ${payload}`;
});

worker.start();
```

### Java Worker
Located in `examples/java/`.

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

*   **Core:** Java 21, Spring Boot 3
*   **Messaging:** Apache Kafka
*   **Database:** PostgreSQL 16
*   **Caching:** Redis 7
*   **SDK Languages:** Java, Python 3.11, Node.js 20 (TypeScript)