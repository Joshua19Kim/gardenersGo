fuser -k 9500/tcp || true
source staging/.env

java -jar staging/libs/gardeners-grove-0.0.1-SNAPSHOT.jar \
    --server.port=9500 \
    --server.servlet.contextPath=/test \
    --spring.application.name=gardeners-grove \
    --server.url=https://csse-seng302-team700.canterbury.ac.nz/test
