# NOTE: this Dockerfile must be run from the root directory of the project "context" folder to work properly.
# But since this Dockerfile is in energy-sales-api, if you want just to run the api
# you must run the following command in the root directory of the project:
#
# docker build -t energysales-api -f src/energysales-api/Dockerfile .


# Use the OpenJDK 11 image as the base image
FROM openjdk:21

# Expose the application port
EXPOSE 8080

# Create application directories
RUN mkdir -p /app/energysales-spa

# Copy the JAR file into the Docker image
COPY ./src/energysales-api/build/libs/*-all.jar /app/energysales-api/energysales-api.jar

# Copy the React frontend build files to the appropriate directory
COPY ./src/energysales-spa/dist/ /app/energysales-spa/dist

# Set the entry point to run the JAR file
ENTRYPOINT ["java", "-jar", "/app/energysales-api/energysales-api.jar"]
