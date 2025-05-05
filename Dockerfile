# Stage 1: Build the application
FROM maven:3.9.6-eclipse-temurin-17-focal AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Add metadata
LABEL maintainer="Vinod Kumar Kayartaya <vinod@vinod.co>"
LABEL description="Online Groovy Editor - A web-based Groovy code editor and runner"
LABEL version="1.0"

# Expose the port the app runs on
EXPOSE 8080

# Command to run the application
CMD ["java", "-jar", "app.jar"] 