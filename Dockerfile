# Create builder image
FROM openjdk:17-jdk-slim as builder

# Copy maven wrapper
COPY mvnw .
COPY .mvn .mvn
RUN chmod +x ./mvnw

COPY pom.xml .
COPY customers-api/pom.xml customers-api/
COPY customers-app/pom.xml customers-app/
RUN ./mvnw dependency:go-offline

# build the image
COPY . /customer-api
WORKDIR /customer-api
RUN chmod +x ./mvnw
RUN ./mvnw -U clean install -DskipTests=true

# Create runtime image
FROM openjdk:17-jdk-slim-buster

# Add non-root user
RUN addgroup --system spring && adduser --system spring --ingroup spring
USER spring:spring

# Add actual application
COPY --from=builder /customer-api/customers-app/target/customers-app-0.0.1-SNAPSHOT.jar /app/service.jar

EXPOSE 8080
CMD ["java", "-jar", "/app/service.jar"]
