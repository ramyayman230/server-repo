# Use an appropriate base image that includes JDK
FROM openjdk:11-jdk-slim

# Create app directory
WORKDIR /app

# Copy server source code
COPY Server.java .

# Compile the server application
RUN javac Server.java

# Run the server application
CMD ["java", "Server"]



