# --- Variables ---
DC = docker compose
SERVICES_CORE = postgres redis kafka orchestrator

# --- Targets ---

.PHONY: up-infra java python node all down clean ps logs-main

up-infra:
	$(DC) up -d $(SERVICES_CORE)

java:
	$(DC) --profile java up -d

python:
	$(DC) --profile python up -d

node:
	$(DC) --profile node up -d

all:
	$(DC) --profile all up -d

down:
	$(DC) down

clean:
	$(DC) down -v

ps:
	$(DC) ps

logs-main:
	$(DC) logs -f orchestrator
