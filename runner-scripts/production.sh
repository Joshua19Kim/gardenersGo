fuser -k 10500/tcp || true
source staging/.env

java -jar production/libs/gardeners-grove-0.0.1-SNAPSHOT.jar \
    --server.port=10500 \
    --server.servlet.contextPath=/prod \
    --spring.application.name=gardeners-grove
