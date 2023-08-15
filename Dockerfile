FROM eclipse-temurin:17
EXPOSE 8080
ENTRYPOINT ["mvnw","spring-boot:run"]