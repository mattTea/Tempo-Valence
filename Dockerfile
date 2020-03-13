FROM gradle:5.4.1-jdk11 AS build
COPY --chown=gradle:gradle . /home/gradle/src
ENV CLIENT_KEY $CLIENT_KEY
WORKDIR /home/gradle/src

#RUN ./gradlew --no-daemon shadowJar
RUN gradle build --no-daemon shadowJar

FROM openjdk:11

RUN mkdir /app

EXPOSE 8080

#COPY --from=build /home/gradle/src/build/libs/ /app/
COPY --from=build /home/gradle/src/out/artifacts/tempo_valence_jar/ /app/

ENTRYPOINT ["java","-jar","/app/tempo-valence.jar"]


#FROM openjdk:11
#FROM gradle:5.4.1-jdk11
#
#WORKDIR /
#
#EXPOSE 8080
#
## Replace this!
#ENV CLIENT_KEY $CLIENT_KEY

#CMD [ "gradle", "run" ]

#COPY . .

#RUN gradle run