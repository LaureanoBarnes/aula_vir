FROM openjdk:23
ARG JAR_FILE=target/HolaSpring-0.0.1.jar
COPY ${JAR_FILE} app_trabajov3.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app_trabajov3.jar"]