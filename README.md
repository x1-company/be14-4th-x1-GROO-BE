# 기획 의도

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
<summary>ERD 상세보기</summary>
<img width="1016" alt="KakaoTalk_Photo_2025-05-03-15-44-48" src="https://github.com/user-attachments/assets/d75dab37-700a-44cb-818a-0c66ac2146ea" />

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
