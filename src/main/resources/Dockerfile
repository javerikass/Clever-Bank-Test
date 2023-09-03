FROM adoptopenjdk:17-jdk-hotspot AS builder

WORKDIR /Clever-Bank

COPY . /Clever-Bank

RUN ./gradlew build

FROM adoptopenjdk:17-jdk-hotspot

WORKDIR /Clever-Bank

COPY --from=builder /Clever-Bank/build/libs/Clever-Bank-1.0-SNAPSHOT.war /Clever-Bank/Clever-Bank.war

CMD ["java", "-war", "/Clever-Bank/build/libs/Clever-Bank-1.0-SNAPSHOT.war"]

FROM postgres:14.1

ENV POSTGRES_USER postgres
ENV POSTGRES_PASSWORD pass
ENV POSTGRES_DB test

EXPOSE 5432

CMD ["postgres"]

COPY init.sql /docker-entrypoint-initdb.d/
