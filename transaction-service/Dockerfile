# Use the eclipse-temurin OpenJDK 11 image as the base image
FROM eclipse-temurin:21-jre-alpine

#Create app directory
WORKDIR /app

# Copy application JAR
COPY target/*.jar transaction-service.jar

#Expose application port
EXPOSE 8082

#Run the application
ENTRYPOINT ["java", "-jar", "transaction-service.jar"]
