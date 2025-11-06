FROM openjdk:26-ea-trixie
ADD target/movieticket.jar movieticket.jar
ENTRYPOINT ["java", "-jar","/movieticket.jar"]