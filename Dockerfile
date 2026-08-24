FROM maven:3.9.6-eclipse-temurin-17 AS build 
WORKDIR /app

COPY pom.xml ./
RUN mvn -B dependency:go-offline || true

COPY src ./src
RUN mvn -B clean package -DskipTests && cp target/*-bootable.jar /app/application.jar

FROM eclipse-temurin:17-jre
WORKDIR /opt/app

COPY --from=build /app/application.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-Djboss.bind.address=0.0.0.0", "-jar", "app.jar"]

