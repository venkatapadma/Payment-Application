# Use the eclipse-temurin OpenJDK 11 image as the base image
FROM eclipse-temurin:21-jre-alpine

#Create app directory
WORKDIR /app

# Copy application JAR
COPY target/*.jar user-service.jar

#Expose application port
EXPOSE 8081

#Run the application
ENTRYPOINT ["java", "-jar", "user-service.jar"]
