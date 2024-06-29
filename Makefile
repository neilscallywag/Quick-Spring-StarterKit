# Project-specific variables
PROJECT_NAME=starterkit
LOCAL_DEPLOY_DIR=.

# ---------------------------------------
# For deploying docker containers locally
# ---------------------------------------
up: 
	@docker compose -p ${PROJECT_NAME} \
		-f ${LOCAL_DEPLOY_DIR}/docker-compose.yml \
		up --build -d --remove-orphans

down:
	@docker compose -p ${PROJECT_NAME} \
		-f ${LOCAL_DEPLOY_DIR}/docker-compose.yml \
		down

down-clean: down
	@docker compose -p ${PROJECT_NAME} \
		-f ${LOCAL_DEPLOY_DIR}/docker-compose.yml \
		down -v

build:
	@docker compose -p ${PROJECT_NAME} \
		-f ${LOCAL_DEPLOY_DIR}/docker-compose.yml \
		build
