FROM maven:3.8.8-eclipse-temurin-8 AS build
WORKDIR /src
COPY . .
RUN mvn -pl web -am clean package -DskipTests

FROM eclipse-temurin:8-jre
WORKDIR /app
COPY --from=build /src/web/target/web-1.0.0.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java","-XX:+UseContainerSupport","-Xms256m","-Xmx512m","-jar","/app/app.jar"]
