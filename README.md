# 🌳 Groo 

## "감정을 기록하고, 정원으로 표현하는 감성 다이어리 서비스"

<br><br><br>

# 🚀 Team X1 🚀

| <img src="https://github.com/user-attachments/assets/c96cd9e0-dee6-4026-9086-7b2ffee3e56f" width="140"> | <img src="https://github.com/user-attachments/assets/c3df4899-1878-4180-a133-689254256ec8" width="140"> | <img src="https://github.com/user-attachments/assets/2e220b76-b0b4-4939-a277-dd1dfbf92db1" width="140"> | <img src="https://github.com/user-attachments/assets/8c603a5c-9c99-4d21-a353-e194298b1318" width="140"> | <img src="https://github.com/user-attachments/assets/47e0f3a1-b5e0-48c7-b63f-590db29cc4df" width="140"> | <img src="https://github.com/user-attachments/assets/8d5fd81a-89cb-42a1-a432-45a5ab59516c" width="140"> |
| :-----------------------------------------------------------------------------------------------------: | :-----------------------------------------------------------------------------------------------------: | :-----------------------------------------------------------------------------------------------------: | :-----------------------------------------------------------------------------------------------------: | :-----------------------------------------------------------------------------------------------------: | :------------------------------------------------------------------------------------------------------: |
| 황수민 | 신민경 | 이예원 | 조윤태 | 이준규 | 박지원 |
| [<img src="https://img.shields.io/badge/Github-Link-181717?logo=Github">](https://github.com/emily9949) | [<img src="https://img.shields.io/badge/Github-Link-181717?logo=Github">](https://github.com/mmmv41) | [<img src="https://img.shields.io/badge/Github-Link-181717?logo=Github">](https://github.com/oni128) | [<img src="https://img.shields.io/badge/Github-Link-181717?logo=Github">](https://github.com/cxzaqq) | [<img src="https://img.shields.io/badge/Github-Link-181717?logo=Github">](https://github.com/JK-LEE98) | [<img src="https://img.shields.io/badge/Github-Link-181717?logo=Github">](https://github.com/zi-won) |

<br><br>
# 🌱 기획 의도

우리는 하루에도 여러 감정을 겪지만, 그 감정들이 지나간 자리에는 종종 아무것도 남지 않습니다.
Groo는 바로 이 순간들을 놓치지 않고, 감정을 기록하고 표현할 수 있는 특별한 공간을 만들고자 시작되었습니다.

단순한 텍스트 일기장을 넘어, Groo는 감정을 시각적으로 표현합니다.
사용자가 일기를 작성하면, AI가 감정을 분석하고 그 결과에 따라 정원을 꾸밀 수 있는 자연 요소(꽃, 나무, 날씨 등)를 제공합니다.
이렇게 표현된 감정은 ‘하루치 마음’을 담은 작은 정원이 되고,
시간이 지날수록 하나의 개인적인 감정의 숲으로 확장되어 갑니다.

Groo는 위로를 주기 위한 도구라기보다는, 스스로의 감정과 마음 상태를 바라보고 표현하는 일상의 루틴이 되기를 지향합니다.
감정은 좋고 나쁨의 문제가 아니라, 존재 자체로 의미가 있다는 믿음 아래
슬픔, 기쁨, 불안, 설렘 등 다양한 감정을 있는 그대로 기록하고 남길 수 있도록 도와줍니다.

또한 사용자는 혼자만의 정원을 가꿀 수도 있고, 친구들과 함께 숲을 만들어 서로의 감정을 공유할 수도 있습니다.
이처럼 Groo는 “감정의 표현”과 “정서적 연결”, 두 가지를 중심으로 사용자에게 새로운 경험을 제공합니다.

단 한 줄의 일기라도 괜찮아요.
그날의 감정이 하나의 나무가 되어, 당신의 정원에 자랍니다. 🌱
# CICD

## 아키텍처

<details>
  <summary>아키텍처 보기</summary>

  ![image](https://github.com/user-attachments/assets/1e896b25-2f05-4c7d-92f9-442e9e02ae73)

</details>

## jenkins 파이프라인 코드

<details>
  <summary>코드 보기</summary>
  
  ```groovy
pipeline {
    agent any

    tools {
        gradle 'gradle'
        jdk 'openJDK17'
    }

    environment {
        GITHUB_URL = 'https://github.com/x1-company/be14-4th-x1-GROO-BE.git'
    }

    stages {
        stage('Preparation') {
            steps {
                script {
                    if (isUnix()) {
                        sh 'docker --version'
                    } else {
                        bat 'docker --version'
                    }
                }
            }
        }

        stage('Checkout & Inject Secrets') {
            steps {
                git branch: 'feature/roy/CICD', url: "${env.GITHUB_URL}"
                withCredentials([file(credentialsId: 'x1_groo_boot-yml', variable: 'APP_YML_PATH')]) {
                    script {
                        if (isUnix()) {
                            sh "mkdir -p ./src/main/resources"
                            sh "cp $APP_YML_PATH ./src/main/resources/application.yml"
                        } else {
                            bat 'if not exist src\\main\\resources mkdir src\\main\\resources'
                            bat 'copy %APP_YML_PATH% src\\main\\resources\\application.yml'
                        }
                    }
                }
            }
        }

        stage('Source Build') {
            steps {
                script {
                    if (isUnix()) {
                        sh "chmod +x ./gradlew"
                        sh "./gradlew clean build"
                    } else {
                        bat "gradlew.bat clean build"
                    }
                }
            }
        }

        stage('Container Build and Push') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'DOCKERHUB_PASSWORD', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                        if (isUnix()) {
                            sh "docker login -u ${DOCKER_USER} -p ${DOCKER_PASS}"
                            sh "docker build -t ${DOCKER_USER}/x1_groo_boot:latest ."
                            sh "docker push ${DOCKER_USER}/x1_groo_boot:latest"
                        } else {
                            bat "docker login -u %DOCKER_USER% -p %DOCKER_PASS%"
                            bat "docker build -t ${DOCKER_USER}/x1_groo_boot:latest ."
                            bat "docker push ${DOCKER_USER}/x1_groo_boot:latest"
                        }
                    }
                }
            }
        }

        stage('Run Container') {
            steps {
                script {
                    def containerName = "x1_groo_boot_container"
                    def imageName = "cxzaqq/x1_groo_boot:latest"

                    if (isUnix()) {
                        sh "docker ps -q --filter 'name=${containerName}' | grep -q . && docker rm -f ${containerName} || echo 'No existing container to remove'"
                        sh "docker run -d --name ${containerName} -p 8080:8080 ${imageName}"
                    } else {
                        bat """
                            FOR /F %%i IN ('docker ps -q --filter "name=${containerName}"') DO docker rm -f %%i
                            docker run -d --name ${containerName} -p 8080:8080 ${imageName}
                            docker ps
                            docker logs ${containerName}
                        """
                    }
                }
            }
        }
    }

    post {
        always {
            script {
                // application.yml 삭제
                if (isUnix()) {
                    sh 'rm -f ./src/main/resources/application.yml'
                    sh 'docker logout'
                } else {
                    bat 'del /F /Q src\\main\\resources\\application.yml'
                    bat 'docker logout'
                }
            }
        }
        success {
            echo 'Pipeline succeeded!'
        }
        failure {
            echo 'Pipeline failed!'
        }
    }
}
```
</details>

## 테스트 결과

<details>
  <summary>링크 보기</summary>

  https://ohgiraffers.notion.site/CICD-1e8649136c1180f78da6f0e62d73bb0b?pvs=73

</details>

## ERD
<details>
    <summary>ERD Cloud</summary>
<img width="1016" alt="KakaoTalk_Photo_2025-05-03-15-44-48" src="https://github.com/user-attachments/assets/d75dab37-700a-44cb-818a-0c66ac2146ea" />
</details>
<details>
    <summary>ERD 상세보기</summary>
  
```mermaid
erDiagram
    user {
        INT id PK
        VARCHAR email
        VARCHAR oauth_provider
        VARCHAR oauth_id
        VARCHAR password
        DATETIME created_at
        VARCHAR role
        DATETIME birth
        VARCHAR nickname
        BOOLEAN is_deleted
    }

    background {
        INT id PK
        VARCHAR name
        VARCHAR image_url
    }

    category {
        INT id PK
        VARCHAR category
    }

    item {
        INT id PK
        VARCHAR name
        VARCHAR image_url
        INT category_id FK
        VARCHAR emotion
    }

    forest {
        INT id PK
        VARCHAR name
        VARCHAR month
        BOOLEAN is_public
        INT background_id FK
        INT user_id FK
    }

    user_item {
        INT id PK
        INT item_id FK
        INT user_id FK
        INT total_count
        INT placed_count
        INT forest_id FK
    }

    shared_forest {
        INT id PK
        INT user_id FK
        INT forest_id FK
    }

    mailbox {
        INT id PK
        VARCHAR content
        DATETIME created_at
        BOOLEAN is_deleted
        INT user_id FK
        INT forest_id FK
    }

    announcement {
        INT id PK
        INT admin_id FK
        VARCHAR title
        TEXT content
        DATETIME created_at
    }

    diary {
        INT id PK
        DATETIME created_at
        DATETIME updated_at
        TEXT content
        BOOLEAN is_published
        INT user_id FK
        INT forest_id FK
        VARCHAR weather
    }

    diary_emotion {
        INT id PK
        INT weight
        INT diary_id FK
        VARCHAR emotion
    }

    placement {
        INT id PK
        DECIMAL position_x
        DECIMAL position_y
        INT user_id FK
        INT user_item_id FK
    }

    item }o--|| category : belongs_to
    forest }o--|| background : uses
    forest }o--|| user : owned_by
    user_item }o--|| user : owned_by
    user_item }o--|| item : contains
    user_item }o--|| forest : placed_in
    shared_forest }o--|| user : viewer
    shared_forest }o--|| forest : shared_from
    mailbox }o--|| user : written_by
    mailbox }o--|| forest : posted_in
    announcement }o--|| user : created_by
    diary }o--|| user : written_by
    diary }o--|| forest : related_to
    diary_emotion }o--|| diary : analyzed_from
    placement }o--|| user : placed_by
    placement }o--|| user_item : uses
```
</details>

# 개인 회고

<details>
  <summary>회고 보기</summary>

  |이름|회고|
  |------|---|
  |박지원|내용|
  |신민경|내용|
  |이예원|내용|
  |이준규|내용|
  |조윤태|내용|
  |황수민|내용|

</details>
