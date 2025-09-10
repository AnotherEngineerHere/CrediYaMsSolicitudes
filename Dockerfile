FROM eclipse-temurin:21-jdk-alpine
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser
VOLUME /tmp
ARG JAR_FILE=applications/app-service/build/libs/CrediYaMsSolictudes.jar
COPY ${JAR_FILE} CrediYaMsSolictudes.jar
ENV JAVA_OPTS=" -Xshareclasses:name=cacheapp,cacheDir=/cache,nonfatal -XX:+UseContainerSupport -XX:MaxRAMPercentage=70 -Djava.security.egd=file:/dev/./urandom"
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS  -jar CrediYaMsSolictudes.jar" ]
