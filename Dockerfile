FROM openjdk:17-jdk-slim

WORKDIR /app

# 사용자와 그룹 생성
RUN groupadd -r bongsan && useradd -r -g bongsan bongsan

# 권한 설정
RUN chown -R bongsan:bongsan /app

COPY build/libs/kefa-0.0.1-SNAPSHOT.jar app.jar

# JAR 파일 권한 설정
RUN chown bongsan:bongsan app.jar

EXPOSE 8080

# bongsan 사용자로 전환
USER bongsan

ENTRYPOINT ["java", "-jar", "app.jar"]