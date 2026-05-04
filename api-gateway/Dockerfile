# Use the eclipse-temurin OpenJDK 11 image as the base image
FROM eclipse-temurin:21-jre-alpine

#Create app directory
WORKDIR /app

# Copy application JAR
COPY target/*.jar api-gateway.jar

#Expose application port
EXPOSE 8080

#Run the application
ENTRYPOINT ["java", "-jar", "api-gateway.jar"]
