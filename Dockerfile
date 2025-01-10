   # Use an official OpenJDK 21 runtime as a parent image
   FROM openjdk:21

   # Set the working directory in the container
   WORKDIR /app

   # Copy the current directory contents into the container at /app
   COPY target/sifan-home-transaction-management-1.0-SNAPSHOT.jar /app/sifan-home-transaction-management.jar

   # Make port 8080 available to the world outside this container
   EXPOSE 8080

   # Run the application
   ENTRYPOINT ["java", "-jar", "sifan-home-transaction-management.jar"]
