FROM openjdk:8-jdk-alpine
ADD /life2-app/life2-app-email/target/email.jar email.jar
COPY external /data/external
ENTRYPOINT [ \
"java", \
"-jar", \
"-Dlife2.data=/data/life2/data", \
"-Dlife2.mongo.host=mongo", \
"-Dspring.config.additional-location=/data/external/config/", \
"email.jar" \
]