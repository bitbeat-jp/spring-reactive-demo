FROM eclipse-temurin:17
RUN ./mvnw clean package
EXPOSE 8080
ENTRYPOINT ["java","-jar","./target/spring-reactive-demo-0.0.1-SNAPSHOT.jar"]