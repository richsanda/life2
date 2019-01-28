FROM openjdk:8-jdk-alpine
ADD /life2-app/life2-app-email/target/email.jar email.jar
COPY external /data/external
ENTRYPOINT [ \
"java", \
"-jar", \
"-Dspring.config.additional-location=/data/external/config/", \
"email.jar" \
]