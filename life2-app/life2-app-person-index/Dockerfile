FROM openjdk:8-jdk-alpine
ADD /life2-app/life2-app-person-index/target/person-index.jar person-index.jar
COPY external /data/external
ENTRYPOINT [ \
"java", \
"-jar", \
"-Dlife2.data=/data/life2/data", \
"-Dlife2.mongo.host=mongo", \
"person-index.jar" \
]