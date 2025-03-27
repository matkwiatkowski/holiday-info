FROM openjdk:21-jdk

VOLUME /tmp

ARG JAR_FILE
COPY /target/${JAR_FILE} app.jar

HEALTHCHECK --interval=1m --timeout=6s --start-period=5s --retries=5 \
  CMD if curl -s -k http://localhost:${SERVER_PORT:-8080}/actuator/health/ignore-mail | grep UP; then exit 0 ; else exit 1 ; fi

COPY docker-entrypoint.sh /
ENTRYPOINT ["/docker-entrypoint.sh"]
