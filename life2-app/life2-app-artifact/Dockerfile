FROM openjdk:8-jdk-alpine
EXPOSE 11000
ADD /life2-app/life2-app-artifact/target/life2.jar life2.jar
COPY external /data/external
ENTRYPOINT [ \
"java", \
"-jar", \
"-Dspring.profiles.active=life2", \
"-Dspring.config.additional-location=/data/external/config/", \
"life2.jar" \
]