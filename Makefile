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
# Run mvn spotless:apply verify and open JaCoCo report
verify-and-open-report:
	@cd backend/monolith-proxy && mvn spotless:apply verify
	@open backend/monolith-proxy/target/site/jacoco/index.html