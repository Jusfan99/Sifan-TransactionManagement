# Use an official OpenJDK 21 runtime as a parent image
FROM openjdk:21-jre-slim

# Set the working directory in the container
WORKDIR /app

# Copy the current directory contents into the container at /app
COPY target/bank-transaction-system-1.0-SNAPSHOT.jar /app/bank-transaction-system.jar

# Make port 8080 available to the world outside this container
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "bank-transaction-system.jar"]