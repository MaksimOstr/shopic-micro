# -----------------------------------------------------------------------------
# Stage 1: Extractor
# This stage is used solely to extract layers from the "fat" JAR.
# We use JRE instead of JDK because no compilation is needed here.
# -----------------------------------------------------------------------------
FROM eclipse-temurin:24-jre-alpine AS build

WORKDIR /app

COPY build/libs/*.jar application.jar

RUN java -Djarmode=layertools -jar application.jar extract

# -----------------------------------------------------------------------------
# Stage 2: Runner
# Final image
# -----------------------------------------------------------------------------
FROM eclipse-temurin:24-jre-alpine

WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring

COPY --from=build /app/dependencies/ ./
COPY --from=build /app/spring-boot-loader/ ./
COPY --from=build /app/snapshot-dependencies/ ./
COPY --from=build /app/application/ ./

RUN chown -R spring:spring /app
USER spring

HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
  CMD wget -q -O - ${PAYMENT_SERVICE_URL}/actuator/health || exit 1

EXPOSE ${PAYMENT_SERVICE_PORT}

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]