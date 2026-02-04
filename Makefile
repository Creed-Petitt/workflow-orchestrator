# --- Variables ---
DC = docker compose
SERVICES_CORE = postgres redis kafka orchestrator

# --- Targets ---

.PHONY: up-infra java python node all down clean ps logs-main rebuild

# 1. Just the core infrastructure + Orchestrator
up-infra:
	$(DC) up -d $(SERVICES_CORE)

# 2. Start specific language workers (automatically starts infra)
java:
	$(DC) --profile java up -d

python:
	$(DC) --profile python up -d

node:
	$(DC) --profile node up -d

# 3. Start everything at once
all:
	$(DC) --profile all up -d

# 4. Stop and remove containers (Explicitly target all profiles)
down:
	$(DC) --profile java --profile python --profile node --profile all down --remove-orphans

# 5. Full wipe (removes volumes like DB data)
clean:
	$(DC) --profile java --profile python --profile node --profile all down -v --remove-orphans

# 6. Rebuild Orchestrator (Docker handles compilation now!)
rebuild:
	@echo "🐳 Rebuilding Docker Image (and compiling Java inside)..."
	$(DC) build --no-cache orchestrator
	$(DC) up -d orchestrator
	@echo "✅ Orchestrator updated!"

# 7. Quick Status
ps:
	$(DC) ps

# 8. Follow logs for the orchestrator
logs-main:
	$(DC) logs -f orchestrator