#FROM openjdk:11
FROM gradle:5.4.1-jdk11

WORKDIR /

EXPOSE 8080

# Replace this!
ENV CLIENT_KEY $CLIENT_KEY

CMD [ "gradle", "run" ]

COPY . .

RUN gradle run