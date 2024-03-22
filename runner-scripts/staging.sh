fuser -k 9500/tcp || true

java -jar staging/libs/gardeners-grove-0.0.1-SNAPSHOT.jar \
    --server.port=9500 \
    --server.servlet.contextPath=/test \
    --spring.application.name=gardeners-grove
