FROM openjdk:8-jdk-alpine
ADD /life2-app/life2-app-person-index/target/person-index.jar person-index.jar
COPY external /data/external
ENTRYPOINT [ \
"java", \
"-jar", \
"-Dspring.data.mongodb.uri=mongodb://mongo/life2", \
"-Dspring.profiles.active=life2", \
"-Dspring.config.additional-location=/data/external/config/", \
"person-index.jar" \
]