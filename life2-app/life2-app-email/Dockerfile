FROM openjdk:8-jdk-alpine
ARG profile
ARG port
EXPOSE $port
ADD /life2-app/life2-app-email/target/email.jar email.jar
COPY external /data/external
ENTRYPOINT [ \
"java", \
"-jar", \
"-Dspring.data.mongodb.uri=mongodb://mongo/$profile", \
"-Dspring.profiles.active=$profile", \
"-Dspring.config.additional-location=/data/external/config/", \
"email.jar" \
]