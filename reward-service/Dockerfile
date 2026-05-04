# Use the eclipse-temurin OpenJDK 11 image as the base image
FROM eclipse-temurin:21-jre-alpine

#Create app directory
WORKDIR /app

# Copy application JAR
COPY target/*.jar reward-service.jar

#Expose application port
EXPOSE 8084

#Run the application
ENTRYPOINT ["java", "-jar", "reward-service.jar"]
