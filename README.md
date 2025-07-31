# operation

- (윈도우) 어플리케이션 실행
```
.\gradlew.bat bootRun
```

- 토큰이 필요한 api는 gateway를 꼭 거쳐서 실행할 것
  - gateway port 8080
  - operation port 8081

## 회원가입
- 관리자
```
POST http://localhost:8080/api/operation/admin/signup
```
```json
{
    "loginId": "admin123",
    "password": "123",
    "name": "관리자",
    "email": "admin@example.com",
    "phoneNumber": "01012345678",
    "companyNumber": "123-45-67890",
    "address": "서울특별시 강남구",
    "adminCode": "ADM001"
}
```

- 작업자
```
POST http://localhost:8080/api/operation/workers/signup
```
```json
{
    "loginId": "worker123",
    "password": "123",
    "name": "작업자",
    "email": "admin@example.com",
    "phoneNumber": "01012345678",
    "companyNumber": "123-45-67890",
    "address": "서울특별시 강남구"
}
```
- 성공
```json
{
    "loginId": "admin123",
    "name": "관리자",
    "email": "admin@example.com",
    "phoneNumber": "01012345678",
    "companyNumber": "123-45-67890",
    "address": "서울특별시 강남구"
}
```
- 실패
    - 403   

#### httpie
- 관리자 회원가입
```bash
http POST http://localhost:8080/api/operation/admin/signup loginId=admin123 password=123 name=관리자 email=admin@example.com phoneNumber=01012345678 employeeNumber=123-45-67890 address="서울특별시 강남구" adminCode=ADM001
```
- 작업자 회원가입
```bash
http POST http://localhost:8080/api/operation/workers/signup loginId=worker password=123 name=작업자 email=admin@example.com phoneNumber=01012345678 employeeNumber=123-45-67890 address="서울특별시 강남구"
```
---

## 로그인

```
POST http://localhost:8080/api/operation/admin/login
```
```json
{
  "loginId": "admin123",
  "password": "123"
}
```
- 작업자
```
POST http://localhost:8080/api/operation/workers/login
```
```json
{
  "loginId": "admin123",
  "password": "123"
}
```
- 성공

```json
{
    "token": "token",
    "role": "ADMIN",
    "expiresIn": 3600
}
```
- 실패

```json
{
    "message": "ID or password is not correct",
    "error": "Unauthorized"
}
```
#### httpie
- 관리자 로그인
  - 아이디 최소 8자리
  - 비밀번호 최소 8자리
    - 특수문자 한개 이상 포함
```bash
http POST http://localhost:8080/api/operation/admin/login loginId=admin123 password=12345678!
```
- 작업자 로그인
```bash
http POST http://localhost:8080/api/operation/workers/login loginId=worker123 password=12345678!
```
---

## 토큰 테스트용 API
- 테스트는 gateway를 거쳐야 함


- 관리자 토큰 테스트용 API
```
POST http://localhost:8080/api/operation/admin/test
```
```
Authorization:"Bearer <JWT-토큰>"
```
```json
{
    "message": "데이터 요청 A"
}
```

- 작업자 토큰 테스트용 API
```
POST http://localhost:8080/api/operation/workers/test
```
```
Authorization:"Bearer <JWT-토큰>"
```
```json
{
    "message": "데이터 요청 A"
}
```

- 성공

```json
{
    "code": 200,
    "message": "Token is valid",
    "request": "데이터 요청 A",
    "id": 1,
    "role": "ADMIN"
}
```
- 실패
    - 403

#### httpie
- 관리자 인가 테스트
```bash
http POST http://localhost:8080/api/operation/admin/test Authorization:"Bearer {token}" Content-Type:application/json message="요청 1"
```
- 작업자 인가 테스트
```bash
http POST http://localhost:8080/api/operation/workers/test Authorization:"Bearer {token}" Content-Type:application/json message="요청 1"
```

## 작업자

- 목록 조회
```
GET :8080/api/operation/workers
```

- 단일 조회

```
GET :8080/api/operation/workers/id
```

- 작업자 개인 조회
```
GET :8080/api/operation/workers/profile
```

- 작업자 삭제
```
DELETE :8080/api/operation/workers/id
```

- 작업 기준 작업자 조회
```
GET :8080/api/operation/workers/task
```

### TODO
  - 작업자 정보 수정

## 공지

- 공지 목록 조회
```
GET :8080/api/operation/notices
```

- 공지 생성
  - 토큰 필요
```
POST :8080/api/operation/notices
```

```json
{
  "title": "title",
  "content": "content",
  "fileUrl": "fileUrl"
}
```


- 공지 수정
```
PUT :8080/api/operation/notices/id
```

- 공지 제목 검색
```
GET :8080/api/operation/notices/search/adminId?keyword=""
```

- 공지 작성자 조회
```
GET :8080/api/operation/notices/search/adminId?keyword=""
```

- 공지 삭제
```
DELETE :8080/api/operation/notices/id
```

- 서버 체크용 api
```
GET :8080/api/operation/notices/health
```