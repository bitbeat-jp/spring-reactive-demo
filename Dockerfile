FROM eclipse-temurin:17-jdk AS build
COPY ./ /home/app/
RUN cd /home/app && ./mvnw clean package

FROM eclipse-temurin:17-jdk-alpine
COPY --from=build /home/app/target/spring-reactive-demo-0.0.1-SNAPSHOT.jar spring-reactive-demo.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","spring-reactive-demo.jar"]