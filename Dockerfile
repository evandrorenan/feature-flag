FROM registry.access.redhat.com/ubi8/openjdk-17-runtime:1.20

# Add a name to the image
LABEL name="feature-flag"

# Copy the application JAR file to the container
COPY --chown=185:0 target/feature-flag-__VERSION__.jar /deployments/app.jar

# Expose the port the application runs on
EXPOSE 8080

# Set the default command to evaluateAllFeatureFlagsOfType the application
CMD ["java", "-jar", "/deployments/app.jar"]
