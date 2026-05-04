# Use the eclipse-temurin OpenJDK 11 image as the base image
FROM eclipse-temurin:21-jre-alpine

#Create app directory
WORKDIR /app

# Copy application JAR
COPY target/*.jar notification-service.jar

#Expose application port
EXPOSE 8083

#Run the application
ENTRYPOINT ["java", "-jar", "notification-service.jar"]