# CHECKAR :: Operation Service

## 1. 프로젝트 개요

본 프로젝트는 CHECKAR 서비스의 MSA 구조의 일부로, 사용자 인증 및 내부 운영 관리를 담당하는 백엔드 서비스입니다.

주요 기능으로는 관리자 및 작업자의 회원가입과 로그인, 공지사항 관리, 작업자 정보 관리가 있으며, 안전한 인증을 위해 JWT (JSON Web Token)를 사용하고 Google reCAPTCHA를 통해 보안을 강화합니다.

## 2. 주요 기능

- **사용자 인증**
  - 관리자/작업자 회원가입 및 로그인
  - ID 중복 확인
  - JWT 토큰 기반의 인증/인가 처리
  - Google reCAPTCHA를 이용한 봇 방지
- **공지사항 관리**
  - 공지사항 CRUD (생성, 조회, 수정, 삭제) 기능
  - 파일 첨부 기능 (AWS S3 연동)
- **작업자 관리**
  - 작업자 목록 조회
  - 작업자 정보 조회, 수정, 삭제

## 3. 기술 스택

- **언어**: Java 21
- **프레임워크**: Spring Boot 3.5.3
- **데이터베이스**: MySQL, H2
- **인증**: Spring Security, JWT (jjwt-api, jjwt-impl, jjwt-jackson)
- **데이터 액세스**: Spring Data JPA
- **API 문서화**: SpringDoc OpenAPI (Swagger UI)
- **파일 저장소**: AWS S3
- **빌드 도구**: Gradle
- **배포**: Docker, Kubernetes, Jenkins

## 4. API 명세

API는 인증이 필요한 경우 요청 헤더에 `Authorization: Bearer <JWT_TOKEN>`을 포함해야 합니다.

### 4.1. 인증 (Login & Signup)

| Method | URL                               | 설명                       |
| ------ | --------------------------------- | -------------------------- |
| `POST` | `/api/login`                      | 관리자/작업자 로그인       |
| `POST` | `/api/signup/admin`               | 관리자 회원가입            |
| `POST` | `/api/signup/admin/check`         | 관리자 ID 중복 확인        |
| `POST` | `/api/signup/worker`              | 작업자 회원가입            |

### 4.2. 공지사항 (Notice)

| Method | URL                               | 설명                       |
| ------ | --------------------------------- | -------------------------- |
| `POST` | `/api/notices`                    | 공지사항 생성 (파일 첨부 가능) |
| `GET`    | `/api/notices`                    | 전체 공지사항 목록 조회    |
| `GET`    | `/api/notices/{id}`               | 특정 공지사항 상세 조회    |
| `PUT`    | `/api/notices/{id}`               | 공지사항 수정              |
| `DELETE` | `/api/notices/{id}`               | 공지사항 삭제              |

### 4.3. 작업자 (Worker)

| Method | URL                               | 설명                       |
| ------ | --------------------------------- | -------------------------- |
| `GET`    | `/api/workers`                    | 전체 작업자 목록 조회      |
| `GET`    | `/api/workers/{id}`               | 특정 작업자 정보 조회      |
| `PUT`    | `/api/workers/{id}`               | 작업자 정보 수정           |
| `DELETE` | `/api/workers/{id}`               | 작업자 계정 삭제           |

## 5. 실행 방법

### 5.1. 사전 요구사항

- Java 21
- Gradle

### 5.2. 빌드 및 실행

1.  **프로젝트 클론**
    ```bash
    git clone <repository_url>
    cd operation
    ```

2.  **환경 변수 설정**
    애플리케이션을 실행하기 전에 다음 환경 변수를 설정해야 합니다. `.env` 파일을 사용하거나 실행 환경에 직접 변수를 주입하십시오.

    ```bash
    # 데이터베이스 연결 정보
    DB_URL=jdbc:mysql://<your_db_host>:<port>/operation
    DB_USERNAME=<your_db_username>
    DB_PASSWORD=<your_db_password>

    # JWT 시크릿 키
    JWT_SECRET=<your_jwt_secret_key>

    # AWS S3 설정
    S3_ACCESS_KEY=<your_s3_access_key>
    S3_SECRET_KEY=<your_s3_secret_key>
    S3_BUCKET=<your_s3_bucket_name>

    # Google reCAPTCHA 시크릿 키
    CAPTCHA_SECRET=<your_captcha_secret_key>
    ```

3.  **빌드**
    ```bash
    ./gradlew build
    ```

4.  **실행**
    ```bash
    java -jar build/libs/operation-0.0.1-SNAPSHOT.jar
    ```

## 6. 배포

- **CI/CD**: `Jenkinsfile`을 통해 Jenkins 파이프라인이 구성되어 있습니다.
- **Containerization**: `Dockerfile`을 사용하여 서비스의 Docker 이미지를 빌드합니다.
- **Orchestration**: `kubernetes/` 디렉토리의 `deployment.yaml`과 `service.yaml`을 통해 Kubernetes 클러스터에 배포할 수 있습니다.