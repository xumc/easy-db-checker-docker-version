FROM openjdk:8
WORKDIR /app
ADD ./out/artifacts/easy_db_checker_docker_version_jar/easy-db-checker-docker-version.jar /app
CMD ["java", "-jar", "easy-db-checker-docker-version.jar"]