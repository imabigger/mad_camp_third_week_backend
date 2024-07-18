# Start with a base image containing Java runtime
FROM openjdk:21-jdk-slim

# Set the working directory inside the container
WORKDIR /usr/src/app

# Add a volume pointing to /tmp
VOLUME /tmp

# Make port 3002 available to the world outside this container
EXPOSE 3000

# The application's jar file
ARG JAR_FILE=build/libs/*.jar

# Add the application's jar to the container
COPY ${JAR_FILE} app.jar

# Copy the .env file and other required files to the container
COPY . .

# Run the jar file
ENTRYPOINT ["java", "-jar", "/usr/src/app/app.jar"]
