## 1. build된 jar 파일이 있는 경우
#FROM eclipse-temurin:17-jre-alpine
#WORKDIR /app
#COPY build/libs/*.jar ./
#RUN mv $(ls | grep '.jar' | grep -v plain) app.jar
#ENTRYPOINT ["java", "-jar", "app.jar"]

## 2. build 후 jar 파일로 실행되게 수정(멀티 스테이징)
## 2-1. gradle 이미지로 build(jar 생성)
FROM gradle:8.5-jdk17-alpine AS build
WORKDIR /app

## 첫 번째 . : 내가 작성한 프로젝트, 두 번째 . : /app 즉 내가 작성한 프로젝트를 /appdp 에 모두 복사하라
COPY . .

## clean: 기존 빌드 파일을 지운다. --no-demon: 빌드 후 JVM이 꺼지게
## 즉 daemon 스레드를 쓰지 않게 하여 리소스 낭비 방지
## -x test: 테스트 코드 실행 없이
RUN gradle clean build --no-daemon -x test

## 2-2. 앞선 build라는 이름의 스테이지 겨로가로 실행 스테이지 시작
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

## AS build의 결과에서 /app/build/libs/*jar 파일들을 ./으로 복사
COPY --from=build /app/build/libs/*jar ./

RUN mv $(ls | grep '.jar' | grep -v plain) app.jar

## 컨테이너 내부에서 7777 포트에서 실행
ENTRYPOINT ["java", "-jar", "app.jar"]