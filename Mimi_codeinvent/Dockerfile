FROM maven:3.10.2-eclipse-temurin-27-resolute

WORKDIR /app

COPY src .

RUN mvn -B -DskipTests package

CMD ["mvn", "test"]