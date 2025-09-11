FROM eclipse-temurin:21-jdk-alpine

# Usuario no root
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Directorio de trabajo
WORKDIR /app

# OJO: verifica el nombre del JAR. Tienes "Solictudes" (con 't') en el path.
# Si tu build realmente genera ese nombre, deja igual; si no, corrígelo a "Solicitudes".
ARG JAR_FILE=applications/app-service/build/libs/CrediYaMsSolictudes.jar

# Copia el jar
COPY ${JAR_FILE} app.jar

# No definas JAVA_OPTS aquí para evitar duplicados.
# Puedes mantener esta propiedad segura para entropía:
ENV JAVA_SECURITY_EGD="file:/dev/./urandom"

# ENTRYPOINT en forma exec, sin 'sh -c', y sin flags aquí (vendrán de JAVA_TOOL_OPTIONS)
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app/app.jar"]
