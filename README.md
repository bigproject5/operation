# operation

- (윈도우) 어플리케이션 실행
```
.\gradlew.bat bootRun
```

- 회원가입
```
POST http://localhost:8080/api/operation/admin
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

- 로그인
```
POST http://localhost:8080/api/operation/admin/login
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
    "message": "1753518433470ID or password is not correct",
    "error": "Unauthorized"
}
```

- 토큰 테스트용 API
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
